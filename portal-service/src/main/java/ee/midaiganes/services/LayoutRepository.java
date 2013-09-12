package ee.midaiganes.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.DefaultLayout;
import ee.midaiganes.model.Layout;
import ee.midaiganes.model.LayoutTitle;
import ee.midaiganes.model.PageLayoutName;
import ee.midaiganes.model.Theme;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.exceptions.IllegalFriendlyUrlException;
import ee.midaiganes.services.exceptions.IllegalLanguageIdException;
import ee.midaiganes.services.exceptions.IllegalPageLayoutException;
import ee.midaiganes.services.rowmapper.LayoutResultSetExtractor;
import ee.midaiganes.services.rowmapper.LayoutRowMapper;
import ee.midaiganes.services.rowmapper.LayoutTitleRowMapper;
import ee.midaiganes.services.statementcreator.AddLayoutPreparedStatementCreator;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

@Component(value = PortalConfig.LAYOUT_REPOSITORY)
public class LayoutRepository {
	private static final Logger log = LoggerFactory.getLogger(LayoutRepository.class);
	private static final String QRY_GET_LAYOUT_BY_LAYOUTSETID = "SELECT Layout.id, Layout.layoutSetId, Layout.friendlyUrl, Layout.pageLayoutId, Theme.name, Theme.context, Layout.parentId, Layout.nr, Layout.defaultLayoutTitleLanguageId FROM Layout LEFT JOIN Theme ON (Layout.themeId = Theme.id) WHERE layoutSetId = ?";
	private static final String QRY_DELETE_LAYOUT = "DELETE FROM Layout WHERE id = ?";
	private static final String QRY_GET_LAYOUT_TITLES = "SELECT LayoutTitle.id, LayoutTitle.languageId, LayoutTitle.layoutId, LayoutTitle.title FROM LayoutTitle WHERE LayoutTitle.layoutId = ?";
	private static final String QRY_GET_LAYOUT_BY_ID = "SELECT Layout.id, Layout.layoutSetId, Layout.friendlyUrl, Layout.pageLayoutId, Theme.name, Theme.context, Layout.parentId, Layout.nr, Layout.defaultLayoutTitleLanguageId FROM Layout LEFT JOIN Theme ON (Layout.themeId = Theme.id) WHERE Layout.id = ?";
	private static final String QRY_UPDATE_PAGE_LAYOUT = "UPDATE Layout SET pageLayoutId = ? WHERE id = ?";
	private static final String QRY_UPDATE_LAYOUT = "UPDATE Layout SET friendlyUrl = ?, pageLayoutId = ?, parentId = ?, defaultLayoutTitleLanguageId = ? WHERE id = ?";
	private static final String QRY_MOVE_LAYOUT_UP = "UPDATE Layout AS l1 JOIN Layout AS l2 ON (l1.id = ? AND (l2.parentId = l1.parentId or (l2.parentId is null and l1.parentId is null)) and l2.nr = l1.nr - 1) SET l1.nr = l2.nr, l2.nr = l1.nr";
	private static final String QRY_MOVE_LAYOUT_DOWN = "UPDATE Layout AS l1 JOIN Layout AS l2 ON (l1.id = ? AND (l2.parentId = l1.parentId or (l2.parentId is null and l1.parentId is null)) and l2.nr = l1.nr + 1) SET l1.nr = l2.nr, l2.nr = l1.nr";
	private static final Pattern FRIENDLY_URL_PATTERN = Pattern.compile("^\\/[a-zA-Z0-9_\\-]*$");
	private static final LayoutRowMapper layoutRowMapper = new LayoutRowMapper();
	private static final LayoutTitleRowMapper layoutTitleRowMapper = new LayoutTitleRowMapper();
	private static final LayoutResultSetExtractor layoutResultSetExtractor = new LayoutResultSetExtractor();

	@Resource(name = PortalConfig.PORTAL_JDBC_TEMPLATE)
	private JdbcTemplate jdbcTemplate;

	@Resource(name = PortalConfig.LANGUAGE_REPOSITORY)
	private LanguageRepository languageRepository;

	@Resource(name = PortalConfig.THEME_REPOSITORY)
	private ThemeRepository themeRepository;

	@Resource(name = PortalConfig.PAGE_LAYOUT_REPOSITORY)
	private PageLayoutRepository pageLayoutRepository;

	private final Cache cache;
	private final Cache layoutTitleCache;
	private final Cache layoutCache;

	public LayoutRepository() {
		cache = SingleVmPool.getCache(LayoutRepository.class.getName());
		layoutTitleCache = SingleVmPool.getCache(LayoutRepository.class.getName() + ".LayoutTitle");
		layoutCache = SingleVmPool.getCache(LayoutRepository.class.getName() + ".Layout");
	}

