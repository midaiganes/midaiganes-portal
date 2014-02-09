package ee.midaiganes.services;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.model.Group;
import ee.midaiganes.services.dao.GroupDao;

@Resource(name = PortalConfig.GROUP_REPOSITORY)
public class GroupRepository {
    private static final String GET_GROUPS_CACHE_KEY = "getGroups";
    private final SingleVmCache cache;
    private final GroupDao groupDao;
    private static final long[] EMPTY_ARRAY = new long[0];

    public GroupRepository(GroupDao groupDao) {
        cache = SingleVmPoolUtil.getCache(GroupRepository.class.getName());
        this.groupDao = groupDao;
    }

    public List<Group> getGroups() {
        List<Group> groups = cache.get(GET_GROUPS_CACHE_KEY);
        if (groups == null) {
            try {
                groups = groupDao.loadGroups();
            } finally {
                cache.put(GET_GROUPS_CACHE_KEY, groups == null ? Collections.emptyList() : groups);
            }
        }
        return groups;
    }

    public void addGroup(String name, boolean userGroup) {
        try {
            groupDao.addGroup(name, userGroup);
        } finally {
            cache.clear();
        }
    }

    public void addUserGroup(long userId, long groupId) {
        try {
            groupDao.addUserGroup(userId, groupId);
        } finally {
            cache.remove(Long.toString(userId));
        }
    }

    public void removeUserGroup(long userId, long groupId) {
        try {
            groupDao.removeUserGroup(userId, groupId);
        } finally {
            cache.clear();
        }
    }

    public long[] getUserGroupIds(long userId) {
        String cacheKey = Long.toString(userId);
        Element el = cache.getElement(cacheKey);
        if (el != null) {
            return el.get();
        }
        long[] list = null;
        try {
            list = groupDao.loadUserGroupIds(userId).toArray();
        } finally {
            cache.put(cacheKey, list == null ? EMPTY_ARRAY : list);
        }
        return list;
    }

    public Long getGroupId(String name) {
        for (Group group : getGroups()) {
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
            cache.clear();
        }
    }
}
