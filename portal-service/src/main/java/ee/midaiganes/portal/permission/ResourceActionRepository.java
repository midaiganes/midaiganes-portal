package ee.midaiganes.portal.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceHasNoActionsException;

public class ResourceActionRepository {
    private final SingleVmCache cache;
    private final ResourceActionDao resourceActionDao;

    @Inject
    public ResourceActionRepository(ResourceActionDao resourceActionDao) {
        this.cache = SingleVmPoolUtil.getCache(ResourceActionRepository.class.getName());
        this.resourceActionDao = resourceActionDao;
    }

    public long getResourceActionPermission(long resourceId, String action) throws ResourceActionNotFoundException {
        List<ResourceActionPermission> resourceActionPermissions = getResourceActionPermissions(resourceId);
        if (resourceActionPermissions != null) {
            for (ResourceActionPermission resourceActionPermission : resourceActionPermissions) {
                if (resourceActionPermission.getResourceId() == resourceId && resourceActionPermission.getAction().equals(action)) {
                    return resourceActionPermission.getPermission();
                }
            }
            throw new ResourceActionNotFoundException(resourceId, action);
        }
        throw new ResourceHasNoActionsException(resourceId, action);
    }

    public List<String> getResourceActions(long resourceId) {
        List<ResourceActionPermission> resourceActionPermissions = getResourceActionPermissions(resourceId);
        if (resourceActionPermissions != null) {
            List<String> actions = new ArrayList<>(resourceActionPermissions.size());
            for (ResourceActionPermission resourceActionPermission : resourceActionPermissions) {
                actions.add(resourceActionPermission.getAction());
            }
            return actions;
        }
        return Collections.emptyList();
    }

    private List<ResourceActionPermission> getResourceActionPermissions(long resourceId) {
        String cacheKey = Long.toString(resourceId);
        Element el = cache.getElement(cacheKey);
        if (el == null) {
            List<ResourceActionPermission> list = null;
            try {
                list = resourceActionDao.loadResourceActionPermissions(resourceId);
            } finally {
                cache.put(cacheKey, list == null || list.isEmpty() ? Collections.emptyList() : list);
            }
            return list;
        }
        return el.get();
    }

}
