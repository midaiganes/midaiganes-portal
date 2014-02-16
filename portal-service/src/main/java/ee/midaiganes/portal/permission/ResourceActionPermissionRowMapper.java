package ee.midaiganes.portal.permission;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.ResourceActionPermission;

public final class ResourceActionPermissionRowMapper implements RowMapper<ResourceActionPermission> {
	@Override
	public final ResourceActionPermission mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		return new ResourceActionPermission(rs.getLong(1), rs.getString(2), rs.getLong(3));
	}
}