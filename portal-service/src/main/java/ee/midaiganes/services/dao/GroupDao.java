package ee.midaiganes.services.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.model.Group;
import ee.midaiganes.services.rowmapper.GroupRowMapper;
import ee.midaiganes.services.rowmapper.TLongArrayListResultSetExtractor;
import gnu.trove.list.array.TLongArrayList;

public class GroupDao {
	private final JdbcTemplate jdbcTemplate;
	private final GroupRowMapper groupRowMapper;
	private final TLongArrayListResultSetExtractor longResultSetExtractor;

	public GroupDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.groupRowMapper = new GroupRowMapper();
		this.longResultSetExtractor = new TLongArrayListResultSetExtractor();
	}

	public List<Group> loadGroups() {
		return jdbcTemplate.query("SELECT id, name, userGroup FROM Group_", groupRowMapper);
	}

	public void addGroup(String name, boolean userGroup) {
		jdbcTemplate.update("INSERT INTO Group_ (name, userGroup) VALUES(?, ?)", name, Boolean.valueOf(userGroup));
	}

	public void addUserGroup(long userId, long groupId) {
		jdbcTemplate.update("INSERT INTO UserGroup(userId, groupId) VALUES(?, ?)", Long.valueOf(userId), Long.valueOf(groupId));
	}

	public void removeUserGroup(long userId, long groupId) {
		jdbcTemplate.update("DELETE FROM UserGroup WHERE userId = ? AND groupId = ?", Long.valueOf(userId), Long.valueOf(groupId));
	}

	public TLongArrayList loadUserGroupIds(long userId) {
		return jdbcTemplate.query("SELECT groupId FROM UserGroup WHERE userId = ?", longResultSetExtractor, Long.valueOf(userId));
	}

	public void deleteGroup(long groupId) {
		jdbcTemplate.update("DELETE FROM Group_ WHERE id = ?", Long.valueOf(groupId));
	}
}
