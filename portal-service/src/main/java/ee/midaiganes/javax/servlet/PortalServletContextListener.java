package ee.midaiganes.javax.servlet;

import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.autodeploy.AutoDeployer;
import ee.midaiganes.util.PropsValues;

public class PortalServletContextListener implements ServletContextListener {

	private static final Logger log = LoggerFactory.getLogger(PortalServletContextListener.class);
	private Thread autoDeploy;
	private AutoDeployer autoDeployer;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			if (PropsValues.AUTODEPLOY_ENABLED) {
				autoDeployer = new AutoDeployer(Paths.get(PropsValues.AUTODEPLOY_DIR), Paths.get(PropsValues.WEBAPPS_DIR));// TODO
				autoDeploy = new Thread(autoDeployer, AutoDeployer.class.getName());
				autoDeploy.start();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (autoDeployer != null) {
			autoDeployer.stop();
			try {
				autoDeploy.join();
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

}
