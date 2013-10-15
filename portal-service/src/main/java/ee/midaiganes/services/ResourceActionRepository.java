package ee.midaiganes.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.ResourceActionPermission;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.dao.ResourceActionDao;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceHasNoActionsException;

@Resource(name = PortalConfig.RESOURCE_ACTION_REPOSITORY)
public class ResourceActionRepository {
    private final Cache cache;
    private final ResourceActionDao resourceActionDao;

    public ResourceActionRepository(ResourceActionDao resourceActionDao) {
        cache = SingleVmPool.getCache(ResourceActionRepository.class.getName());
        this.resourceActionDao = resourceActionDao;
    }

    public long getResourceActionPermission(long resourceId, String action) throws ResourceActionNotFoundException {
        List<ResourceActionPermission> list = getResourceActionPermissions(resourceId);
        if (list != null) {
            for (ResourceActionPermission item : list) {
                if (item.getResourceId() == resourceId && item.getAction().equals(action)) {
                    return item.getPermission();
                }
            }
            throw new ResourceActionNotFoundException(resourceId, action);
        }
        throw new ResourceHasNoActionsException(resourceId, action);
    }

    public List<String> getResourceActions(long resourceId) {
        List<ResourceActionPermission> list = getResourceActionPermissions(resourceId);
        if (list != null) {
            List<String> actions = new ArrayList<>(list.size());
            for (ResourceActionPermission ra : list) {
                actions.add(ra.getAction());
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
