package ee.midaiganes.portal.layoutportlet;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

public class LayoutPortletDao {
    private static final String GET_LAYOUT_PORTLETS = "SELECT LayoutPortlet.id, LayoutPortlet.layoutId, LayoutPortlet.rowId, LayoutPortlet.portletInstanceId, PortletInstance.portletContext, PortletInstance.portletName, PortletInstance.windowID, LayoutPortlet.boxIndex FROM LayoutPortlet JOIN PortletInstance ON (LayoutPortlet.portletInstanceId = PortletInstance.id) WHERE layoutId = ?";
    private static final LayoutPortletRowMapper layoutPortletRowMapper = new LayoutPortletRowMapper();

    private final JdbcTemplate jdbcTemplate;

    public LayoutPortletDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LayoutPortlet> loadLayoutPortlets(long layoutId) {
        return jdbcTemplate.query(GET_LAYOUT_PORTLETS, layoutPortletRowMapper, Long.valueOf(layoutId));
    }

    public long addLayoutPortlet(long layoutId, long rowId, long portletInstanceId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new AddLayoutPortletPrepareStatementCreator(layoutId, rowId, portletInstanceId), keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Transactional
    public long addLayoutPortlet(long layoutId, long rowId, long portletInstanceId, int boxIndex) {
        long layoutPortletId = addLayoutPortlet(layoutId, rowId, portletInstanceId);
        moveLayoutPortlet(layoutPortletId, rowId, boxIndex);
        return layoutPortletId;
    }

    @Transactional
    public void moveLayoutPortlet(long layoutPortletId, long rowId, long boxIndex) {

        jdbcTemplate
                .update("UPDATE LayoutPortlet SET boxIndex = boxIndex + 1 WHERE id IN (SELECT * FROM ("
                        + "SELECT l2.id FROM LayoutPortlet AS l2 JOIN LayoutPortlet AS l3 ON (l2.layoutId = l3.layoutId) WHERE l3.id = ? AND l2.rowId = ? "
                        + "AND l2.id NOT IN (SELECT * FROM (SELECT l4.id FROM LayoutPortlet AS l4 JOIN LayoutPortlet AS l5 ON (l4.layoutId = l5.layoutId) WHERE l4.rowId = ? AND l5.id = ? ORDER BY l4.boxIndex ASC LIMIT ?) AS not_in)"
                        + ") AS t) ORDER BY boxIndex DESC", new Object[] { Long.valueOf(layoutPortletId), Long.valueOf(rowId), Long.valueOf(rowId), Long.valueOf(layoutPortletId),
                        Long.valueOf(boxIndex) });
        jdbcTemplate
                .update("UPDATE LayoutPortlet SET rowId = ?, boxIndex = IF(0 = ?, 0 , (SELECT MAX(t.boxIndex) FROM (SELECT l1.boxIndex FROM LayoutPortlet AS l1 JOIN LayoutPortlet AS l2 ON (l1.layoutId = l2.layoutId AND l1.id != l2.id) WHERE l2.id = ? AND l1.rowId = ? ORDER BY boxIndex ASC LIMIT ? ) AS t) + 1) WHERE id = ?",
                        new Object[] { Long.valueOf(rowId), Long.valueOf(boxIndex), Long.valueOf(layoutPortletId), Long.valueOf(rowId), Long.valueOf(boxIndex),
                                Long.valueOf(layoutPortletId) });
    }
}
