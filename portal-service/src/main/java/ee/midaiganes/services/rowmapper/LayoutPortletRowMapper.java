package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.model.PortletNamespace;

public class LayoutPortletRowMapper implements RowMapper<LayoutPortlet> {
	@Override
	public LayoutPortlet mapRow(ResultSet rs, int rowNum) throws SQLException {
		long layoutPortletId = rs.getLong(1);
		long layoutId = rs.getLong(2);
		long rowId = rs.getLong(3);
		long portletInstanceId = rs.getLong(4);
		PortletNamespace namespace = new PortletNamespace(new PortletName(rs.getString(5), rs.getString(6)), rs.getString(7));
		PortletInstance portletInstance = new PortletInstance(portletInstanceId, namespace);
		return new LayoutPortlet(layoutPortletId, portletInstanceId, layoutId, rowId, portletInstance);
	}
}