package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletName;

public class PortletInstanceRowMapper implements RowMapper<PortletInstance> {
	@Override
	public PortletInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
		PortletInstance portletInstance = new PortletInstance();
		portletInstance.setId(rs.getLong(1));
		portletInstance.setPortletName(new PortletName(rs.getString(2), rs.getString(3)));
		portletInstance.setWindowID(rs.getString(4));
		return portletInstance;
	}
}