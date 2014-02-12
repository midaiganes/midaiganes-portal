package ee.midaiganes.portal.layout;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;

public class LayoutResultSetExtractor implements ResultSetExtractor<Layout> {
	@Override
	public Layout extractData(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return LayoutRowMapper.getLayout(rs);
		}
		return null;
	}

}
