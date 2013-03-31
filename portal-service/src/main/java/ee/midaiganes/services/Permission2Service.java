package ee.midaiganes.services;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.rowmapper.LongResultSetExtractor;

@Component(value = PortalConfig.PERMISSION_SERVICE)
public class Permission2Service implements PermissionService {
	private static final String QRY_LOAD_PERMISSIONS = "SELECT permission FROM Permission WHERE resourceId = ? AND resourcePrimKey = ? AND resource2Id = ? AND resource2PrimKey = ?";
	private static final String QRY_ADD_PERMISSIONS = "INSERT INTO Permission(resourceId, resourcePrimKey, resource2Id, resource2PrimKey, permission) VALUES(?, ?, ?, ?, ?)";
	private static final String QRY_UPDATE_PERMISSIONS = "UPDATE Permission SET permission = ? WHERE id = ?";
	private static final String QRY_GET_PERMISSIONS_ID = "SELECT id FROM Permission WHERE resourceId = ? AND resourcePrimKey = ? AND resource2Id = ? AND resource2PrimKey = ?";

	private final LongResultSetExtractor longResultSetExtractor;
	private final Cache cache;

	@Resource(name = PortalConfig.PORTAL_JDBC_TEMPLATE)
	private JdbcTemplate jdbcTemplate;

	@Resource(name = PortalConfig.RESOURCE_ACTION_REPOSITORY)
	private ResourceActionRepository resourceActionPermissionRepository;

	public Permission2Service() {
		longResultSetExtractor = new LongResultSetExtractor();
		cache = SingleVmPool.getCache(Permission2Service.class.getName());
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, value = PortalConfig.TXMANAGER)
	public boolean hasPermission(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String action)
			throws ResourceActionNotFoundException {
		long resourceActionPermission = resourceActionPermissionRepository.getResourceActionPermission(resource2, action);
		Long permissions = getPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey);
		return permissions != null && (permissions.longValue() & resourceActionPermission) == resourceActionPermission;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, value = PortalConfig.TXMANAGER)
	public void setPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String[] actions, boolean[] permissions)
			throws ResourceActionNotFoundException {
		long actionPermissions = calculateActionPermission(getResourceActionPermissions(resource2, actions), permissions);
		Long id = getPermissionsId(resource1, resource1PrimKey, resource2, resource2PrimKey);
		try {
			if (id == null) {
				addPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey, actionPermissions);
			} else {
				updatePermissions(id.longValue(), actionPermissions);
			}
			cache.put(resource1 + "#" + resource1PrimKey + "#" + resource2 + "#" + resource2PrimKey, actionPermissions);
		} catch (RuntimeException e) {
			cache.clear();
			throw e;
		}
	}

	private Long getPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey) {
		String cacheKey = resource1 + "#" + resource1PrimKey + "#" + resource2 + "#" + resource2PrimKey;
		Element el = cache.getElement(cacheKey);
		if (el == null) {
			Long permissions = null;
			try {
				permissions = loadPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey);
			} finally {
				cache.put(cacheKey, permissions);
			}
			return permissions;
		}
		return el.get();
	}

	private long calculateActionPermission(long[] actionPermissions, boolean[] permissions) {
		long val = 0;
		for (int i = 0; i < actionPermissions.length; i++) {
			if (permissions[i]) {
				val = val | actionPermissions[i];
			}
		}
		return val;
	}

	private long[] getResourceActionPermissions(long resourceId, String[] actions) throws ResourceActionNotFoundException {
		long[] actionPermissions = new long[actions.length];
		for (int i = 0; i < actions.length; i++) {
			actionPermissions[i] = resourceActionPermissionRepository.getResourceActionPermission(resourceId, actions[i]);
		}
		return actionPermissions;
	}

	private Long loadPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey) {
		return jdbcTemplate.query(QRY_LOAD_PERMISSIONS, longResultSetExtractor, resource1, resource1PrimKey, resource2, resource2PrimKey);
	}

	private void addPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, long actionPermissions) {
		jdbcTemplate.update(QRY_ADD_PERMISSIONS, resource1, resource1PrimKey, resource2, resource2PrimKey, actionPermissions);
	}

	private void updatePermissions(long id, long actionPermissions) {
		jdbcTemplate.update(QRY_UPDATE_PERMISSIONS, actionPermissions, id);
	}

	private Long getPermissionsId(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey) {
		return jdbcTemplate.query(QRY_GET_PERMISSIONS_ID, longResultSetExtractor, resource1, resource1PrimKey, resource2, resource2PrimKey);
	}
}
