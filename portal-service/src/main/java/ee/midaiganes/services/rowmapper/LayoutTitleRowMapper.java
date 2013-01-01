package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.LayoutTitle;

public class LayoutTitleRowMapper implements RowMapper<LayoutTitle> {
	@Override
	public LayoutTitle mapRow(ResultSet rs, int rowNum) throws SQLException {
		LayoutTitle layoutTitle = new LayoutTitle();
		layoutTitle.setId(rs.getLong(1));
		layoutTitle.setLanguageId(rs.getString(2));
		layoutTitle.setLayoutId(rs.getLong(3));
		layoutTitle.setTitle(rs.getString(4));
		return layoutTitle;
	}
}