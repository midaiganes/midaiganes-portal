package ee.midaiganes.portal.permission;

import javax.annotation.Resource;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

@Resource(name = PortalConfig.RESOURCE_REPOSITORY)
public class ResourceRepository {
    private final SingleVmCache cache;
    private final ResourceDao resourceDao;

    public ResourceRepository(ResourceDao resourceDao) {
        this.resourceDao = resourceDao;
        cache = SingleVmPoolUtil.getCache(ResourceRepository.class.getName());
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
