package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.LayoutTitle;

public class LayoutTitleRowMapper implements RowMapper<LayoutTitle> {
	@Override
	public LayoutTitle mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new LayoutTitle(rs.getLong(1), rs.getLong(3), rs.getLong(2), rs.getString(4));
	}
}