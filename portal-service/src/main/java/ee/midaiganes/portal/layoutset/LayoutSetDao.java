package ee.midaiganes.portal.layoutset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.google.common.base.Preconditions;

import ee.midaiganes.portal.theme.ThemeName;
import ee.midaiganes.services.rowmapper.TLongArrayListResultSetExtractor;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;
import gnu.trove.list.array.TLongArrayList;

public class LayoutSetDao {
    private static final String QRY_UPDATE_LAYOUT_SET = "UPDATE LayoutSet SET virtualHost = ?, themeId = (SELECT Theme.id FROM Theme WHERE Theme.context = ? AND Theme.name = ?) WHERE id = ?";
    private static final String ADD_LAYOUT_SET = "INSERT INTO LayoutSet(virtualHost, themeId) VALUES(?, (SELECT Theme.id FROM Theme WHERE Theme.context = ? AND Theme.name = ?))";
    private static final String GET_LAYOUT_SET_BY_VIRTUAL_HOST = "SELECT LayoutSet.id, LayoutSet.virtualHost, Theme.name, Theme.context FROM LayoutSet LEFT JOIN Theme ON (LayoutSet.themeId = Theme.id) WHERE virtualHost = ?";
    private static final String GET_LAYOUT_SET_IDS = "SELECT LayoutSet.id FROM LayoutSet";
    private static final String GET_LAYOUT_SET_BY_ID = "SELECT LayoutSet.id, LayoutSet.virtualHost, Theme.name, Theme.context FROM LayoutSet LEFT JOIN Theme ON (LayoutSet.themeId = Theme.id) WHERE LayoutSet.id = ?";
    private static final String GET_LAYOUT_SETS_BY_IDS = "SELECT LayoutSet.id, LayoutSet.virtualHost, Theme.name, Theme.context FROM LayoutSet LEFT JOIN Theme ON (LayoutSet.themeId = Theme.id) WHERE LayoutSet.id IN ";
    private static final String[] ID_ARRAY = new String[] { StringPool.ID };
    private static final LayoutSetResultSetExtractor layoutSetResultSetExtractor = new LayoutSetResultSetExtractor();

    private final JdbcTemplate jdbcTemplate;

    @Inject
    public LayoutSetDao(JdbcTemplate jdbcTemplate) {
        Preconditions.checkNotNull(jdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
    }

    public TLongArrayList getLayoutSetIds() {
        return jdbcTemplate.query(GET_LAYOUT_SET_IDS, new TLongArrayListResultSetExtractor());
    }

    @Nullable
    public LayoutSet getLayoutSet(String virtualHost) {
        return jdbcTemplate.query(GET_LAYOUT_SET_BY_VIRTUAL_HOST, layoutSetResultSetExtractor, virtualHost);
    }

    @Nullable
    public LayoutSet getLayoutSet(long id) {
        return jdbcTemplate.query(GET_LAYOUT_SET_BY_ID, layoutSetResultSetExtractor, Long.valueOf(id));
    }

    public List<LayoutSet> getLayoutSets(List<Long> ids) {
        return jdbcTemplate.query(GET_LAYOUT_SETS_BY_IDS + "(" + StringUtil.repeat("?", ",", ids.size()) + ")", ids.toArray(), new LayoutSetRowMapper());
    }

    public void updateLayoutSet(long id, String virtualHost, ThemeName themeName) {
        jdbcTemplate.update(QRY_UPDATE_LAYOUT_SET, virtualHost, themeName.getContext(), themeName.getName(), Long.valueOf(id));
    }

    public long addLayoutSet(final String virtualHost, final ThemeName themeName) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(ADD_LAYOUT_SET, ID_ARRAY);
                ps.setString(1, virtualHost);
                ps.setString(2, themeName.getContext());
                ps.setString(3, themeName.getName());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

}
