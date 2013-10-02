package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.model.PortletNamespace;

public class PortletInstanceRowMapper implements RowMapper<PortletInstance> {
	@Override
	public PortletInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
		long id = rs.getLong(1);
		PortletNamespace namespace = new PortletNamespace(new PortletName(rs.getString(2), rs.getString(3)), rs.getString(4));
		return new PortletInstance(id, namespace);
	}
}