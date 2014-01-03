package ee.midaiganes.portletsservices.webcontent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.SingleVmPool;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;

@Repository(value = "webContentRepository")
public class WebContentRepository {

    @Resource(name = PortalConfig.PORTAL_JDBC_TEMPLATE)
    private JdbcTemplate jdbcTemplate;

    private final Cache cache;
    private final Cache webContentCache;
    private static final WebContentRowMapper webContentRowMapper = new WebContentRowMapper();

    public WebContentRepository() {
        cache = SingleVmPool.getCache(WebContentRepository.class.getName());
        webContentCache = SingleVmPool.getCache(WebContentRepository.class.getName() + "." + WebContent.class.getName());
    }

    public WebContent getWebContent(long id) {
        Element el = webContentCache.getElement(Long.toString(id));
        if (el != null) {
            return el.get();
        }
        WebContent webContent = null;
        try {
            List<WebContent> wcs = jdbcTemplate.query("SELECT id, layoutSetId, title, content, createDate FROM WebContent WHERE id = ?", webContentRowMapper, Long.valueOf(id));
            webContent = wcs.isEmpty() ? null : wcs.get(0);
        } finally {
            webContentCache.put(Long.toString(id), webContent);
        }
        return webContent;
    }

    public List<WebContent> getWebContents(long layoutSetId) {
        Element el = cache.getElement(Long.toString(layoutSetId));
        List<WebContent> wcs = el != null ? el.<List<WebContent>> get() : null;
        if (wcs == null) {
            try {
                wcs = jdbcTemplate
                        .query("SELECT id, layoutSetId, title, content, createDate FROM WebContent WHERE layoutSetId = ?", webContentRowMapper, Long.valueOf(layoutSetId));

            } finally {
                wcs = wcs == null ? Collections.<WebContent> emptyList() : wcs;
                cache.put(Long.toString(layoutSetId), wcs);
                for (WebContent wc : wcs) {
                    webContentCache.put(Long.toString(wc.getId()), wc);
                }
            }
        }
        return wcs;
    }

    public long addWebContent(final long layoutSet, final String title, final String content) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO WebContent(layoutSetId, title, content) VALUES (?, ?, ?)", new String[] { "id" });
                    ps.setLong(1, layoutSet);
                    ps.setString(2, title);
                    ps.setString(3, content);
                    return ps;
                }
            }, keyHolder);
            return keyHolder.getKey().longValue();
        } finally {
            cache.remove(Long.toString(layoutSet));
        }
    }

    public void updateWebContent(long id, String title, String content) {
        try {
            jdbcTemplate.update("UPDATE WebContent SET title = ?, content = ? WHERE id = ?", title, content, Long.valueOf(id));
        } finally {
            webContentCache.remove(Long.toString(id));
            cache.clear();
        }
    }

    private static class WebContentRowMapper implements RowMapper<WebContent> {
        @Override
        public WebContent mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new WebContent(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4), new DateTime(rs.getTimestamp(5).getTime()));
        }
    }
}
