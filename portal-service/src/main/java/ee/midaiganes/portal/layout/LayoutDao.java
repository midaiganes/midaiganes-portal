package ee.midaiganes.portal.layout;

import ee.midaiganes.portal.pagelayout.PageLayoutName;
import ee.midaiganes.portal.theme.ThemeName;
import ee.midaiganes.services.rowmapper.TLongArrayListResultSetExtractor;
import ee.midaiganes.util.StringUtil;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class LayoutDao {
    private final JdbcTemplate jdbcTemplate;
    private static final LayoutTitleRowMapper layoutTitleRowMapper = new LayoutTitleRowMapper();
    private static final LayoutRowMapper layoutRowMapper = new LayoutRowMapper();
    private static final LayoutResultSetExtractor layoutResultSetExtractor = new LayoutResultSetExtractor();
    private static final String QRY_DELETE_LAYOUT = "DELETE FROM Layout WHERE id = ?";
    private static final String QRY_UPDATE_PAGE_LAYOUT = "UPDATE Layout SET pageLayoutId = ? WHERE id = ?";
    private static final String QRY_GET_LAYOUT_TITLES = "SELECT LayoutTitle.id, LayoutTitle.languageId, LayoutTitle.layoutId, LayoutTitle.title FROM LayoutTitle WHERE LayoutTitle.layoutId = ?";
    private static final String QRY_GET_LAYOUT_TITLES_BY_LAYOUTIDS = "SELECT LayoutTitle.id, LayoutTitle.languageId, LayoutTitle.layoutId, LayoutTitle.title FROM LayoutTitle WHERE LayoutTitle.layoutId IN";
    private static final String QRY_GET_LAYOUT_BY_ID = "SELECT Layout.id, Layout.layoutSetId, Layout.friendlyUrl, Layout.pageLayoutId, Theme.name, Theme.context, Layout.parentId, Layout.nr, Layout.defaultLayoutTitleLanguageId FROM Layout LEFT JOIN Theme ON (Layout.themeId = Theme.id) WHERE Layout.id = ?";
    private static final String QRY_GET_LAYOUTS_BY_IDS = "SELECT Layout.id, Layout.layoutSetId, Layout.friendlyUrl, Layout.pageLayoutId, Theme.name, Theme.context, Layout.parentId, Layout.nr, Layout.defaultLayoutTitleLanguageId FROM Layout LEFT JOIN Theme ON (Layout.themeId = Theme.id) WHERE Layout.id IN ";
    private static final String QRY_GET_LAYOUT_IDS_BY_LAYOUTSETID = "SELECT Layout.id FROM Layout WHERE layoutSetId = ?";
    private static final String QRY_UPDATE_LAYOUT = "UPDATE Layout SET friendlyUrl = ?, pageLayoutId = ?, parentId = ?, defaultLayoutTitleLanguageId = ? WHERE id = ?";
    private static final String QRY_MOVE_LAYOUT_UP = "UPDATE Layout AS l1 JOIN Layout AS l2 ON (l1.id = ? AND (l2.parentId = l1.parentId or (l2.parentId is null and l1.parentId is null)) and l2.nr = l1.nr - 1) SET l1.nr = l2.nr, l2.nr = l1.nr";
    private static final String QRY_MOVE_LAYOUT_DOWN = "UPDATE Layout AS l1 JOIN Layout AS l2 ON (l1.id = ? AND (l2.parentId = l1.parentId or (l2.parentId is null and l1.parentId is null)) and l2.nr = l1.nr + 1) SET l1.nr = l2.nr, l2.nr = l1.nr";

    @Inject
    public LayoutDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nonnull
    public List<LayoutTitle> loadLayoutTitles(long layoutId) {
        List<LayoutTitle> list = jdbcTemplate.query(QRY_GET_LAYOUT_TITLES, layoutTitleRowMapper, Long.valueOf(layoutId));
        return Preconditions.checkNotNull(list);
    }

    public TLongObjectHashMap<List<LayoutTitle>> loadLayoutTitles(ImmutableSet<Long> layoutIds) {
        return jdbcTemplate.query(QRY_GET_LAYOUT_TITLES_BY_LAYOUTIDS + "(" + StringUtil.repeat("?", ",", layoutIds.size()) + ")", layoutIds.toArray(),
                new LoadLayoutTitlesResultSetExtractor(layoutIds.size()));
    }

    private static class LoadLayoutTitlesResultSetExtractor implements ResultSetExtractor<TLongObjectHashMap<List<LayoutTitle>>> {
        private final int resultSize;

        public LoadLayoutTitlesResultSetExtractor(int resultSize) {
            this.resultSize = resultSize;
        }

        @Override
        public TLongObjectHashMap<List<LayoutTitle>> extractData(ResultSet rs) throws SQLException, DataAccessException {
            TLongObjectHashMap<List<LayoutTitle>> map = new TLongObjectHashMap<>(resultSize);
            while (rs.next()) {
                mapRow(map, rs);
            }
            return map;
        }

        private void mapRow(TLongObjectHashMap<List<LayoutTitle>> map, ResultSet rs) throws SQLException {
            LayoutTitle title = layoutTitleRowMapper.mapRow(rs, 0);
            List<LayoutTitle> titles = map.get(title.getLayoutId());
            if (titles == null) {
                titles = new ArrayList<>();
                map.put(title.getLayoutId(), titles);
            }
            titles.add(title);
        }
    }

    public Layout loadLayout(long layoutId) {
        return jdbcTemplate.query(QRY_GET_LAYOUT_BY_ID, layoutResultSetExtractor, Long.valueOf(layoutId));
    }

    public List<Layout> loadLayouts(List<Long> ids) {
        return jdbcTemplate.query(QRY_GET_LAYOUTS_BY_IDS + "(" + StringUtil.repeat("?", ",", ids.size()) + ")", ids.toArray(), layoutRowMapper);
    }

    public TLongArrayList loadLayoutIds(long layoutSetId) {
        return jdbcTemplate.query(QRY_GET_LAYOUT_IDS_BY_LAYOUTSETID, new TLongArrayListResultSetExtractor(), Long.valueOf(layoutSetId));
    }

    public void updateLayout(String friendlyUrl, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId, long id) {
        jdbcTemplate.update(QRY_UPDATE_LAYOUT, friendlyUrl, pageLayoutName.getFullName(), parentId, Long.valueOf(defaultLayoutTitleLanguageId), Long.valueOf(id));
    }

    public void addLayoutTitle(long layoutId, long languageId, String title) {
        jdbcTemplate.update("INSERT INTO LayoutTitle(layoutId, languageId, title) VALUES(?, ?, ?)", Long.valueOf(layoutId), Long.valueOf(languageId), title);
    }

    public void updateLayoutTitle(long layoutId, long languageId, String title) {
        jdbcTemplate.update("UPDATE LayoutTitle SET languageId = ?, title = ? WHERE layoutId = ?", Long.valueOf(languageId), title, Long.valueOf(layoutId));
    }

    public void deleteLayoutTitle(long layoutId, long languageId) {
        jdbcTemplate.update("DELETE LayoutTitle WHERE languageId = ? AND layoutId = ?", Long.valueOf(languageId), Long.valueOf(layoutId));
    }

    public void updatePageLayout(long layoutId, PageLayoutName pageLayoutName) {
        jdbcTemplate.update(QRY_UPDATE_PAGE_LAYOUT, pageLayoutName.getFullName(), Long.valueOf(layoutId));
    }

    public int moveLayoutsUp(long layoutSetId, Long parentId, long nr) {
        if (parentId == null) {
            return jdbcTemplate.update("UPDATE Layout set nr = nr - 1 WHERE layoutSetId = ? AND parentId IS NULL AND nr > ?", Long.valueOf(layoutSetId), Long.valueOf(nr));
        }
        return jdbcTemplate.update("UPDATE Layout set nr = nr - 1 WHERE layoutSetId = ? AND parentId = ? AND nr > ?", Long.valueOf(layoutSetId), parentId, Long.valueOf(nr));
    }

    public int deleteLayout(long layoutId) {
        return jdbcTemplate.update(QRY_DELETE_LAYOUT, Long.valueOf(layoutId));
    }

    public int moveLayoutUp(long layoutId) {
        return jdbcTemplate.update(QRY_MOVE_LAYOUT_UP, Long.valueOf(layoutId));
    }

    public int moveLayoutDown(long layoutId) {
        return jdbcTemplate.update(QRY_MOVE_LAYOUT_DOWN, Long.valueOf(layoutId));
    }

    public long addLayout(long layoutSetId, String friendlyUrl, ThemeName themeName, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new AddLayoutPreparedStatementCreator(layoutSetId, friendlyUrl, themeName, pageLayoutName, parentId, defaultLayoutTitleLanguageId), keyHolder);
        return keyHolder.getKey().longValue();
    }
}
