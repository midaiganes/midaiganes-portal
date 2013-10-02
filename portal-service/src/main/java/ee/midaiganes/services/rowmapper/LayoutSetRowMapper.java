package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.LayoutSet;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.util.StringUtil;

public class LayoutSetRowMapper implements RowMapper<LayoutSet> {
	@Override
	public LayoutSet mapRow(ResultSet rs, int rowNum) throws SQLException {
		long id = rs.getLong(1);
		String virtualHost = rs.getString(2);
		String themeName = rs.getString(3);
		String themeContext = rs.getString(4);
		if (!StringUtil.isEmpty(themeName) && !StringUtil.isEmpty(themeContext)) {
			return new LayoutSet(id, virtualHost, new ThemeName(themeContext, themeName));
		}
		return new LayoutSet(id, virtualHost, null);
	}
}
