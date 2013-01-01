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
		LayoutSet layoutSet = new LayoutSet();
		layoutSet.setId(rs.getLong(1));
		layoutSet.setVirtualHost(rs.getString(2));
		String themeName = rs.getString(3);
		String themeContext = rs.getString(4);
		if (!StringUtil.isEmpty(themeName) && !StringUtil.isEmpty(themeContext)) {
			layoutSet.setThemeName(new ThemeName(themeContext, themeName));
		}
		return layoutSet;
	}
}
