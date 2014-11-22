package ee.midaiganes.portal.permission;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.model.PortalResource;
import ee.midaiganes.portal.group.Group;
import ee.midaiganes.portal.group.GroupRepository;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;
import ee.midaiganes.util.PortalUtil;
import ee.midaiganes.util.PropsValues;
import ee.midaiganes.util.StringUtil;

public class PermissionRepository {
    private static final long[] EMPTY_ARRAY = new long[0];
    @Nonnull
    private static final String GROUP_CLASS_NAME = StringUtil.getName(Group.class);
    private final ResourceRepository resourceRepository;
    private final GroupRepository groupRepository;
    private final PermissionDao permissionDao;
    private final ResourceActionRepository resourceActionPermissionRepository;

    private final SingleVmCache cache;

    @Inject
    public PermissionRepository(ResourceRepository resourceRepository, GroupRepository groupRepository, PermissionDao permissionDao,
            ResourceActionRepository resourceActionPermissionRepository) {
        this.resourceRepository = resourceRepository;
        this.groupRepository = groupRepository;
        this.permissionDao = permissionDao;
        this.resourceActionPermissionRepository = resourceActionPermissionRepository;
        this.cache = SingleVmPoolUtil.getCache(PermissionRepository.class.getName());
    }

    @Transactional
    public boolean hasUserPermission(long userId, PortalResource resource, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return hasUserPermission(userId, resourceRepository.getResourceId(resource.getResource()), resource.getId(), action);
    }

    @Transactional
    protected boolean hasUserPermission(long userId, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return PortalUtil.isSuperAdminUser(userId) || hasUserGroupsPermission(userId, resourceId, resourcePrimKey, action);
    }

    private boolean hasUserGroupsPermission(long userId, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        for (long userGroupId : getUserGroups(userId)) {
            if (hasGroupPermission(userGroupId, resourceId, resourcePrimKey, action)) {
                return true;
            }
        }
        return false;
    }

    private long[] getUserGroups(long userId) {
        long[] groupIds = null;
        if (User.isDefaultUserId(userId)) {
            if (!StringUtil.isEmpty(PropsValues.GUEST_GROUP_NAME)) {
                Long guestGroupId = groupRepository.getGroupId(PropsValues.GUEST_GROUP_NAME);
                if (guestGroupId != null) {
                    groupIds = new long[] { guestGroupId.longValue() };
                }
            }
            if (!StringUtil.isEmpty(PropsValues.NOT_LOGGED_IN_GROUP_NAME)) {
                Long notLoggedInGroupId = groupRepository.getGroupId(PropsValues.NOT_LOGGED_IN_GROUP_NAME);
                if (notLoggedInGroupId != null) {
                    groupIds = groupIds == null ? new long[] { notLoggedInGroupId.longValue() } : new long[] { notLoggedInGroupId.longValue(), groupIds[0] };
                }
            }
        } else {
            long[] list = groupRepository.getUserGroupIds(userId);
            if (!StringUtil.isEmpty(PropsValues.LOGGED_IN_GROUP_NAME)) {
                Long loggedInGroupId = groupRepository.getGroupId(PropsValues.LOGGED_IN_GROUP_NAME);
                if (loggedInGroupId != null) {
                    long[] ll = new long[list.length + 1];
                    System.arraycopy(list, 0, ll, 0, list.length);
                    ll[list.length - 1] = loggedInGroupId.longValue();
                    list = ll;
                }
            }
            groupIds = list;
        }
        return groupIds == null ? EMPTY_ARRAY : groupIds;
    }

    private boolean hasGroupPermission(long groupId, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return hasPermission(getGroupResourceId(), groupId, resourceId, resourcePrimKey, action);
    }

    private long getGroupResourceId() throws ResourceNotFoundException {
        return resourceRepository.getResourceId(GROUP_CLASS_NAME);
    }

    protected boolean hasPermission(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String action) throws ResourceActionNotFoundException {
        long resourceActionPermission = resourceActionPermissionRepository.getResourceActionPermission(resource2, action);
        Long permissions = getPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey);
        return permissions != null && (permissions.longValue() & resourceActionPermission) == resourceActionPermission;
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

    protected void setPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, long actionPermissions) {
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
}
