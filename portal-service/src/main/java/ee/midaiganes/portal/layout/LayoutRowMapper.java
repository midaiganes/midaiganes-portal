package ee.midaiganes.portal.layout;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.portal.theme.ThemeName;
import ee.midaiganes.util.StringUtil;

public class LayoutRowMapper implements RowMapper<Layout> {
    @Override
    public Layout mapRow(ResultSet rs, int rowNum) throws SQLException {
        return getLayout(rs);
    }

    public static Layout getLayout(ResultSet rs) throws SQLException {
        long id = rs.getLong(1);
        long layoutSetId = rs.getLong(2);
        String friendlyUrl = rs.getString(3);
        String pageLayoutId = rs.getString(4);
        String themeName = rs.getString(5);
        String themeContext = rs.getString(6);
        ThemeName theme = null;
        if (!StringUtil.isEmpty(themeName) && !StringUtil.isEmpty(themeContext)) {
            theme = new ThemeName(themeContext, themeName);
        }
        long parentId = rs.getLong(7);
        Long parent = rs.wasNull() ? null : Long.valueOf(parentId);
        long nr = rs.getLong(8);
        return new Layout(id, layoutSetId, friendlyUrl, theme, pageLayoutId, nr, parent, rs.getLong(9), null);
    }
}