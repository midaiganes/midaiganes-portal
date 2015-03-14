package ee.midaiganes.portal.portletinstance;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.util.StringPool;

public class PortletInstanceRepository {
    private static final Logger log = LoggerFactory.getLogger(PortletInstanceRepository.class);
    private static final SecureRandom random = new SecureRandom(new Object().toString().getBytes(Charsets.UTF_8));
    private final Cache<String, Optional<PortletInstance>> cache;

    private final PortletInstanceDao portletInstanceDao;

    @Inject
    public PortletInstanceRepository(PortletInstanceDao portletInstanceDao) {
        this.portletInstanceDao = portletInstanceDao;
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).<String, Optional<PortletInstance>> build();
    }

    public PortletInstance getPortletInstance(long id) {
        String cacheKey = Long.toString(id);

        Optional<PortletInstance> el = cache.getIfPresent(cacheKey);
        if (el != null) {
            return el.orNull();
        }

        PortletInstance instance = null;
        try {
            instance = portletInstanceDao.loadPortletInstance(id);
        } finally {
            cache.put(cacheKey, Optional.fromNullable(instance));
        }
        return instance;
    }

    public void addDefaultPortletInstance(PortletName portletName) {
        try {
            // TODO fix duplicatekeyexception @ transactional
            addPortletInstance(portletName, StringPool.DEFAULT_PORTLET_WINDOWID);
        } catch (DuplicateKeyException e) {
            log.debug(e.getMessage(), e);
        }
    }

    public PortletInstance getDefaultPortletInstance(PortletName portletName) {
        return getPortletInstance(portletName, StringPool.DEFAULT_PORTLET_WINDOWID);
    }

    public PortletInstance getPortletInstance(PortletName portletName, String windowID) {
        String cacheKey = "getPortletInstance" + portletName.getFullName() + "#" + windowID;
        Optional<PortletInstance> el = cache.getIfPresent(cacheKey);
        if (el != null) {
            return el.orNull();
        }
        PortletInstance instance = null;
        try {
            Long value = portletInstanceDao.loadPortletInstanceId(portletName, windowID);
            if (value != null) {
                instance = new PortletInstance(value.longValue(), new PortletNamespace(portletName, windowID));
            }
        } finally {
            cache.put(cacheKey, Optional.fromNullable(instance));
        }
        return instance;
    }

    public List<PortletInstance> getDefaultPortletInstances() {
        return portletInstanceDao.getDefaultPortletInstances();
    }

    public long addPortletInstance(PortletName portletName) {
        return addPortletInstance(portletName, generateWindowID());
    }

    public long addPortletInstance(final PortletName portletName, final String windowID) {
        try {
            return portletInstanceDao.addPortletInstance(portletName, windowID);
        } finally {
            cache.invalidateAll();
        }
    }

    public void deletePortletInstance(String windowID) {
        try {
            portletInstanceDao.deletePortletInstance(windowID);
        } finally {
            cache.invalidateAll();
        }
    }

    private String generateWindowID() {
        return Integer.toString(random.nextInt(1000000000));
    }
}
