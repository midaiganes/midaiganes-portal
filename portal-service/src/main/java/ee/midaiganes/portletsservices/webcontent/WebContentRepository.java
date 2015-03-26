package ee.midaiganes.portletsservices.webcontent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ee.midaiganes.fn.Optionals;
import ee.midaiganes.services.rowmapper.LongRowMapper;
import ee.midaiganes.util.StringUtil;

@Singleton
public class WebContentRepository {
    private final JdbcTemplate jdbcTemplate;

    private final LoadingCache<Long, ImmutableList<WebContent>> cache;
    private final LoadingCache<Long, Optional<WebContent>> webContentCache;
    private static final WebContentRowMapper webContentRowMapper = new WebContentRowMapper();

    @Inject
    public WebContentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.webContentCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, Optional<WebContent>>() {
            @Override
            public Optional<WebContent> load(Long id) {
                List<WebContent> wcs = jdbcTemplate.query("SELECT id, layoutSetId, title, content, createDate FROM WebContent WHERE id = ?", webContentRowMapper, id);
                return Optional.fromNullable(wcs.isEmpty() ? null : wcs.get(0));
            }

            @Override
            public Map<Long, Optional<WebContent>> loadAll(Iterable<? extends Long> keys) {
                Object[] keysArray = ImmutableList.copyOf(keys).toArray();
                List<WebContent> wcs = jdbcTemplate.query(
                        "SELECT id, layoutSetId, title, content, createDate FROM WebContent WHERE id in (" + StringUtil.repeat("?", ",", keysArray.length) + ")", keysArray,
                        webContentRowMapper);
                return Maps.uniqueIndex(Lists.transform(wcs, Optionals.of()), new Function<Optional<WebContent>, Long>() {
                    @Override
                    @Nullable
                    public Long apply(@Nullable Optional<WebContent> input) {
                        return Long.valueOf(input.get().getId());
                    }
                });
            }
        });
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, ImmutableList<WebContent>>() {
            @Override
            public ImmutableList<WebContent> load(Long layoutSetId) throws Exception {
                List<Long> list = jdbcTemplate.query("SELECT id FROM WebContent WHERE layoutSetId = ?", new LongRowMapper(), layoutSetId);
                return ImmutableList.copyOf(Collections2.transform(webContentCache.getAll(list).values(), Optionals.get()));
            }

        });
    }

    public WebContent getWebContent(long id) {
        return webContentCache.getUnchecked(Long.valueOf(id)).orNull();
    }

    public ImmutableList<WebContent> getWebContents(long layoutSetId) {
        return cache.getUnchecked(Long.valueOf(layoutSetId));
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
            cache.invalidate(Long.valueOf(layoutSet));
        }
    }

    public void updateWebContent(long id, String title, String content) {
        try {
            jdbcTemplate.update("UPDATE WebContent SET title = ?, content = ? WHERE id = ?", title, content, Long.valueOf(id));
        } finally {
            webContentCache.invalidate(Long.valueOf(id));
            cache.invalidateAll();
        }
    }

    private static final class WebContentRowMapper implements RowMapper<WebContent> {
        @Override
        public WebContent mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new WebContent(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4), new DateTime(rs.getTimestamp(5).getTime()));
        }
    }
}
