package ee.midaiganes.portal.permission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.base.Preconditions;

import ee.midaiganes.services.rowmapper.LongResultSetExtractor;

public class ResourceDao {
    private static final String QRY_GET_RESOURCE_ID = "SELECT id FROM Resource WHERE resource = ?";
    private final LongResultSetExtractor resultSetExtractor;
    private final JdbcTemplate jdbcTemplate;

    public ResourceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Preconditions.checkNotNull(jdbcTemplate);
        this.resultSetExtractor = new LongResultSetExtractor();
    }

    @Nullable
    public Long loadResourceId(@Nonnull String resource) {
        return jdbcTemplate.query(QRY_GET_RESOURCE_ID, resultSetExtractor, resource);
    }
}