	public List<LayoutTitle> getLayoutTitles(long layoutId) {
		List<LayoutTitle> list = getLayoutTitlesFromCache(layoutId);
		if (list == null) {
			return queryAndCacheLayoutTitles(layoutId);
		}
		return list;
	}

	private List<LayoutTitle> getLayoutTitlesFromCache(long layoutId) {
		Element el = layoutTitleCache.getElement(Long.toString(layoutId));
		return el != null ? el.<List<LayoutTitle>> get() : null;
	}

	private List<LayoutTitle> queryAndCacheLayoutTitles(long layoutId) {
		List<LayoutTitle> list = null;
		try {
			list = queryLayoutTitles(layoutId);
		} finally {
			list = list == null ? Collections.<LayoutTitle> emptyList() : list;
			layoutTitleCache.put(Long.toString(layoutId), list);
		}
		return list;
	}

	private List<LayoutTitle> queryLayoutTitles(long layoutId) {
		return jdbcTemplate.query(QRY_GET_LAYOUT_TITLES, layoutTitleRowMapper, layoutId);
	}

	public Layout getLayout(long layoutId) {
		Element el = layoutCache.getElement(Long.toString(layoutId));
		if (el != null) {
			return el.get();
		}
		Layout layout = null;
		try {
			layout = jdbcTemplate.query(QRY_GET_LAYOUT_BY_ID, layoutResultSetExtractor, layoutId);
			if (layout != null) {
				layout.setLayoutTitles(getLayoutTitles(layoutId));
			}
		} finally {
			layoutCache.put(Long.toString(layoutId), layout);
		}
		return layout;
	}

	public List<Layout> getLayouts(long[] layoutIds) {
		if (layoutIds != null) {
			List<Layout> list = new ArrayList<>(layoutIds.length);
			for (long id : layoutIds) {
				list.add(getLayout(id));
			}
			return list;
		}
		return Collections.emptyList();
	}

	public List<Layout> getLayouts(long layoutSetId) {
		String cacheKey = Long.toString(layoutSetId);
		Element el = cache.getElement(cacheKey);
		if (el != null) {
			return getLayouts(el.<long[]> get());
		}
		List<Layout> layouts = null;
		try {
			layouts = Collections.unmodifiableList(jdbcTemplate.query(QRY_GET_LAYOUT_BY_LAYOUTSETID, layoutRowMapper, layoutSetId));
			long[] layoutIds = new long[layouts.size()];
			int i = 0;
			for (Layout layout : layouts) {
				layout.setLayoutTitles(getLayoutTitles(layout.getId()));
				layoutCache.put(Long.toString(layout.getId()), layout);
				layoutIds[i++] = layout.getId();
			}
			cache.put(cacheKey, layoutIds);
		} catch (RuntimeException e) {
			cache.put(cacheKey, null);
		} finally {
			layouts = layouts == null ? Collections.<Layout> emptyList() : layouts;
		}

		return layouts;
	}

	public List<Layout> getChildLayouts(long layoutSetId, Long parentId) {
		List<Layout> layouts = new ArrayList<>();
		for (Layout layout : getLayouts(layoutSetId)) {
			Long layoutParentId = layout.getParentId();
			if (parentId == null && layoutParentId == null || parentId.longValue() == layoutParentId.longValue()) {
				layouts.add(layout);
			}
		}
		Collections.sort(layouts, new Comparator<Layout>() {

			@Override
			public int compare(Layout o1, Layout o2) {
				return Long.compare(o1.getNr(), o2.getNr());
			}
		});
		return layouts;
	}

	public Layout getLayout(long layoutSetId, String friendlyUrl) {
		for (Layout layout : getLayouts(layoutSetId)) {
			if (layout.getFriendlyUrl().equals(friendlyUrl)) {
				return layout;
			}
		}
		return null;
	}

