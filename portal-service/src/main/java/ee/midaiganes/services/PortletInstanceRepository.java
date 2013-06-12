package ee.midaiganes.services;

import java.security.SecureRandom;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.model.PortletNamespace;
import ee.midaiganes.services.rowmapper.LongResultSetExtractor;
import ee.midaiganes.services.rowmapper.PortletInstanceRowMapper;
import ee.midaiganes.services.statementcreator.AddPortletPreparedStatementCreator;
import ee.midaiganes.util.CharsetPool;
import ee.midaiganes.util.CollectionUtil;
import ee.midaiganes.util.StringPool;

@Component(value = PortalConfig.PORTLET_INSTANCE_REPOSITORY)
public class PortletInstanceRepository {
	private static final Logger log = LoggerFactory.getLogger(PortletInstanceRepository.class);
	private static PortletInstanceRepository instance;
	private static final SecureRandom random = new SecureRandom(new Object().toString().getBytes(CharsetPool.UTF_8));
	private static final PortletInstanceRowMapper rowMapper = new PortletInstanceRowMapper();
	private static final String DELETE_PORTLET_INSTANCE = "DELETE FROM PortletInstance WHERE windowID = ?";
	private static final String GET_PORTLET_INSTANCE = "SELECT id, portletContext, portletName, windowID FROM PortletInstance WHERE id = ?";
	private static final String GET_PORTLET_INSTANCE_ID = "SELECT id FROM PortletInstance WHERE portletContext = ? and portletName = ? and windowID = ?";
	private final LongResultSetExtractor longResultSetExtractor;

	@Resource(name = PortalConfig.PORTAL_JDBC_TEMPLATE)
	private JdbcTemplate jdbcTemplate;

	public PortletInstanceRepository() {
		longResultSetExtractor = new LongResultSetExtractor();
	}

	public static void setInstance(PortletInstanceRepository instance) {
		PortletInstanceRepository.instance = instance;
	}

	public static PortletInstanceRepository getInstance() {
		return PortletInstanceRepository.instance;
	}

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

	public PortletInstance getDefaultPortletInstance(PortletName portletName) {
		return getPortletInstance(portletName, StringPool.DEFAULT_PORTLET_WINDOWID);
	}

	public PortletInstance getPortletInstance(PortletName portletName, String windowID) {
		Long value = jdbcTemplate.query(GET_PORTLET_INSTANCE_ID, longResultSetExtractor, portletName.getContext(), portletName.getName(), windowID);
		if (value != null) {
			PortletInstance instance = new PortletInstance();
			instance.setId(value.longValue());
			instance.setPortletNamespace(new PortletNamespace(portletName, windowID));
			return instance;
		}
		return null;
	}

	public long addPortletInstance(final PortletName portletName) {
		return addPortletInstance(portletName, generateWindowID());
	}

	public long addPortletInstance(final PortletName portletName, final String windowID) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new AddPortletPreparedStatementCreator(portletName, windowID), keyHolder);
		return keyHolder.getKey().longValue();
	}

	public void deletePortletInstance(String windowID) {
		jdbcTemplate.update(DELETE_PORTLET_INSTANCE, windowID);
	}

	private String generateWindowID() {
		return Integer.toString(random.nextInt(1000000000));
	}
}
