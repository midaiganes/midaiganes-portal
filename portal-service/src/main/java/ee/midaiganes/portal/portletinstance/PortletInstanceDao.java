package ee.midaiganes.portal.portletinstance;

import java.util.List;

import javax.inject.Inject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.services.rowmapper.LongResultSetExtractor;
import ee.midaiganes.util.StringPool;

public class PortletInstanceDao {
    private static final String DELETE_PORTLET_INSTANCE = "DELETE FROM PortletInstance WHERE windowID = ?";
    private static final String GET_PORTLET_INSTANCE_ID = "SELECT id FROM PortletInstance WHERE portletContext = ? and portletName = ? and windowID = ?";
    private static final String GET_DEFAULT_PORTLET_INSTANCES = "SELECT id, portletContext, portletName FROM PortletInstance WHERE windowID = ?";
    private static final LongResultSetExtractor longResultSetExtractor = new LongResultSetExtractor();
    private final JdbcTemplate jdbcTemplate;

    @Inject
    public PortletInstanceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deletePortletInstance(String windowID) {
        jdbcTemplate.update(DELETE_PORTLET_INSTANCE, windowID);
    }

    public long addPortletInstance(PortletName portletName, String windowID) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new AddPortletPreparedStatementCreator(portletName, windowID), keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<PortletInstance> getDefaultPortletInstances() {
        return jdbcTemplate.query(GET_DEFAULT_PORTLET_INSTANCES, new DefaultPortletInstanceRowMapper(), StringPool.DEFAULT_PORTLET_WINDOWID);
    }

    public Long loadPortletInstanceId(PortletName portletName, String windowID) {
        return jdbcTemplate.query(GET_PORTLET_INSTANCE_ID, longResultSetExtractor, portletName.getContext(), portletName.getName(), windowID);
    }
}
