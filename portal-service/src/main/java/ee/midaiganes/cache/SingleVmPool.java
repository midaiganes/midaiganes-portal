package ee.midaiganes.cache;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleVmPool {
    private static final Logger log = LoggerFactory.getLogger(SingleVmPool.class);
    private ConcurrentHashMap<String, SingleVmCache> caches = new ConcurrentHashMap<>();
    private static final String OBJECT_NAME = "ee.midaiganes:type=SingleVmPool,name=";

    public SingleVmCache getCache(String name) {
        synchronized (caches) {
            if (caches == null) {
                throw new IllegalStateException("Cache is destroyed!");
            }
            SingleVmCache cache = caches.get(name);
            if (cache == null) {
                cache = new SingleVmCache();
                registerMBean(cache, name);
            }
            return cache;
        }
    }

    @PreDestroy
    public void preDestroy() {
        synchronized (caches) {
            MBeanServer mbs = getPlatformMBeanServer();
            for (Map.Entry<String, SingleVmCache> entry : caches.entrySet()) {
                try {
                    entry.getValue().destroy();
                    ObjectName name = new ObjectName(OBJECT_NAME + entry.getKey());
                    if (mbs.isRegistered(name)) {
                        mbs.unregisterMBean(name);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            caches.clear();
            caches = null;
        }
    }

    private void registerMBean(SingleVmCache cache, String name) {
        try {
            getPlatformMBeanServer().registerMBean(cache, new ObjectName(OBJECT_NAME + name));
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

}
