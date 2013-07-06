package ee.midaiganes.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.DefaultUser;
import ee.midaiganes.model.Group;
import ee.midaiganes.model.PortalResource;
import ee.midaiganes.model.User;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;
import ee.midaiganes.util.ArrayUtil;
import ee.midaiganes.util.PortalUtil;
import ee.midaiganes.util.PropsValues;
import ee.midaiganes.util.StringUtil;

@Component(value = PortalConfig.PERMISSION_REPOSITORY)
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

	public boolean hasUserPermission(long userId, PortalResource resource, String action) throws ResourceNotFoundException, ResourceActionNotFoundException {
		return hasUserPermission(userId, resource.getResource(), resource.getId(), action);
	}

	public boolean hasUserPermission(long userId, String resource, long resourcePrimKey, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return hasUserPermission(userId, resourceRepository.getResourceId(resource), resourcePrimKey, action);
	}

	public void setUserPermissions(long userId, long resourceId, long resourcePrimKey, String[] actions, boolean[] permissions)
			throws ResourceNotFoundException, ResourceActionNotFoundException {
		setPermissions(getUserResourceId(), userId, resourceId, resourcePrimKey, actions, permissions);
	}

	public void setPermissions(long resourceId, long resourcePrimKey, long resource2Id, long resource2PrimKey, String[] actions, boolean[] permissions)
			throws ResourceActionNotFoundException {
		permissionService.setPermissions(resourceId, resourcePrimKey, resource2Id, resource2PrimKey, actions, permissions);
	}

	public boolean hasPermission(PortalResource resource, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return permissionService.hasPermission(resourceRepository.getResourceId(resource.getResource()), resource.getId(), resourceId, resourcePrimKey, action);
	}

	public boolean hasUserPermission(long userId, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return PortalUtil.isSuperAdminUser(userId) ||
		// permissionService.hasPermission(getUserResourceId(), userId,
		// resourceId, resourcePrimKey, action);
				hasUserGroupsPermission(userId, resourceId, resourcePrimKey, action);
	}

	private boolean hasUserGroupsPermission(long userId, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		for (long userGroupId : getUserGroups(userId)) {
			if (hasGroupPermission(userGroupId, resourceId, resourcePrimKey, action)) {
				return true;
			}
		}
		return false;
	}

	private long[] getUserGroups(long userId) {
		long[] groupIds = null;
		if (DefaultUser.DEFAULT_USER_ID == userId) {
			if (!StringUtil.isEmpty(PropsValues.GUEST_GROUP_NAME)) {
				Long gid = groupRepository.getGroupId(PropsValues.GUEST_GROUP_NAME);
				if (gid != null) {
					groupIds = new long[] { gid.longValue() };
				}
			}
			if (!StringUtil.isEmpty(PropsValues.NOT_LOGGED_IN_GROUP_NAME)) {
				Long gid = groupRepository.getGroupId(PropsValues.NOT_LOGGED_IN_GROUP_NAME);
				if (gid != null) {
					groupIds = groupIds == null ? new long[] { gid.longValue() } : new long[] { gid.longValue(), groupIds[0] };
				}
			}
		} else {
			List<Long> list = groupRepository.getUserGroupIds(userId);
			if (!StringUtil.isEmpty(PropsValues.LOGGED_IN_GROUP_NAME)) {
				Long gid = groupRepository.getGroupId(PropsValues.LOGGED_IN_GROUP_NAME);
				if (gid != null) {
					list = new ArrayList<>(list);
					list.add(gid);
				}
			}
			groupIds = ArrayUtil.toPrimitivLongArray(list);
		}
		return groupIds == null ? EMPTY_ARRAY : groupIds;
	}

	public boolean hasGroupPermission(long groupId, String resource, long resourcePrimKey, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return hasGroupPermission(groupId, resourceRepository.getResourceId(resource), resourcePrimKey, action);
	}

	public boolean hasGroupPermission(long groupId, long resourceId, long resourcePrimKey, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return permissionService.hasPermission(getGroupResourceId(), groupId, resourceId, resourcePrimKey, action);
	}

	private long getUserResourceId() throws ResourceNotFoundException {
		return resourceRepository.getResourceId(User.class.getName());
	}

	private long getGroupResourceId() throws ResourceNotFoundException {
		return resourceRepository.getResourceId(Group.class.getName());
	}
}
