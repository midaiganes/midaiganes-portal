package ee.midaiganes.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.Layout;
import ee.midaiganes.model.LayoutTitle;
import ee.midaiganes.model.PageLayoutName;
import ee.midaiganes.model.Theme;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.dao.LayoutDao;
import ee.midaiganes.services.exceptions.IllegalFriendlyUrlException;
import ee.midaiganes.services.exceptions.IllegalLanguageIdException;
import ee.midaiganes.services.exceptions.IllegalPageLayoutException;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

@Resource(name = PortalConfig.LAYOUT_REPOSITORY)
public class LayoutRepository {
	private static final Logger log = LoggerFactory.getLogger(LayoutRepository.class);
	private static final Pattern FRIENDLY_URL_PATTERN;
	static {
		FRIENDLY_URL_PATTERN = Pattern.compile("^\\/[a-zA-Z0-9_\\-]*$");
	}
	private final LayoutDao layoutDao;
	private final ThemeRepository themeRepository;
	private final PageLayoutRepository pageLayoutRepository;

	private final Cache cache;
	private final Cache layoutTitleCache;
	private final Cache layoutCache;

	public LayoutRepository(LayoutDao layoutDao, ThemeRepository themeRepository, PageLayoutRepository pageLayoutRepository) {
		this.layoutDao = layoutDao;
		this.themeRepository = themeRepository;
		this.pageLayoutRepository = pageLayoutRepository;
		this.cache = SingleVmPool.getCache(LayoutRepository.class.getName());
		this.layoutTitleCache = SingleVmPool.getCache(LayoutRepository.class.getName() + ".LayoutTitle");
		this.layoutCache = SingleVmPool.getCache(LayoutRepository.class.getName() + ".Layout");
	}

	public List<LayoutTitle> getLayoutTitles(long layoutId) {
		List<LayoutTitle> list = getLayoutTitlesFromCache(layoutId);
		if (list == null) {
			return loadAndCacheLayoutTitles(layoutId);
		}
		return list;
	}

	private List<LayoutTitle> getLayoutTitlesFromCache(long layoutId) {
		Element el = layoutTitleCache.getElement(Long.toString(layoutId));
		return el != null ? el.<List<LayoutTitle>> get() : null;
	}

	private List<LayoutTitle> loadAndCacheLayoutTitles(long layoutId) {
		List<LayoutTitle> list = null;
		try {
			list = layoutDao.loadLayoutTitles(layoutId);
		} finally {
			list = list == null ? Collections.<LayoutTitle> emptyList() : list;
			layoutTitleCache.put(Long.toString(layoutId), list);
		}
		return list;
	}

	public Layout getLayout(long layoutId) {
		Element el = layoutCache.getElement(Long.toString(layoutId));
		if (el != null) {
			return el.get();
		}
		Layout layout = null;
		try {
			layout = layoutDao.loadLayout(layoutId);
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
			layouts = Collections.unmodifiableList(layoutDao.loadLayouts(layoutSetId));
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
			return layoutDao.addLayout(layoutSetId, friendlyUrl, themeName, pageLayoutName, parentId, defaultLayoutTitleLanguageId);
		} finally {
			cache.clear();
		}
	}

	public void updateLayout(String friendlyUrl, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId, long id)
			throws IllegalFriendlyUrlException, IllegalLanguageIdException, IllegalPageLayoutException {
		validateLayoutData(friendlyUrl, pageLayoutName);
		try {
			layoutDao.updateLayout(friendlyUrl, pageLayoutName, parentId, defaultLayoutTitleLanguageId, id);
		} finally {
			cache.clear();
			layoutCache.remove(Long.toString(id));
		}
	}

	public void addLayoutTitle(long layoutId, long languageId, String title) {
		try {
			layoutDao.addLayoutTitle(layoutId, languageId, title);
		} finally {
			layoutCache.remove(Long.toString(layoutId));
			layoutTitleCache.remove(Long.toString(layoutId));
		}
	}

	public void updateLayoutTitle(long layoutId, long languageId, String title) {
		try {
			layoutDao.updateLayoutTitle(layoutId, languageId, title);
		} finally {
			layoutTitleCache.remove(Long.toString(layoutId));
			layoutCache.remove(Long.toString(layoutId));
		}
	}

	public void deleteLayoutTitle(long layoutId, long languageId) {
		try {
			layoutDao.deleteLayoutTitle(layoutId, languageId);
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
			layoutDao.updatePageLayout(layoutId, pageLayoutName);
		} finally {
			layoutCache.remove(Long.toString(layoutId));
		}
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, readOnly = false, value = PortalConfig.TXMANAGER)
	public void deleteLayout(long layoutId) {
		try {
			Layout layout = getLayout(layoutId);
			int deleted = layoutDao.deleteLayout(layoutId);
			int updated = layoutDao.moveLayoutsUp(layout.getLayoutSetId(), layout.getParentId(), layout.getNr());
			log.debug("Deleted {} and updated {} layout(s)", Integer.valueOf(deleted), Integer.valueOf(updated));
		} finally {
			cache.clear();
			layoutTitleCache.remove(Long.toString(layoutId));
			layoutCache.remove(Long.toString(layoutId));
		}
	}

	public boolean moveLayoutUp(long layoutId) {
		try {
			return layoutDao.moveLayoutUp(layoutId) == 2;
		} finally {
			cache.clear();
			layoutCache.clear();
		}
	}

	public boolean moveLayoutDown(long layoutId) {
		try {
			return layoutDao.moveLayoutDown(layoutId) == 2;
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
		log.warn("get default layout; layoutsetid = {}; friendlyUrl = {}", Long.valueOf(layoutSetId), friendlyUrl);
		Theme defaultTheme = themeRepository.getDefaultTheme();
		String pageLayoutId = pageLayoutRepository.getDefaultPageLayout().getPageLayoutName().getFullName();
		return Layout.getDefault(layoutSetId, friendlyUrl, defaultTheme.getThemeName(), pageLayoutId);
	}

	public boolean isFriendlyUrlValid(String friendlyUrl) {
		return !StringUtil.isEmpty(friendlyUrl) && friendlyUrl.startsWith(StringPool.SLASH) && FRIENDLY_URL_PATTERN.matcher(friendlyUrl).matches();
	}
}