	public long addLayout(long layoutSetId, String friendlyUrl, ThemeName themeName, PageLayoutName pageLayoutName, Long parentId,
			long defaultLayoutTitleLanguageId) throws IllegalFriendlyUrlException, IllegalLanguageIdException, IllegalPageLayoutException {
		validateLayoutData(friendlyUrl, pageLayoutName);
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new AddLayoutPreparedStatementCreator(layoutSetId, friendlyUrl, themeName, pageLayoutName, parentId,
					defaultLayoutTitleLanguageId), keyHolder);
			return keyHolder.getKey().longValue();
		} finally {
			cache.clear();
		}
	}

	public void updateLayout(String friendlyUrl, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId, long id)
			throws IllegalFriendlyUrlException, IllegalLanguageIdException, IllegalPageLayoutException {
		validateLayoutData(friendlyUrl, pageLayoutName);
		try {
			jdbcTemplate.update(QRY_UPDATE_LAYOUT, friendlyUrl, pageLayoutName.getFullName(), parentId, defaultLayoutTitleLanguageId, id);
		} finally {
			cache.clear();
			layoutCache.remove(Long.toString(id));
		}
	}

	public void addLayoutTitle(long layoutId, long languageId, String title) {
		try {
			jdbcTemplate.update("INSERT INTO LayoutTitle(layoutId, languageId, title) VALUES(?, ?, ?)", layoutId, languageId, title);
		} finally {
			layoutCache.remove(Long.toString(layoutId));
			layoutTitleCache.remove(Long.toString(layoutId));
		}
	}

	public void updateLayoutTitle(long layoutId, long languageId, String title) {
		try {
			jdbcTemplate.update("UPDATE LayoutTitle SET languageId = ?, title = ? WHERE layoutId = ?", languageId, title, layoutId);
		} finally {
			layoutTitleCache.remove(Long.toString(layoutId));
			layoutCache.remove(Long.toString(layoutId));
		}
	}

	public void deleteLayoutTitle(long layoutId, long languageId) {
		try {
			jdbcTemplate.update("DELETE LayoutTitle WHERE languageId = ? AND layoutId = ?", languageId, layoutId);
		} finally {
			layoutTitleCache.remove(Long.toString(layoutId));
			layoutCache.remove(Long.toString(layoutId));
		}
	}

	private void validateLayoutData(final String friendlyUrl, final PageLayoutName pageLayoutName) throws IllegalFriendlyUrlException,
			IllegalPageLayoutException {
		if (!isFriendlyUrlValid(friendlyUrl)) {
			throw new IllegalFriendlyUrlException(friendlyUrl);
		}
		if (pageLayoutName == null) {
			throw new IllegalPageLayoutException("pageLayoutId is null");
		}
	}

	public void updatePageLayout(long layoutId, PageLayoutName pageLayoutName) {
		try {
			jdbcTemplate.update(QRY_UPDATE_PAGE_LAYOUT, pageLayoutName.getFullName(), layoutId);
		} finally {
			layoutCache.remove(Long.toString(layoutId));
		}
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, readOnly = false, value = PortalConfig.TXMANAGER)
	public void deleteLayout(long layoutId) {
		try {
			Layout layout = getLayout(layoutId);
			int deleted = jdbcTemplate.update(QRY_DELETE_LAYOUT, layoutId);
			int updated = moveLayoutsUp(layout.getLayoutSetId(), layout.getParentId(), layout.getNr());
			log.debug("Deleted {} and updated {} layout(s)", deleted, updated);
		} finally {
			cache.clear();
			layoutTitleCache.remove(Long.toString(layoutId));
			layoutCache.remove(Long.toString(layoutId));
		}
	}

	private int moveLayoutsUp(long layoutSetId, Long parentId, long nr) {
		if (parentId == null) {
			return jdbcTemplate.update("UPDATE Layout set nr = nr - 1 WHERE layoutSetId = ? AND parentId IS NULL AND nr > ?", layoutSetId, nr);
		}
		return jdbcTemplate.update("UPDATE Layout set nr = nr - 1 WHERE layoutSetId = ? AND parentId = ? AND nr > ?", layoutSetId, parentId, nr);
	}

	public boolean moveLayoutUp(long layoutId) {
		try {
			return jdbcTemplate.update(QRY_MOVE_LAYOUT_UP, layoutId) == 2;
		} finally {
			cache.clear();
			layoutCache.clear();
		}
	}

	public boolean moveLayoutDown(long layoutId) {
		try {
			return jdbcTemplate.update(QRY_MOVE_LAYOUT_DOWN, layoutId) == 2;
		} finally {
			cache.clear();
			layoutCache.clear();
		}
	}

	/**
	 * TODO remove me
	 */
	@Deprecated
	public Layout getDefaultLayout(long layoutSetId, String friendlyUrl) {
		log.warn("get default layout; layoutsetid = {}; friendlyUrl = {}", layoutSetId, friendlyUrl);
		Theme defaultTheme = themeRepository.getDefaultTheme();
		String pageLayoutId = pageLayoutRepository.getDefaultPageLayout().getPageLayoutName().getFullName();
		return new DefaultLayout(layoutSetId, friendlyUrl, defaultTheme.getThemeName(), pageLayoutId);
	}

	public boolean isFriendlyUrlValid(String friendlyUrl) {
		return !StringUtil.isEmpty(friendlyUrl) && friendlyUrl.startsWith(StringPool.SLASH) && FRIENDLY_URL_PATTERN.matcher(friendlyUrl).matches();
	}
}
