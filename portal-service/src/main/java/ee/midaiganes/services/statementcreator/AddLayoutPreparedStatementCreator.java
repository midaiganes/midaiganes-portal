package ee.midaiganes.services.statementcreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import ee.midaiganes.model.PageLayoutName;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.util.StringPool;

public class AddLayoutPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
    private static final String[] ID_ARRAY = { StringPool.ID };

    private final long layoutSetId;
    private final String friendlyUrl;
    private final ThemeName themeName;
    private final PageLayoutName pageLayoutName;
    private final Long parentId;
    private final long defaultLayoutTitleLanguageId;
    private final String qry_add_layout;

    public AddLayoutPreparedStatementCreator(long layoutSetId, String friendlyUrl, ThemeName themeName, PageLayoutName pageLayoutName, Long parentId,
            long defaultLayoutTitleLanguageId) {
        this.layoutSetId = layoutSetId;
        this.friendlyUrl = friendlyUrl;
        this.themeName = themeName;
        this.pageLayoutName = pageLayoutName;
        this.parentId = parentId;
        this.defaultLayoutTitleLanguageId = defaultLayoutTitleLanguageId;
        this.qry_add_layout = "INSERT INTO Layout(layoutSetId, friendlyUrl, themeId, pageLayoutId, parentId, nr, defaultLayoutTitleLanguageId) VALUES(?, ?, (SELECT id FROM Theme WHERE name = ? AND context = ?), ?, ?, (SELECT c + 1 FROM (SELECT COUNT(1) AS c FROM Layout WHERE layoutSetId = ? AND "
                + (this.parentId == null ? "parentId IS NULL" : "parentId = ? ") + ") AS t), ?)";
    }

    @Override
    public String getSql() {
        return qry_add_layout;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(qry_add_layout, ID_ARRAY);
        int i = 1;
        ps.setLong(i++, layoutSetId);
        ps.setString(i++, friendlyUrl);
        if (themeName != null) {
            ps.setString(i++, themeName.getName());
            ps.setString(i++, themeName.getContext());
        } else {
            ps.setNull(i++, Types.VARCHAR);
            ps.setNull(i++, Types.VARCHAR);
        }
        ps.setString(i++, pageLayoutName.getFullName());
        if (parentId != null) {
            ps.setLong(i++, parentId.longValue());
        } else {
            ps.setNull(i++, Types.INTEGER);
        }
        ps.setLong(i++, layoutSetId);
        if (parentId != null) {
            ps.setLong(i++, parentId.longValue());
        }

        ps.setLong(i++, defaultLayoutTitleLanguageId);
        return ps;
    }
}
