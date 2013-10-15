package ee.midaiganes.services;

import javax.annotation.Resource;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.dao.ResourceDao;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

@Resource(name = PortalConfig.RESOURCE_REPOSITORY)
public class ResourceRepository {
    private final Cache cache;
    private final ResourceDao resourceDao;

    public ResourceRepository(ResourceDao resourceDao) {
        this.resourceDao = resourceDao;
        cache = SingleVmPool.getCache(ResourceRepository.class.getName());
    }

    public long getResourceId(String resource) throws ResourceNotFoundException {
        final String cacheKey = resource;
        final Element el = cache.getElement(cacheKey);
        if (el != null) {
            return el.<Long> get().longValue();
        }
        Long value = null;
        try {
            value = resourceDao.loadResourceId(resource);
            if (value == null) {
                throw new ResourceNotFoundException("Invalid resource: '" + resource + "'");
            }
            return value.longValue();
        } finally {
            cache.put(cacheKey, value);
        }
    }
}
