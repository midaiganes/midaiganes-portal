package ee.midaiganes.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

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
import ee.midaiganes.services.rowmapper.LayoutRowMapper;
import ee.midaiganes.services.rowmapper.LayoutTitleRowMapper;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

public class LayoutRepository {
	private static final Logger log = LoggerFactory.getLogger(LayoutRepository.class);
	private static final String GET_LAYOUT_BY_LAYOUTSETID = "SELECT Layout.id, Layout.layoutSetId, Layout.friendlyUrl, Layout.pageLayoutId, Theme.name, Theme.context FROM Layout LEFT JOIN Theme ON (Layout.themeId = Theme.id) WHERE layoutSetId = ?";
	private static final String ADD_LAYOUT = "INSERT INTO Layout(layoutSetId, friendlyUrl, themeId, pageLayoutId, parentId, nr, defaultLayoutTitleLanguageId) VALUES(?, ?, (SELECT id FROM Theme WHERE name = ? AND context = ?), ?, ?, (SELECT c FROM (SELECT COUNT(1) AS c FROM Layout WHERE layoutSetId = ? AND parentId = ?) AS t), ?)";
	private static final String DELETE_LAYOUT = "DELETE FROM Layout WHERE id = ?";
	private static final String GET_LAYOUT_TITLES = "SELECT LayoutTitle.id, LayoutTitle.languageId, LayoutTitle.layoutId, LayoutTitle.title FROM LayoutTitle";
	private static final String UPDATE_PAGE_LAYOUT = "UPDATE Layout SET pageLayoutId = ? WHERE id = ?";
	private static final Pattern FRIENDLY_URL_PATTERN = Pattern.compile("^\\/[a-zA-Z0-9_\\-]+$");
	private static final LayoutRowMapper layoutRowMapper = new LayoutRowMapper();
	private static final LayoutTitleRowMapper layoutTitleRowMapper = new LayoutTitleRowMapper();
	private static final String[] ID_ARRAY = { StringPool.ID };

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource(name = PortalConfig.LANGUAGE_REPOSITORY)
	private LanguageRepository languageRepository;

	@Resource(name = PortalConfig.THEME_REPOSITORY)
	private ThemeRepository themeRepository;

	@Resource
	private PageLayoutRepository pageLayoutRepository;

	private final Cache cache;

	public LayoutRepository() {
		cache = SingleVmPool.getCache(LayoutRepository.class.getName());
	}

	public List<LayoutTitle> getLayoutTitles() {
		return jdbcTemplate.query(GET_LAYOUT_TITLES, layoutTitleRowMapper);
	}

	public List<Layout> getLayouts(long layoutSetId) {
		String cacheKey = Long.toString(layoutSetId);
		Element el = cache.getElement(cacheKey);
		if (el != null) {
			return el.get();
		}
		List<Layout> layouts = Collections.unmodifiableList(jdbcTemplate.query(GET_LAYOUT_BY_LAYOUTSETID, layoutRowMapper, layoutSetId));
		cache.put(cacheKey, layouts);
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

	public long addLayout(final long layoutSetId, final String friendlyUrl, final ThemeName themeName, final PageLayoutName pageLayoutName,
			final Long parentId, final String defaultLayoutTitleLanguageId) throws IllegalFriendlyUrlException, IllegalLanguageIdException,
			IllegalPageLayoutException {
		validateLayoutData(friendlyUrl, pageLayoutName, defaultLayoutTitleLanguageId);
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(ADD_LAYOUT, ID_ARRAY);
					ps.setLong(1, layoutSetId);
					ps.setString(2, friendlyUrl);
					if (themeName != null) {
						ps.setString(3, themeName.getName());
						ps.setString(4, themeName.getContext());
					} else {
						ps.setNull(3, Types.VARCHAR);
						ps.setNull(4, Types.VARCHAR);
					}
					ps.setString(5, pageLayoutName.getFullName());
					if (parentId != null) {
						ps.setLong(6, parentId);
						ps.setLong(8, parentId);
					} else {
						ps.setNull(6, Types.INTEGER);
						ps.setNull(8, Types.INTEGER);
					}
					ps.setLong(7, layoutSetId);
					ps.setString(9, defaultLayoutTitleLanguageId);
					return ps;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} finally {
			cache.clear();
		}
	}

	private void validateLayoutData(final String friendlyUrl, final PageLayoutName pageLayoutName, final String defaultLayoutTitleLanguageId)
			throws IllegalFriendlyUrlException, IllegalLanguageIdException, IllegalPageLayoutException {
		if (!isFriendlyUrlValid(friendlyUrl)) {
			throw new IllegalFriendlyUrlException(friendlyUrl);
		}
		if (!languageRepository.isLanguageIdSupported(defaultLayoutTitleLanguageId)) {
			throw new IllegalLanguageIdException(defaultLayoutTitleLanguageId);
		}
		if (pageLayoutName == null) {
			throw new IllegalPageLayoutException("pageLayoutId is null");
		}
	}

	public void updatePageLayout(long layoutId, PageLayoutName pageLayoutName) {
		try {
			jdbcTemplate.update(UPDATE_PAGE_LAYOUT, pageLayoutName.getFullName(), layoutId);
		} finally {
			cache.clear();
		}
	}

	public void deleteLayout(long layoutId) {
		try {
			jdbcTemplate.update(DELETE_LAYOUT, layoutId);
		} finally {
			cache.clear();
		}
	}

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
