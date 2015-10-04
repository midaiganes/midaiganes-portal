package ee.midaiganes.portal.permission;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceHasNoActionsException;

public class ResourceActionRepository {
    private final LoadingCache<Long, ImmutableList<ResourceActionPermission>> cache;

    @Inject
    public ResourceActionRepository(ResourceActionDao resourceActionDao) {
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new ResourceActionCacheLoader(resourceActionDao));
    }

    private static class ResourceActionCacheLoader extends CacheLoader<Long, ImmutableList<ResourceActionPermission>> {
        private final ResourceActionDao resourceActionDao;

        public ResourceActionCacheLoader(ResourceActionDao resourceActionDao) {
            this.resourceActionDao = resourceActionDao;
        }

        @Override
        public ImmutableList<ResourceActionPermission> load(Long resourceId) throws Exception {
            return ImmutableList.copyOf(resourceActionDao.loadResourceActionPermissions(resourceId.longValue()));
        }
    }

    public long getResourceActionPermission(long resourceId, String action) throws ResourceActionNotFoundException {
        ImmutableList<ResourceActionPermission> resourceActionPermissions = cache.getUnchecked(Long.valueOf(resourceId));
        if (!resourceActionPermissions.isEmpty()) {
            for (ResourceActionPermission resourceActionPermission : resourceActionPermissions) {
                if (resourceActionPermission.getResourceId() == resourceId && resourceActionPermission.getAction().equals(action)) {
                    return resourceActionPermission.getPermission();
                }
            }
            throw new ResourceActionNotFoundException(resourceId, action);
        }
        throw new ResourceHasNoActionsException(resourceId, action);
    }

    public ImmutableList<String> getResourceActions(long resourceId) {
        ImmutableList<ResourceActionPermission> resourceActionPermissions = cache.getUnchecked(Long.valueOf(resourceId));
        return ImmutableList.copyOf(Iterables.transform(resourceActionPermissions, r -> r.getAction()));
    }
}
