package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;

import ee.midaiganes.model.Layout;

public class LayoutResultSetExtractor implements ResultSetExtractor<Layout> {
	@Override
	public Layout extractData(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return LayoutRowMapper.getLayout(rs);
		}
		return null;
	}

}
