package ee.midaiganes.portal.layout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import ee.midaiganes.portal.pagelayout.PageLayoutName;
import ee.midaiganes.portal.theme.ThemeName;
import ee.midaiganes.util.StringPool;

public class AddLayoutPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
    private static final Logger log = LoggerFactory.getLogger(AddLayoutPreparedStatementCreator.class);
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
        try {
            setParameters(ps);
            return ps;
        } catch (SQLException | RuntimeException e) {
            closePrepareStatement(ps);
            throw e;
        }
    }

    private void setParameters(PreparedStatement ps) throws SQLException {
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
    }

    private void closePrepareStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
