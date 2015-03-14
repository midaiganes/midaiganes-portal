package ee.midaiganes.portal.group;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

public class GroupRepository {
    private final GroupDao groupDao;

    private final LoadingCache<Long, long[]> userGroupsCache;
    private final LoadingCache<Boolean, ImmutableList<Group>> allGroupsCache;

    @Inject
    public GroupRepository(GroupDao groupDao) {
        this.groupDao = groupDao;

        this.userGroupsCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, long[]>() {
            @Override
            public long[] load(Long userId) {
                return groupDao.loadUserGroupIds(userId.longValue()).toArray();
            }
        });
        this.allGroupsCache = CacheBuilder.newBuilder().concurrencyLevel(1).maximumSize(1).expireAfterAccess(1, TimeUnit.HOURS)
                .build(new CacheLoader<Boolean, ImmutableList<Group>>() {
                    @Override
                    public ImmutableList<Group> load(Boolean key) throws Exception {
                        return ImmutableList.copyOf(groupDao.loadGroups());
                    }
                });
    }

    public ImmutableList<Group> getGroups() {
        return allGroupsCache.getUnchecked(Boolean.TRUE);
    }

    public void addGroup(@Nonnull String name, boolean userGroup) {
        try {
            groupDao.addGroup(name, userGroup);
        } finally {
            allGroupsCache.invalidateAll();
        }
    }

    public void addUserGroup(long userId, long groupId) {
        try {
            groupDao.addUserGroup(userId, groupId);
        } finally {
            userGroupsCache.invalidate(Long.valueOf(userId));
        }
    }

    public void removeUserGroup(long userId, long groupId) {
        try {
            groupDao.removeUserGroup(userId, groupId);
        } finally {
            userGroupsCache.invalidate(Long.valueOf(userId));
        }
    }

    public long[] getUserGroupIds(long userId) {
        return userGroupsCache.getUnchecked(Long.valueOf(userId));
    }

    public Long getGroupId(String name) {
        for (Group group : allGroupsCache.getUnchecked(Boolean.TRUE)) {
            if (group.getName().equals(name)) {
                return Long.valueOf(group.getId());
            }
        }
        return null;
    }

    public void deleteGroup(long groupId) {
        try {
            groupDao.deleteGroup(groupId);
        } finally {
            userGroupsCache.invalidateAll();
            allGroupsCache.invalidateAll();
        }
    }
}
