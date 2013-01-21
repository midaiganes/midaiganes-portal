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
		LayoutPortlet layoutPortlet = new LayoutPortlet();
		layoutPortlet.setId(rs.getLong(1));
		layoutPortlet.setLayoutId(rs.getLong(2));
		layoutPortlet.setRowId(rs.getLong(3));
		layoutPortlet.setPortletInstanceId(rs.getLong(4));
		PortletInstance portletInstance = new PortletInstance();
		portletInstance.setId(rs.getLong(5));
		portletInstance.setPortletNamespace(new PortletNamespace(new PortletName(rs.getString(6), rs.getString(7)), rs.getString(8)));
		layoutPortlet.setPortletInstance(portletInstance);
		return layoutPortlet;
	}
}