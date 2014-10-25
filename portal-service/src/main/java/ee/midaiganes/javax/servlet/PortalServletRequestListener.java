package ee.midaiganes.javax.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalServletRequestListener implements ServletRequestListener {
    private static final Logger log = LoggerFactory.getLogger(PortalServletRequestListener.class);

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        log.debug("Request destroyed.");
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        log.debug("Request initialized.");
    }

}
