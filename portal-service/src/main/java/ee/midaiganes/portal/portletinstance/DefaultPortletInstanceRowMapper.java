package ee.midaiganes.portal.portletinstance;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.util.StringPool;

public class DefaultPortletInstanceRowMapper implements RowMapper<PortletInstance> {
    @Override
    public PortletInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong(1);
        PortletNamespace namespace = new PortletNamespace(new PortletName(rs.getString(2), rs.getString(3)), StringPool.DEFAULT_PORTLET_WINDOWID);
        return new PortletInstance(id, namespace);
    }
}
