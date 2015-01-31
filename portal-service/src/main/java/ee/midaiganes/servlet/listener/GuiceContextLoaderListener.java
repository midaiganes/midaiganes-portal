package ee.midaiganes.servlet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import ee.midaiganes.beans.Utils;

public class GuiceContextLoaderListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(GuiceContextLoaderListener.class);
    public static final String SERVLET_ATTRIBUTE = GuiceContextLoaderListener.class.getName();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final Injector injector = Utils.getInstance();
        sce.getServletContext().setAttribute(SERVLET_ATTRIBUTE, injector);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Guice context destroyed");
    }

}
