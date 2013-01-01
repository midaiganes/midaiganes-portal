package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.Layout;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.util.StringUtil;

public class LayoutRowMapper implements RowMapper<Layout> {
	@Override
	public Layout mapRow(ResultSet rs, int rowNum) throws SQLException {
		Layout layout = new Layout();
		layout.setId(rs.getLong(1));
		layout.setLayoutSetId(rs.getLong(2));
		layout.setFriendlyUrl(rs.getString(3));
		layout.setPageLayoutId(rs.getString(4));
		String themeName = rs.getString(5);
		String themeContext = rs.getString(6);
		if (!StringUtil.isEmpty(themeName) && !StringUtil.isEmpty(themeContext)) {
			layout.setThemeName(new ThemeName(themeContext, themeName));
		}
		return layout;
	}
}