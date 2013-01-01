package ee.midaiganes.services;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.services.rowmapper.PortletInstanceRowMapper;
import ee.midaiganes.util.CollectionUtil;
import ee.midaiganes.util.StringPool;

public class PortletInstanceRepository {
	private static final Logger log = LoggerFactory.getLogger(PortletInstanceRepository.class);
	private static final SecureRandom random = new SecureRandom(new Object().toString().getBytes());
	private static final PortletInstanceRowMapper rowMapper = new PortletInstanceRowMapper();
	private static final String[] ID_ARRAY = new String[] { StringPool.ID };
	private static final String DELETE_PORTLET_INSTANCE = "DELETE FROM PortletInstance WHERE windowID = ?";
	private static final String GET_PORTLET_INSTANCE = "SELECT id, portletContext, portletName, windowID FROM PortletInstance WHERE id = ?";
	private static final String ADD_PORTLET_INSTANCE = "INSERT INTO PortletInstance(portletContext, portletName, windowID) VALUES(?, ?, ?)";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public PortletInstance getPortletInstance(long id) {
		return CollectionUtil.getFirstElement(jdbcTemplate.query(GET_PORTLET_INSTANCE, rowMapper, id));
	}

	public void addDefaultPortletInstance(PortletName portletName) {
		try {
			addPortletInstance(portletName, StringPool.DEFAULT_PORTLET_WINDOWID);
		} catch (DuplicateKeyException e) {
			log.debug(e.getMessage(), e);
		}
	}

	public long addPortletInstance(final PortletName portletName) {
		return addPortletInstance(portletName, generateWindowID());
	}

	public long addPortletInstance(final PortletName portletName, final String windowID) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(ADD_PORTLET_INSTANCE, ID_ARRAY);
				ps.setString(1, portletName.getContext());
				ps.setString(2, portletName.getName());
				ps.setString(3, windowID);
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	public void deletePortletInstance(String windowID) {
		jdbcTemplate.update(DELETE_PORTLET_INSTANCE, windowID);
	}

	private String generateWindowID() {
		return Integer.toString(random.nextInt(1000000000));
	}
}
