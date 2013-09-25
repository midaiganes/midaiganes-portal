package ee.midaiganes.services;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;
import ee.midaiganes.services.rowmapper.LongResultSetExtractor;

@Resource(name = PortalConfig.RESOURCE_REPOSITORY)
public class ResourceRepository {
	private static final String QRY_GET_RESOURCE_ID = "SELECT id FROM Resource WHERE resource = ?";
	private final Cache cache;
	private final LongResultSetExtractor resultSetExtractor;
	private final JdbcTemplate jdbcTemplate;

	public ResourceRepository(JdbcTemplate jdbcTemplate) {
		resultSetExtractor = new LongResultSetExtractor();
		cache = SingleVmPool.getCache(ResourceRepository.class.getName());
		this.jdbcTemplate = jdbcTemplate;
	}

	public long getResourceId(String resource) throws ResourceNotFoundException {
		final String cacheKey = resource;
		final Element el = cache.getElement(cacheKey);
		if (el != null) {
			return el.<Long> get().longValue();
		}
		Long value = null;
		try {
			value = loadResourceId(resource);
			if (value == null) {
				throw new ResourceNotFoundException("Invalid resource: '" + resource + "'");
			}
			return value.longValue();
		} finally {
			cache.put(cacheKey, value);
		}
	}

	private Long loadResourceId(String resource) {
		return jdbcTemplate.query(QRY_GET_RESOURCE_ID, resultSetExtractor, resource);
	}
}
