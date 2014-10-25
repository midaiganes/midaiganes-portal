package ee.midaiganes.portal.permission;

import java.util.List;

import javax.inject.Inject;

import org.springframework.jdbc.core.JdbcTemplate;

public class ResourceActionDao {
    private static final String QRY_GET_RESOURCE_ACTION_PERMISSION = "SELECT resourceId, action, permission FROM ResourceAction WHERE resourceId = ?";
    private final ResourceActionPermissionRowMapper resourceActionPermissionRowMapper;
    private final JdbcTemplate jdbcTemplate;

    @Inject
    public ResourceActionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceActionPermissionRowMapper = new ResourceActionPermissionRowMapper();
    }

    public List<ResourceActionPermission> loadResourceActionPermissions(long resourceId) {
        return jdbcTemplate.query(QRY_GET_RESOURCE_ACTION_PERMISSION, resourceActionPermissionRowMapper, Long.valueOf(resourceId));
    }
}
