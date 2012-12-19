package ee.midaiganes.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import org.springframework.jdbc.core.RowMapper;
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
import ee.midaiganes.util.StringUtil;

public class LayoutRepository {
	private static final Logger log = LoggerFactory.getLogger(LayoutRepository.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource(name = PortalConfig.LANGUAGE_REPOSITORY)
	private LanguageRepository languageRepository;

	@Resource(name = PortalConfig.THEME_REPOSITORY)
	private ThemeRepository themeRepository;

	@Resource
	private PageLayoutRepository pageLayoutRepository;

	private static final String GET_LAYOUT_BY_LAYOUTSETID = "SELECT Layout.id, Layout.layoutSetId, Layout.friendlyUrl, Layout.pageLayoutId, Theme.name, Theme.context FROM Layout LEFT JOIN Theme ON (Layout.themeId = Theme.id) WHERE layoutSetId = ?";
	private static final String ADD_LAYOUT = "INSERT INTO Layout(layoutSetId, friendlyUrl, themeId, pageLayoutId, parentId, nr, defaultLayoutTitleLanguageId) VALUES(?, ?, (SELECT id FROM Theme WHERE name = ? AND context = ?), ?, ?, (SELECT c FROM (SELECT COUNT(1) AS c FROM Layout WHERE layoutSetId = ? AND parentId = ?) AS t), ?)";
	private static final String DELETE_LAYOUT = "DELETE FROM Layout WHERE id = ?";
	private static final Pattern FRIENDLY_URL_PATTERN = Pattern.compile("^\\/[a-zA-Z0-9_\\-]+$");

	private final Cache cache;

	public LayoutRepository() {
		cache = SingleVmPool.getCache(LayoutRepository.class.getName());
	}

	private static final RowMapper<Layout> layoutRowMapper = new RowMapper<Layout>() {
		@Override
		public Layout mapRow(ResultSet rs, int rowNum) throws SQLException {
			Layout layout = new Layout();
			layout.setId(rs.getLong(1));
			layout.setLayoutSetId(rs.getLong(2));
			layout.setFriendlyUrl(rs.getString(3));
			layout.setPageLayoutId(rs.getString(4));
			String themeName = rs.getString(5);
			String themeContext = rs.getString(6);
			if (!StringUtil.isEmpty(themeName) && !StringUtil.isEmpty(themeContext)) {
				layout.setThemeName(new ThemeName(themeContext, themeName));
			}
			return layout;
		}
	};
	private static final RowMapper<LayoutTitle> layoutTitleRowMapper = new RowMapper<LayoutTitle>() {

		@Override
		public LayoutTitle mapRow(ResultSet rs, int rowNum) throws SQLException {
			LayoutTitle layoutTitle = new LayoutTitle();
			layoutTitle.setId(rs.getLong(1));
			layoutTitle.setLanguageId(rs.getString(2));
			layoutTitle.setLayoutId(rs.getLong(3));
			layoutTitle.setTitle(rs.getString(4));
			return layoutTitle;
		}
	};

	public List<LayoutTitle> getLayoutTitles() {
		return jdbcTemplate.query("SELECT LayoutTitle.id, LayoutTitle.languageId, LayoutTitle.layoutId, LayoutTitle.title FROM LayoutTitle",
				layoutTitleRowMapper);
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
		if (!isFriendlyUrlValid(friendlyUrl)) {
			throw new IllegalFriendlyUrlException(friendlyUrl);
		}
		if (!languageRepository.isLanguageIdSupported(defaultLayoutTitleLanguageId)) {
			throw new IllegalLanguageIdException(defaultLayoutTitleLanguageId);
		}
		if (pageLayoutName == null) {
			throw new IllegalPageLayoutException("pageLayoutId is null");
		}
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(ADD_LAYOUT, new String[] { "id" });
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

	public void updatePageLayout(long layoutId, PageLayoutName pageLayoutName) {
		try {
			jdbcTemplate.update("UPDATE Layout SET pageLayoutId = ? WHERE id = ?", pageLayoutName.getFullName(), layoutId);
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
		log.warn("get layout; layoutsetid = {}; friendlyUrl = {}", layoutSetId, friendlyUrl);
		Theme defaultTheme = themeRepository.getDefaultTheme();
		String pageLayoutId = pageLayoutRepository.getDefaultPageLayout().getPageLayoutName().getFullName();
		return new DefaultLayout(layoutSetId, friendlyUrl, defaultTheme.getThemeName(), pageLayoutId);
	}

	public boolean isFriendlyUrlValid(String friendlyUrl) {
		return !StringUtil.isEmpty(friendlyUrl) && friendlyUrl.startsWith("/") && FRIENDLY_URL_PATTERN.matcher(friendlyUrl).matches();
	}
}
