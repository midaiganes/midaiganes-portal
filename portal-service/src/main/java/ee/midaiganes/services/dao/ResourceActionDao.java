package ee.midaiganes.services.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.model.ResourceActionPermission;
import ee.midaiganes.services.rowmapper.ResourceActionPermissionRowMapper;

public class ResourceActionDao {
    private static final String QRY_GET_RESOURCE_ACTION_PERMISSION = "SELECT resourceId, action, permission FROM ResourceAction WHERE resourceId = ?";
    private final ResourceActionPermissionRowMapper resourceActionPermissionRowMapper;
    private final JdbcTemplate jdbcTemplate;

    public ResourceActionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceActionPermissionRowMapper = new ResourceActionPermissionRowMapper();
    }

    public List<ResourceActionPermission> loadResourceActionPermissions(long resourceId) {
        return jdbcTemplate.query(QRY_GET_RESOURCE_ACTION_PERMISSION, resourceActionPermissionRowMapper, Long.valueOf(resourceId));
    }
}
