package ee.midaiganes.services.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.services.rowmapper.LongResultSetExtractor;

public class PermissionDao {
	private static final String QRY_LOAD_PERMISSIONS = "SELECT permission FROM Permission WHERE resourceId = ? AND resourcePrimKey = ? AND resource2Id = ? AND resource2PrimKey = ?";
	private static final String QRY_ADD_PERMISSIONS = "INSERT INTO Permission(resourceId, resourcePrimKey, resource2Id, resource2PrimKey, permission) VALUES(?, ?, ?, ?, ?)";
	private static final String QRY_UPDATE_PERMISSIONS = "UPDATE Permission SET permission = ? WHERE id = ?";
	private static final String QRY_GET_PERMISSIONS_ID = "SELECT id FROM Permission WHERE resourceId = ? AND resourcePrimKey = ? AND resource2Id = ? AND resource2PrimKey = ?";
	private static final LongResultSetExtractor longResultSetExtractor = new LongResultSetExtractor();

	private final JdbcTemplate jdbcTemplate;

	public PermissionDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Long loadPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey) {
		return jdbcTemplate.query(QRY_LOAD_PERMISSIONS, longResultSetExtractor, Long.valueOf(resource1), Long.valueOf(resource1PrimKey),
				Long.valueOf(resource2), Long.valueOf(resource2PrimKey));
	}

	public void addPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, long actionPermissions) {
		jdbcTemplate.update(QRY_ADD_PERMISSIONS, Long.valueOf(resource1), Long.valueOf(resource1PrimKey), Long.valueOf(resource2),
				Long.valueOf(resource2PrimKey), Long.valueOf(actionPermissions));
	}

	public void updatePermissions(long id, long actionPermissions) {
		jdbcTemplate.update(QRY_UPDATE_PERMISSIONS, Long.valueOf(actionPermissions), Long.valueOf(id));
	}

	public Long loadPermissionsId(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey) {
		return jdbcTemplate.query(QRY_GET_PERMISSIONS_ID, longResultSetExtractor, Long.valueOf(resource1), Long.valueOf(resource1PrimKey),
				Long.valueOf(resource2), Long.valueOf(resource2PrimKey));
	}
}
