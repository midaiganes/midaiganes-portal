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
		return getLayout(rs);
	}

	public static Layout getLayout(ResultSet rs) throws SQLException {
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
		// TODO NULL OR 0 ?
		layout.setParentId(rs.getLong(7));
		layout.setNr(rs.getLong(8));
		layout.setDefaultLayoutTitleLanguageId(rs.getLong(9));
		return layout;
	}
}