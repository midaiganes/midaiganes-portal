package ee.midaiganes.portal.portletinstance;

import java.security.SecureRandom;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import com.google.common.base.Charsets;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.util.StringPool;

@Resource(name = PortalConfig.PORTLET_INSTANCE_REPOSITORY)
public class PortletInstanceRepository {
    private static final Logger log = LoggerFactory.getLogger(PortletInstanceRepository.class);
    private static final SecureRandom random = new SecureRandom(new Object().toString().getBytes(Charsets.UTF_8));
    private final SingleVmCache cache = SingleVmPoolUtil.getCache(PortletInstanceRepository.class.getName());

    private final PortletInstanceDao portletInstanceDao;

    public PortletInstanceRepository(PortletInstanceDao portletInstanceDao) {
        this.portletInstanceDao = portletInstanceDao;
    }

    public PortletInstance getPortletInstance(long id) {
        String cacheKey = Long.toString(id);
        Element el = cache.getElement(cacheKey);
        if (el != null) {
            return el.get();
        }

        PortletInstance instance = null;
        try {
            instance = portletInstanceDao.loadPortletInstance(id);
        } finally {
            cache.put(cacheKey, instance);
        }
        return instance;
    }

    public void addDefaultPortletInstance(PortletName portletName) {
        try {
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
        Element el = cache.getElement(cacheKey);
        if (el != null) {
            return el.get();
        }
        PortletInstance instance = null;
        try {
            Long value = portletInstanceDao.loadPortletInstanceId(portletName, windowID);
            if (value != null) {
                instance = new PortletInstance(value.longValue(), new PortletNamespace(portletName, windowID));
            }
        } finally {
            cache.put(cacheKey, instance);
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
            cache.clear();
        }
    }

    public void deletePortletInstance(String windowID) {
        try {
            portletInstanceDao.deletePortletInstance(windowID);
        } finally {
            cache.clear();
        }
    }

    private String generateWindowID() {
        return Integer.toString(random.nextInt(1000000000));
    }
}
