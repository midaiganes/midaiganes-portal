package ee.midaiganes.services;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.rowmapper.LongResultSetExtractor;

@Deprecated
// @Resource(name = PortalConfig.PERMISSION_SERVICE)
public class Permission1Service implements PermissionService {
	private final LongResultSetExtractor resultSetExtractor;
	private final Cache cache;

	@Resource(name = PortalConfig.RESOURCE_ACTION_REPOSITORY)
	private ResourceActionRepository resourceActionPermissionRepository;

	@Resource(name = PortalConfig.PORTAL_JDBC_TEMPLATE)
	private JdbcTemplate jdbcTemplate;

	public Permission1Service() {
		resultSetExtractor = new LongResultSetExtractor();
		cache = SingleVmPool.getCache(Permission1Service.class.getName());
	}

	@Override
	public boolean hasPermission(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String action)
			throws ResourceActionNotFoundException {
		long resourceActionPermission = resourceActionPermissionRepository.getResourceActionPermission(resource2, action);

		String cacheKey = resource1 + "#" + resource1PrimKey + "#" + resource2 + "#" + resource2PrimKey;
		Element el = cache.getElement(cacheKey);
		Long permissions = null;
		if (el == null) {
			try {
				permissions = jdbcTemplate
						.query("SELECT ResourceInstance_ResourceInstancePermission.permission FROM ResourceInstance_ResourceInstancePermission "
								+ "JOIN ResourceInstance_Resource ON (ResourceInstance_Resource.id = ResourceInstance_ResourceInstancePermission.resourceInstanceResourceId) "
								+ "JOIN ResourceInstance ON (ResourceInstance.id = ResourceInstance_Resource.resourceInstanceId) "
								+ "WHERE ResourceInstance.resourceId = " + resource1 + " AND ResourceInstance.resourcePrimKey = " + resource1PrimKey
								+ " AND ResourceInstance_Resource.resourceId = " + resource2
								+ " AND ResourceInstance_ResourceInstancePermission.resourcePrimKey = " + resource2PrimKey, resultSetExtractor);
			} finally {
				cache.put(cacheKey, permissions);
			}
		} else {
			permissions = el.get();
		}

		return permissions != null && (permissions.longValue() & resourceActionPermission) == resourceActionPermission;
	}

	@Override
	public void setPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String action[], boolean hasPermission[])
			throws ResourceActionNotFoundException {
		// TODO
	}

}
