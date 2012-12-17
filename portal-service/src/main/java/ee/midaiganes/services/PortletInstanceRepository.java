package ee.midaiganes.services;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.util.CollectionUtil;
import ee.midaiganes.util.StringPool;

public class PortletInstanceRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final SecureRandom random = new SecureRandom(new Object().toString().getBytes());

	private static final RowMapper<PortletInstance> rowMapper = new RowMapper<PortletInstance>() {

		@Override
		public PortletInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
			PortletInstance portletInstance = new PortletInstance();
			portletInstance.setId(rs.getLong(1));
			portletInstance.setPortletName(new PortletName(rs.getString(2), rs.getString(3)));
			portletInstance.setWindowID(rs.getString(4));
			return portletInstance;
		}
	};

	public PortletInstance getPortletInstance(long id) {
		return CollectionUtil.getFirstElement(jdbcTemplate.query("SELECT id, portletContext, portletName, windowID FROM PortletInstance WHERE id = ?",
				rowMapper, id));
	}

	public void addPortletInstance(PortletName portletName, String windowID) {
		if (StringPool.DEFAULT_PORTLET_WINDOWID.equals(windowID)) {
			jdbcTemplate.update("INSERT IGNORE INTO PortletInstance(portletContext, portletName, windowID) VALUES(?, ?, ?)", portletName.getContext(),
					portletName.getName(), windowID);
		} else {
			jdbcTemplate.update("INSERT INTO PortletInstance(portletContext, portletName, windowID) VALUES(?, ?, ?)", portletName.getContext(),
					portletName.getName(), windowID);
		}
	}

	public long addPortletInstance(final PortletName portletName) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement("INSERT INTO PortletInstance(portletContext, portletName, windowID) VALUES(?, ?, ?)",
						new String[] { "id" });
				String windowID = Integer.toString(random.nextInt(1000000000));
				ps.setString(1, portletName.getContext());
				ps.setString(2, portletName.getName());
				ps.setString(3, windowID);
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	public void deletePortletInstance(String windowID) {
		jdbcTemplate.update("DELETE FROM PortletInstance WHERE windowID = ?", windowID);
	}
}
