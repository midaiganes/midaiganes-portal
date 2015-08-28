package ee.midaiganes.portal.permission;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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

    private final LoadingCache<CacheKey, Optional<Long>> cache;

    @Inject
    public PermissionRepository(ResourceRepository resourceRepository, GroupRepository groupRepository, PermissionDao permissionDao,
            ResourceActionRepository resourceActionPermissionRepository) {
        this.resourceRepository = resourceRepository;
        this.groupRepository = groupRepository;
        this.permissionDao = permissionDao;
        this.resourceActionPermissionRepository = resourceActionPermissionRepository;
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new PermissionCacheLoader(this.permissionDao));
    }

    private static final class PermissionCacheLoader extends CacheLoader<CacheKey, Optional<Long>> {
        private final PermissionDao permissionDao;

        private PermissionCacheLoader(PermissionDao permissionDao) {
            this.permissionDao = permissionDao;
        }

        @Override
        public Optional<Long> load(CacheKey key) {
            return Optional.fromNullable(permissionDao.loadPermissions(key.resource1, key.resource1PrimKey, key.resource2, key.resource2PrimKey));
        }
    }

    private static final class CacheKey {
        private final long resource1;
        private final long resource1PrimKey;
        private final long resource2;
        private final long resource2PrimKey;

        private CacheKey(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey) {
            this.resource1 = resource1;
            this.resource1PrimKey = resource1PrimKey;
            this.resource2 = resource2;
            this.resource2PrimKey = resource2PrimKey;
        }

        @Override
        public int hashCode() {
            return (int) (resource1 + resource1PrimKey + resource2 + resource2PrimKey);
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof CacheKey) && equals((CacheKey) o);
        }

        private boolean equals(CacheKey c) {
            return resource1 == c.resource1 && resource1PrimKey == c.resource1PrimKey && resource2 == c.resource2 && resource2PrimKey == c.resource2PrimKey;
        }
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

    @Nullable
    private Long getPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey) {
        return cache.getUnchecked(new CacheKey(resource1, resource1PrimKey, resource2, resource2PrimKey)).orNull();
    }

    protected void setPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, long actionPermissions) {
        Long id = permissionDao.loadPermissionsId(resource1, resource1PrimKey, resource2, resource2PrimKey);
        try {
            if (id == null) {
                permissionDao.addPermissions(resource1, resource1PrimKey, resource2, resource2PrimKey, actionPermissions);
            } else {
                permissionDao.updatePermissions(id.longValue(), actionPermissions);
            }
            cache.put(new CacheKey(resource1, resource1PrimKey, resource2, resource2PrimKey), Optional.of(Long.valueOf(actionPermissions)));
        } catch (RuntimeException e) {
            cache.invalidateAll();
            throw e;
        }
    }
}
