package ee.midaiganes.javax.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.services.PageLayoutRegistryRepository;
import ee.midaiganes.services.PortletRegistryRepository;
import ee.midaiganes.services.ThemeRegistryRepository;
import ee.midaiganes.util.GuiceUtil;

public class PortalPluginListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(PortalPluginListener.class);

    @Inject
    private PortletRegistryRepository portletRepository;

    @Inject
    private ThemeRegistryRepository themeRepository;

    @Inject
    private PageLayoutRegistryRepository pageLayoutRepository;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        autowire(sc);
        initPortlets(sc);
        initThemes(sc);
        initPageLayouts(sc);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        unregisterThemes(sc);
        unregisterPortlets(sc);
        unregisterPageLayouts(sc);
    }

    private void unregisterPortlets(ServletContext sc) {
        try {
            portletRepository.unregisterPortlets(sc);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void unregisterThemes(ServletContext sc) {
        try {
            themeRepository.unregisterThemes(sc.getContextPath());
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void unregisterPageLayouts(ServletContext sc) {
        try {
            pageLayoutRepository.unregisterPageLayouts(sc.getContextPath());
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void autowire(ServletContext sc) {
        GuiceUtil.getInjector(getPortalServletContext(sc)).injectMembers(this);
    }

    private ServletContext getPortalServletContext(ServletContext sc) {
        // return sc.getContext(PropsValues.PORTAL_CONTEXT);TODO
        return sc;
    }

    private void initThemes(ServletContext sc) {
        try (InputStream themeXmlInputStream = sc.getResourceAsStream("/WEB-INF/midaiganes-theme.xml")) {
            log.debug("midaiganes-theme.xml exists ? {}", Boolean.valueOf(themeXmlInputStream != null));
            if (themeXmlInputStream != null) {
                try (BufferedInputStream bis = new BufferedInputStream(themeXmlInputStream)) {
                    themeRepository.registerThemes(sc.getContextPath(), bis);
                }
            }
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void initPageLayouts(ServletContext sc) {
        try (InputStream pageLayoutInputStream = sc.getResourceAsStream("/WEB-INF/midaiganes-layout.xml")) {
            if (pageLayoutInputStream != null) {
                try (BufferedInputStream bis = new BufferedInputStream(pageLayoutInputStream)) {
                    pageLayoutRepository.registerPageLayouts(sc.getContextPath(), bis);
                }
            }
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void initPortlets(ServletContext sc) {
        try (InputStream portletInputStream = sc.getResourceAsStream("/WEB-INF/portlet.xml")) {
            log.debug("portlet.xml exists ? {}", Boolean.valueOf(portletInputStream != null));
            if (portletInputStream != null) {
                try (BufferedInputStream bis = new BufferedInputStream(portletInputStream)) {
                    portletRepository.registerPortlets(sc, bis);
                }
            }
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
