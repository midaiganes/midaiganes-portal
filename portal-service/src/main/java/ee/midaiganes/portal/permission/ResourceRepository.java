package ee.midaiganes.portal.permission;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.google.common.base.Preconditions;

import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

public class ResourceRepository {
    private final SingleVmCache cache;
    private final ResourceDao resourceDao;

    @Inject
    public ResourceRepository(ResourceDao resourceDao) {
        this.resourceDao = resourceDao;
        cache = SingleVmPoolUtil.getCache(ResourceRepository.class.getName());
    }

    public long getResourceId(@Nonnull String resource) throws ResourceNotFoundException {
        final String cacheKey = Preconditions.checkNotNull(resource);
        final Element el = cache.getElement(cacheKey);
        if (el != null) {
            Long val = el.get();
            if (val == null) {
                throw new ResourceNotFoundException("Invalid resource from cache: '" + resource + "'");
            }
            return val.longValue();
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
