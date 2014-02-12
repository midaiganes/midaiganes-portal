package ee.midaiganes.portal.layoutset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ee.midaiganes.model.ThemeName;
import ee.midaiganes.util.StringPool;

public class LayoutSetDao {
	private static final String QRY_UPDATE_LAYOUT_SET = "UPDATE LayoutSet SET virtualHost = ?, themeId = (SELECT Theme.id FROM Theme WHERE Theme.context = ? AND Theme.name = ?) WHERE id = ?";
	private static final String ADD_LAYOUT_SET = "INSERT INTO LayoutSet(virtualHost, themeId) VALUES(?, (SELECT Theme.id FROM Theme WHERE Theme.context = ? AND Theme.name = ?))";
	private static final String GET_LAYOUT_SET_BY_VIRTUAL_HOST = "SELECT LayoutSet.id, LayoutSet.virtualHost, Theme.name, Theme.context FROM LayoutSet LEFT JOIN Theme ON (LayoutSet.themeId = Theme.id) WHERE virtualHost = ?";
	private static final String GET_LAYOUT_SETS = "SELECT LayoutSet.id, LayoutSet.virtualHost, Theme.name, Theme.context FROM LayoutSet LEFT JOIN Theme ON (LayoutSet.themeId = Theme.id)";
	private static final String[] ID_ARRAY = new String[] { StringPool.ID };
	private static final LayoutSetRowMapper layoutSetRowMapper = new LayoutSetRowMapper();

	private final JdbcTemplate jdbcTemplate;

	public LayoutSetDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<LayoutSet> getLayoutSets() {
		return jdbcTemplate.query(GET_LAYOUT_SETS, layoutSetRowMapper);
	}

	public List<LayoutSet> getLayoutSet(String virtualHost) {
		return jdbcTemplate.query(GET_LAYOUT_SET_BY_VIRTUAL_HOST, layoutSetRowMapper, virtualHost);
	}

	public void updateLayoutSet(long id, String virtualHost, ThemeName themeName) {
		jdbcTemplate.update(QRY_UPDATE_LAYOUT_SET, virtualHost, themeName.getContext(), themeName.getName(), Long.valueOf(id));
	}

	public long addLayoutSet(final String virtualHost, final ThemeName themeName) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(ADD_LAYOUT_SET, ID_ARRAY);
				ps.setString(1, virtualHost);
				ps.setString(2, themeName.getContext());
				ps.setString(3, themeName.getName());
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

}
