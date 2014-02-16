package ee.midaiganes.javax.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.portal.pagelayout.PageLayoutRepository;
import ee.midaiganes.portal.theme.ThemeRepository;
import ee.midaiganes.services.PortletRepository;

public class PortalPluginListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(PortalPluginListener.class);

    @Resource(name = PortalConfig.PORTLET_REPOSITORY)
    private PortletRepository portletRepository;

    @Resource(name = PortalConfig.THEME_REPOSITORY)
    private ThemeRepository themeRepository;

    @Resource(name = PortalConfig.PAGE_LAYOUT_REPOSITORY)
    private PageLayoutRepository pageLayoutRepository;

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
        WebApplicationContextUtils.getRequiredWebApplicationContext(getPortalServletContext(sc)).getAutowireCapableBeanFactory().autowireBean(this);
    }

    private ServletContext getPortalServletContext(ServletContext sc) {
        // return sc.getContext(PropsValues.PORTAL_CONTEXT);TODO
        return sc;
    }

    private void initThemes(ServletContext sc) {
        try (InputStream themeXmlInputStream = sc.getResourceAsStream("/WEB-INF/midaiganes-theme.xml")) {
            log.debug("midaiganes-theme.xml exists ? {}", Boolean.valueOf(themeXmlInputStream != null));
            if (themeXmlInputStream != null) {
                themeRepository.registerThemes(sc.getContextPath(), themeXmlInputStream);
            }
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void initPageLayouts(ServletContext sc) {
        try (InputStream pageLayoutInputStream = sc.getResourceAsStream("/WEB-INF/midaiganes-layout.xml")) {
            if (pageLayoutInputStream != null) {
                pageLayoutRepository.registerPageLayouts(sc.getContextPath(), pageLayoutInputStream);
            }
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void initPortlets(ServletContext sc) {
        try (InputStream portletInputStream = sc.getResourceAsStream("/WEB-INF/portlet.xml")) {
            log.debug("portlet.xml exists ? {}", Boolean.valueOf(portletInputStream != null));
            if (portletInputStream != null) {
                portletRepository.registerPortlets(sc, portletInputStream);
            }
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
