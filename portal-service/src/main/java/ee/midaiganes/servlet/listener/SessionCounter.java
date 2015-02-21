package ee.midaiganes.servlet.listener;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.Utils;

public class SessionCounter implements SessionCounterMBean, HttpSessionListener, ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(SessionCounter.class);
    private final AtomicLong sessionCount = new AtomicLong();
    private static final String OBJECT_NAME = "ee.midaiganes:type=Sessions";

    private void registerMBean() throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        Utils.getInstance().getInstance(MBeanServer.class).registerMBean(this, new ObjectName(OBJECT_NAME));
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            registerMBean();
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            MBeanServer mbs = Utils.getInstance().getInstance(MBeanServer.class);
            ObjectName name = new ObjectName(OBJECT_NAME);
            if (mbs.isRegistered(name)) {
                mbs.unregisterMBean(name);
            }
        } catch (MBeanRegistrationException | InstanceNotFoundException | MalformedObjectNameException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sessionCount.incrementAndGet();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessionCount.decrementAndGet();
    }

    @Override
    public long getSessionCount() {
        return sessionCount.longValue();
    }
}
