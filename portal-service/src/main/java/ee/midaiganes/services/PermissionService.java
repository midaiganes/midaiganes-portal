package ee.midaiganes.services;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.dao.PermissionDao;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;

@Resource(name = PortalConfig.PERMISSION_SERVICE)
public class PermissionService {

    private final PermissionDao permissionDao;
    private final Cache cache;

    private final ResourceActionRepository resourceActionPermissionRepository;

    public PermissionService(PermissionDao permissionDao, ResourceActionRepository resourceActionPermissionRepository) {
        this.permissionDao = permissionDao;
        this.resourceActionPermissionRepository = resourceActionPermissionRepository;
        this.cache = SingleVmPool.getCache(PermissionService.class.getName());
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, value = PortalConfig.TXMANAGER)
    public boolean hasPermission(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String action) throws ResourceActionNotFoundException {
        long resourceActionPermission = resourceActionPermissionRepository.getResourceActionPermission(resource2, action);
        Long permissions = getPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey);
        return permissions != null && (permissions.longValue() & resourceActionPermission) == resourceActionPermission;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, value = PortalConfig.TXMANAGER)
    public void setPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String[] actions, boolean[] permissions)
            throws ResourceActionNotFoundException {
        long actionPermissions = calculateActionPermission(getResourceActionPermissions(resource2, actions), permissions);
        Long id = permissionDao.loadPermissionsId(resource1, resource1PrimKey, resource2, resource2PrimKey);
        try {
            if (id == null) {
                permissionDao.addPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey, actionPermissions);
            } else {
                permissionDao.updatePermissions(id.longValue(), actionPermissions);
            }
            cache.put(resource1 + "#" + resource1PrimKey + "#" + resource2 + "#" + resource2PrimKey, Long.valueOf(actionPermissions));
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
                permissions = permissionDao.loadPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey);
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
}
