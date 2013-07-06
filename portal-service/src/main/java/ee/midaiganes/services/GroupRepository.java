package ee.midaiganes.services;

import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.Group;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.rowmapper.GroupRowMapper;
import ee.midaiganes.services.rowmapper.LongRowMapper;

// TODO fix caching
@Component(value = PortalConfig.GROUP_REPOSITORY)
public class GroupRepository {
	private static final String QRY_LOAD_GROUPS = "SELECT id, name, userGroup FROM Group_";
	private static final String QRY_LOAD_USER_GROUP_IDS = "SELECT groupId FROM UserGroup WHERE userId = ?";
	private static final String QRY_REMOVE_USER_GROUP = "DELETE FROM UserGroup WHERE userId = ? AND groupId = ?";
	private static final String QRY_ADD_USER_GROUP = "INSERT INTO UserGroup(userId, groupId) VALUES(?, ?)";
	private static final String QRY_ADD_GROUP = "INSERT INTO Group_ (name, userGroup) VALUES(?, ?)";

	private final Cache cache;
	private final GroupRowMapper groupRowMapper;
	private final LongRowMapper longRowMapper;

	private final JdbcTemplate jdbcTemplate;

	public GroupRepository(JdbcTemplate jdbcTemplate) {
		cache = SingleVmPool.getCache(GroupRepository.class.getName());
		groupRowMapper = new GroupRowMapper();
		longRowMapper = new LongRowMapper();
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Group> getGroups() {
		// TODO
		return loadGroups();
	}

	private List<Group> loadGroups() {
		return jdbcTemplate.query(QRY_LOAD_GROUPS, groupRowMapper);
	}

	public void addGroup(String name, boolean userGroup) {
		try {
			jdbcTemplate.update(QRY_ADD_GROUP, name, userGroup);
		} finally {
			cache.clear();
		}
	}

	public void addUserGroup(long userId, long groupId) {
		try {
			jdbcTemplate.update(QRY_ADD_USER_GROUP, userId, groupId);
		} finally {
			cache.remove(Long.toString(userId));
		}
	}

	public void removeUserGroup(long userId, long groupId) {
		try {
			jdbcTemplate.update(QRY_REMOVE_USER_GROUP, userId, groupId);
		} finally {
			// TODO
			cache.clear();
		}
	}

	public List<Long> getUserGroupIds(long userId) {
		String cacheKey = Long.toString(userId);
		Element el = cache.getElement(cacheKey);
		if (el != null) {
			return el.get();
		}
		List<Long> list = null;
		try {
			list = loadUserGroupIds(userId);
		} finally {
			cache.put(cacheKey, list == null || list.isEmpty() ? Collections.emptyList() : list);
		}
		return list;
	}

	public Long getGroupId(String name) {
		for (Group group : getGroups()) {
			if (group.getName().equals(name)) {
				return group.getId();
			}
		}
		return null;
	}

	private List<Long> loadUserGroupIds(long userId) {
		return jdbcTemplate.query(QRY_LOAD_USER_GROUP_IDS, longRowMapper, userId);
	}

	public void deleteGroup(long groupId) {
		try {
			jdbcTemplate.update("DELETE FROM Group_ WHERE id = ?", groupId);
		} finally {
			cache.clear();
		}
	}
}
