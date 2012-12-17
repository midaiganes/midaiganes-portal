package ee.midaiganes.servlet.listener;

import java.io.File;
import java.io.FileNotFoundException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.util.Log4jConfigurer;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.Log4jWebConfigurer;
import org.springframework.web.util.WebUtils;

public class Log4jConfigListener implements ServletContextListener {
	/** Parameter specifying the location of the log4j config file */
	private static final String CONFIG_LOCATION_PARAM = "log4jConfigLocation";

	/**
	 * Parameter specifying the refresh interval for checking the log4j config
	 * file
	 */
	private static final String REFRESH_INTERVAL_PARAM = "log4jRefreshInterval";

	/** Parameter specifying whether to expose the web app root system property */
	private static final String EXPOSE_WEB_APP_ROOT_PARAM = "log4jExposeWebAppRoot";
	private XMLWatchdog watchdog;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		initLogging(event.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		watchdog.stopWatching();
		try {
			watchdog.join();
		} catch (InterruptedException e) {
			event.getServletContext().log(e.getMessage(), e);
		}
		Log4jWebConfigurer.shutdownLogging(event.getServletContext());
	}

	private void initLogging(ServletContext servletContext) {
		// Expose the web app root system property.
		if (exposeWebAppRoot(servletContext)) {
			WebUtils.setWebAppRootSystemProperty(servletContext);
		}

		// Only perform custom log4j initialization in case of a config file.
		String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
		if (location != null) {
			// Perform actual log4j initialization; else rely on log4j's default
			// initialization.
			try {
				// Return a URL (e.g. "classpath:" or "file:") as-is;
				// consider a plain file path as relative to the web application
				// root directory.
				if (!ResourceUtils.isUrl(location)) {
					// Resolve system property placeholders before resolving
					// real path.
					location = SystemPropertyUtils.resolvePlaceholders(location);
					location = WebUtils.getRealPath(servletContext, location);
				}

				// Write log message to server log.
				servletContext.log("Initializing log4j from [" + location + "]");

				// Check whether refresh interval was specified.
				String intervalString = servletContext.getInitParameter(REFRESH_INTERVAL_PARAM);
				if (intervalString != null) {
					// Initialize with refresh interval, i.e. with log4j's
					// watchdog thread,
					// checking the file in the background.
					try {
						long refreshInterval = Long.parseLong(intervalString);
						initLogging(location, refreshInterval);
					} catch (NumberFormatException ex) {
						throw new IllegalArgumentException("Invalid 'log4jRefreshInterval' parameter: " + ex.getMessage());
					}
				} else {
					// Initialize without refresh check, i.e. without log4j's
					// watchdog thread.
					Log4jConfigurer.initLogging(location);
				}
			} catch (FileNotFoundException ex) {
				throw new IllegalArgumentException("Invalid 'log4jConfigLocation' parameter: " + ex.getMessage());
			}
		}
	}

	private void initLogging(String location, long refreshInterval) throws FileNotFoundException {
		String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
		File file = ResourceUtils.getFile(resolvedLocation);
		if (!file.exists()) {
			throw new FileNotFoundException("Log4j config file [" + resolvedLocation + "] not found");
		}
		watchdog = new XMLWatchdog(file.getAbsolutePath());
		watchdog.setDelay(refreshInterval);
		watchdog.start();
	}

	private static boolean exposeWebAppRoot(ServletContext servletContext) {
		String exposeWebAppRootParam = servletContext.getInitParameter(EXPOSE_WEB_APP_ROOT_PARAM);
		return (exposeWebAppRootParam == null || Boolean.valueOf(exposeWebAppRootParam));
	}

	private static class XMLWatchdog extends Thread {
		/**
		 * The default delay between every file modification check, set to 60
		 * seconds.
		 */
		static final public long DEFAULT_DELAY = 60000;
		/**
		 * The name of the file to observe for changes.
		 */
		private final String filename;

		/**
		 * The delay to observe between every check. By default set
		 * {@link #DEFAULT_DELAY}.
		 */
		private long delay = DEFAULT_DELAY;

		private final File file;
		private long lastModif = 0;
		private boolean warnedAlready = false;
		private boolean interrupted = false;

		protected XMLWatchdog(String filename) {
			super(XMLWatchdog.class.getName());
			this.filename = filename;
			file = new File(filename);
			setDaemon(true);
			checkAndConfigure();
		}

		private void doOnChange() {
			new DOMConfigurator().doConfigure(filename, LogManager.getLoggerRepository());
		}

		/**
		 * Set the delay to observe between each check of the file changes.
		 */

		private void setDelay(long delay) {
			this.delay = delay;
		}

		private void checkAndConfigure() {
			boolean fileExists;
			try {
				fileExists = file.exists();
			} catch (SecurityException e) {
				LogLog.warn("Was not allowed to read check file existance, file:[" + filename + "].");
				interrupted = true; // there is no point in continuing
				return;
			}

			if (fileExists) {
				long l = file.lastModified(); // this can also throw a
												// SecurityException
				if (l > lastModif) { // however, if we reached this point this
					lastModif = l; // is very unlikely.
					doOnChange();
					warnedAlready = false;
				}
			} else {
				if (!warnedAlready) {
					LogLog.debug("[" + filename + "] does not exist.");
					warnedAlready = true;
				}
			}
		}

		@Override
		public void run() {
			while (!interrupted) {
				long end = System.currentTimeMillis() + delay;
				do {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
				} while (!interrupted && end > System.currentTimeMillis());
				if (!interrupted) {
					checkAndConfigure();
				}
			}
		}

		public void stopWatching() {
			interrupted = true;
		}
	}
}
