package ee.midaiganes.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ee.midaiganes.model.DefaultLayoutSet;
import ee.midaiganes.model.LayoutSet;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.util.StringUtil;

public class LayoutSetRepository {

	private static final Logger log = LoggerFactory.getLogger(LayoutSetRepository.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String GET_LAYOUT_SET_BY_VIRTUAL_HOST = "SELECT LayoutSet.id, LayoutSet.virtualHost, Theme.name, Theme.context FROM LayoutSet LEFT JOIN Theme ON (LayoutSet.themeId = Theme.id) WHERE virtualHost = ?";
	private static final String GET_LAYOUT_SETS = "SELECT LayoutSet.id, LayoutSet.virtualHost, Theme.name, Theme.context FROM LayoutSet LEFT JOIN Theme ON (LayoutSet.themeId = Theme.id)";
	private static final String ADD_LAYOUT_SET = "INSERT INTO LayoutSet(virtualHost, themeId) VALUES(?, ?)";
	private static final String GET_LAYOUT_SETS_CACHE_KEY = "getLayoutSets";
	private static final String GET_LAYOUT_SET_BY_VIRTUAL_HOST_CACHE_KEY_PREFIX = "getLayoutSet#";

	private final Cache cache;

	public LayoutSetRepository() {
		cache = SingleVmPool.getCache(LayoutSetRepository.class.getName());
	}

	private static final RowMapper<LayoutSet> layoutSetRowMapper = new RowMapper<LayoutSet>() {
		@Override
		public LayoutSet mapRow(ResultSet rs, int rowNum) throws SQLException {
			LayoutSet layoutSet = new LayoutSet();
			layoutSet.setId(rs.getLong(1));
			layoutSet.setVirtualHost(rs.getString(2));
			String themeName = rs.getString(3);
			String themeContext = rs.getString(4);
			if (!StringUtil.isEmpty(themeName) && !StringUtil.isEmpty(themeContext)) {
				layoutSet.setThemeName(new ThemeName(themeContext, themeName));
			}
			return layoutSet;
		}
	};

	public List<LayoutSet> getLayoutSets() {
		Element el = cache.getElement(GET_LAYOUT_SETS_CACHE_KEY);
		List<LayoutSet> layoutSets = el != null ? el.<List<LayoutSet>> get() : null;
		if (layoutSets == null) {
			layoutSets = jdbcTemplate.query(GET_LAYOUT_SETS, layoutSetRowMapper);
			cache.put(GET_LAYOUT_SETS_CACHE_KEY, layoutSets);
		}
		return layoutSets;
	}

	public LayoutSet getLayoutSet(String virtualHost) {
		String cacheKey = GET_LAYOUT_SET_BY_VIRTUAL_HOST_CACHE_KEY_PREFIX + virtualHost;
		Element el = cache.getElement(cacheKey);
		LayoutSet layoutSet = el != null ? el.<LayoutSet> get() : null;
		if (el == null) {
			List<LayoutSet> list = jdbcTemplate.query(GET_LAYOUT_SET_BY_VIRTUAL_HOST, layoutSetRowMapper, virtualHost);
			layoutSet = list.isEmpty() ? null : list.get(0);
			// TODO mem leak?
			cache.put(cacheKey, layoutSet);
		}
		return layoutSet;
	}

	public long addLayoutSet(final String virtualHost, final String themeId) {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(ADD_LAYOUT_SET, new String[] { "id" });
					ps.setString(1, virtualHost);
					ps.setString(2, themeId);
					return ps;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} finally {
			cache.clear();
		}
	}

	public LayoutSet getDefaultLayoutSet(String virtualHost) {
		log.warn("get default layout set: {}", virtualHost);
		return new DefaultLayoutSet(virtualHost);
	}
}
