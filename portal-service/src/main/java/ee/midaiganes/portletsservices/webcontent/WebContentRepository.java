package ee.midaiganes.portletsservices.webcontent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPool;

@Singleton
public class WebContentRepository {
    private final JdbcTemplate jdbcTemplate;

    private final SingleVmCache cache;
    private final SingleVmCache webContentCache;
    private static final WebContentRowMapper webContentRowMapper = new WebContentRowMapper();

    @Inject
    public WebContentRepository(JdbcTemplate jdbcTemplate, SingleVmPool singleVmPool) {
        this.jdbcTemplate = jdbcTemplate;
        cache = singleVmPool.getCache(WebContentRepository.class.getName());
        webContentCache = singleVmPool.getCache(WebContentRepository.class.getName() + "." + WebContent.class.getName());
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
                cacheWebContents(layoutSetId, wcs);
            }
        }
        return wcs;
    }

    private void cacheWebContents(long layoutSetId, List<WebContent> wcs) {
        cache.put(Long.toString(layoutSetId), wcs);
        for (WebContent wc : wcs) {
            webContentCache.put(Long.toString(wc.getId()), wc);
        }
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

    private static final class WebContentRowMapper implements RowMapper<WebContent> {
        @Override
        public WebContent mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new WebContent(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4), new DateTime(rs.getTimestamp(5).getTime()));
        }
    }
}
