package ee.midaiganes.portal.permission;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ee.midaiganes.services.exceptions.ResourceNotFoundException;

public class ResourceRepository {
    private final LoadingCache<String, Optional<Long>> cache;

    @Inject
    public ResourceRepository(ResourceDao resourceDao) {
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new ResourceCacheLoader(resourceDao));
    }

    private static class ResourceCacheLoader extends CacheLoader<String, Optional<Long>> {
        private final ResourceDao resourceDao;

        public ResourceCacheLoader(ResourceDao resourceDao) {
            this.resourceDao = resourceDao;
        }

        @Override
        public Optional<Long> load(String resource) throws Exception {
            return Optional.fromNullable(resourceDao.loadResourceId(resource));
        }
    }

    public long getResourceId(@Nonnull String resource) throws ResourceNotFoundException {
        Optional<Long> val = cache.getUnchecked(resource);
        if (!val.isPresent()) {
            throw new ResourceNotFoundException("Invalid resource from cache: '" + resource + "'");
        }
        return val.get().longValue();
    }
}
