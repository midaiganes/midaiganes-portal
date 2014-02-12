package ee.midaiganes.portal.group;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class GroupRowMapper implements RowMapper<Group> {
	@Override
	public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Group(rs.getLong(1), rs.getString(2), rs.getBoolean(3));
	}
}