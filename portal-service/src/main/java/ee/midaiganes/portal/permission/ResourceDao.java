package ee.midaiganes.portal.permission;

import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.services.rowmapper.LongResultSetExtractor;

public class ResourceDao {
    private static final String QRY_GET_RESOURCE_ID = "SELECT id FROM Resource WHERE resource = ?";
    private final LongResultSetExtractor resultSetExtractor;
    private final JdbcTemplate jdbcTemplate;

    public ResourceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.resultSetExtractor = new LongResultSetExtractor();
    }

    public Long loadResourceId(String resource) {
        return jdbcTemplate.query(QRY_GET_RESOURCE_ID, resultSetExtractor, resource);
    }
}
