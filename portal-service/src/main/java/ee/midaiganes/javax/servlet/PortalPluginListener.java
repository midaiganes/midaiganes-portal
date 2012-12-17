package ee.midaiganes.javax.servlet;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.services.ThemeRepository;

public class PortalPluginListener implements ServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(PortalPluginListener.class);

	@Resource(name = PortalConfig.PORTLET_REPOSITORY)
	private PortletRepository portletRepository;

	@Resource(name = PortalConfig.THEME_REPOSITORY)
	private ThemeRepository themeRepository;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
		autowire(sc);
		initPortlets(sc);
		initThemes(sc);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	private void autowire(ServletContext sc) {
		WebApplicationContextUtils.getRequiredWebApplicationContext(sc).getAutowireCapableBeanFactory().autowireBean(this);
	}

	private void initThemes(ServletContext sc) {
		try {
			InputStream themeXmlInputStream = sc.getResourceAsStream("/WEB-INF/midaiganes-theme.xml");
			log.debug("midaiganes-theme.xml exists ? {}", themeXmlInputStream != null);
			themeRepository.registerThemes(sc.getContextPath(), themeXmlInputStream);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void initPortlets(ServletContext sc) {
		try {
			InputStream portletInputStream = sc.getResourceAsStream("/WEB-INF/portlet.xml");
			log.debug("portlet.xml exists ? {}", portletInputStream != null);
			if (portletInputStream != null) {
				portletRepository.registerPortlets(sc, portletInputStream);
			}
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}
}
