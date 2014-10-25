package ee.midaiganes.servlet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.beans.PortalModule;

public class GuiceContextLoaderListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(GuiceContextLoaderListener.class);
    public static final String SERVLET_ATTRIBUTE = GuiceContextLoaderListener.class.getName();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new PortalModule());
        sce.getServletContext().setAttribute(SERVLET_ATTRIBUTE, injector);
        BeanRepositoryUtil.setBeanRepository(new BeanRepositoryUtil.BeanRepository() {
            @Override
            public <A> void register(Class<A> clazz, A impl) {
                log.warn("Bean registration not supported");
            }

            @Override
            public <A> A getBean(Class<A> clazz) {
                return injector.getInstance(clazz);
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Guice context destroyed");
    }

}
