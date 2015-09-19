package ee.midaiganes.portal.permission;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;

import ee.midaiganes.model.PortalResource;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

public class PermissionService {

    private final ResourceActionRepository resourceActionPermissionRepository;
    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;

    @Inject
    public PermissionService(ResourceActionRepository resourceActionPermissionRepository, ResourceRepository resourceRepository, PermissionRepository permissionRepository) {
        this.resourceActionPermissionRepository = resourceActionPermissionRepository;
        this.resourceRepository = resourceRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional
    public boolean hasPermission(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String action) throws ResourceActionNotFoundException {
        return permissionRepository.hasPermission(resource1, resource1PrimKey, resource2, resource2PrimKey, action);
    }

    @Transactional
    public boolean hasPermission(PortalResource resource, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return hasPermission(resourceRepository.getResourceId(resource.getResource()), resource.getId(), resourceId, resourcePrimKey, action);
    }

    @Transactional
    public void setPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String[] actions, boolean[] permissions)
            throws ResourceActionNotFoundException {
        long actionPermissions = calculateActionPermission(getResourceActionPermissions(resource2, actions), permissions);
        permissionRepository.setPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey, actionPermissions);
    }

    @Transactional
    public boolean hasUserPermission(long userId, @Nonnull String resource, long resourcePrimKey, ResourceAction action) throws ResourceNotFoundException,
            ResourceActionNotFoundException {
        return permissionRepository.hasUserPermission(userId, resourceRepository.getResourceId(resource), resourcePrimKey, action.getAction());
    }

    @Transactional
    public boolean hasUserPermission(long userId, @Nonnull String resource, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return permissionRepository.hasUserPermission(userId, resourceRepository.getResourceId(resource), resourcePrimKey, action);
    }

    @Transactional
    public boolean hasUserPermission(long userId, PortalResource resource, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return permissionRepository.hasUserPermission(userId, resource, action);
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
