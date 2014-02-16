package ee.midaiganes.portal.permission;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.PortalResource;
import ee.midaiganes.portal.group.Group;
import ee.midaiganes.portal.group.GroupRepository;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;
import ee.midaiganes.util.PortalUtil;
import ee.midaiganes.util.PropsValues;
import ee.midaiganes.util.StringUtil;

@Resource(name = PortalConfig.PERMISSION_REPOSITORY)
public class PermissionRepository {
    private static final long[] EMPTY_ARRAY = new long[0];

    private final PermissionService permissionService;

    private final ResourceRepository resourceRepository;

    private final GroupRepository groupRepository;

    public PermissionRepository(PermissionService permissionService, ResourceRepository resourceRepository, GroupRepository groupRepository) {
        this.permissionService = permissionService;
        this.resourceRepository = resourceRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = PortalConfig.TXMANAGER)
    public boolean hasUserPermission(long userId, PortalResource resource, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return hasUserPermission(userId, resource.getResource(), resource.getId(), action);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = PortalConfig.TXMANAGER)
    public boolean hasUserPermission(long userId, String resource, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return hasUserPermission(userId, resourceRepository.getResourceId(resource), resourcePrimKey, action);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = PortalConfig.TXMANAGER)
    public void setUserPermissions(long userId, long resourceId, long resourcePrimKey, String[] actions, boolean[] permissions) throws ResourceNotFoundException,
            ResourceActionNotFoundException {
        setPermissions(getUserResourceId(), userId, resourceId, resourcePrimKey, actions, permissions);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = PortalConfig.TXMANAGER)
    public void setPermissions(long resourceId, long resourcePrimKey, long resource2Id, long resource2PrimKey, String[] actions, boolean[] permissions)
            throws ResourceActionNotFoundException {
        permissionService.setPermissions(resourceId, resourcePrimKey, resource2Id, resource2PrimKey, actions, permissions);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = PortalConfig.TXMANAGER)
    public boolean hasPermission(PortalResource resource, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return permissionService.hasPermission(resourceRepository.getResourceId(resource.getResource()), resource.getId(), resourceId, resourcePrimKey, action);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = PortalConfig.TXMANAGER)
    public boolean hasUserPermission(long userId, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return PortalUtil.isSuperAdminUser(userId) ||
        // permissionService.hasPermission(getUserResourceId(), userId,
        // resourceId, resourcePrimKey, action);
                hasUserGroupsPermission(userId, resourceId, resourcePrimKey, action);
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

    @Transactional(propagation = Propagation.REQUIRED, value = PortalConfig.TXMANAGER)
    public boolean hasGroupPermission(long groupId, String resource, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return hasGroupPermission(groupId, resourceRepository.getResourceId(resource), resourcePrimKey, action);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = PortalConfig.TXMANAGER)
    public boolean hasGroupPermission(long groupId, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return permissionService.hasPermission(getGroupResourceId(), groupId, resourceId, resourcePrimKey, action);
    }

    private long getUserResourceId() throws ResourceNotFoundException {
        return resourceRepository.getResourceId(User.class.getName());
    }

    private long getGroupResourceId() throws ResourceNotFoundException {
        return resourceRepository.getResourceId(Group.class.getName());
    }
}