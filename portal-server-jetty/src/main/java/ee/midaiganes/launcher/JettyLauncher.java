package ee.midaiganes.launcher;

import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyLauncher {
	public static void main(String... args) throws Exception {
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(10);
		threadPool.setMaxThreads(200);
		threadPool.setDetailedDump(false);
		Server server = new Server(threadPool);

		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecureScheme("https");
		httpConfig.setSecurePort(8443);
		httpConfig.setOutputBufferSize(32768);
		httpConfig.setRequestHeaderSize(8192);
		httpConfig.setResponseHeaderSize(8192);

		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		DefaultHandler defaultHandler = new DefaultHandler();
		handlers.setHandlers(new Handler[] { contexts, defaultHandler });

		server.setHandler(handlers);

		server.setStopAtShutdown(true);
		server.setStopTimeout(5000);
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);

		ServerConnector serverConnector = new ServerConnector(server, new ConnectionFactory[] { new HttpConnectionFactory(httpConfig) });
		serverConnector.setHost("0.0.0.0");
		serverConnector.setPort(8080);
		serverConnector.setIdleTimeout(30000);

		server.addConnector(serverConnector);

		DeploymentManager deploymentManager = new DeploymentManager();
		deploymentManager.setContexts(contexts);
		deploymentManager.setContextAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/servlet-api-[^/]*\\.jar$");

		WebAppProvider webappprovider = new WebAppProvider();
		webappprovider.setMonitoredDirName(System.getProperty("deploy.dir"));
		webappprovider.setDefaultsDescriptor(System.getProperty("project.basedir") + "/conf/jetty-webdefault.xml");
		webappprovider.setScanInterval(5);
		webappprovider.setExtractWars(true);
		webappprovider.setConfigurationManager(new PropertiesConfigurationManager());
		deploymentManager.addAppProvider(webappprovider);

		// RequestLogHandler requestLog = new RequestLogHandler();
		// NCSARequestLog requestLogImpl = new NCSARequestLog();
		// // TODO
		// requestLogImpl.setFilename("C:/jettyrequestlog/yyyy_mm_dd.request.log");
		// requestLogImpl.setFilenameDateFormat("yyyy_MM_dd");
		// requestLogImpl.setRetainDays(90);
		// requestLogImpl.setAppend(true);
		// requestLogImpl.setExtended(false);
		// requestLogImpl.setLogCookies(false);
		// requestLogImpl.setLogTimeZone("GMT");
		// requestLog.setRequestLog(requestLogImpl);
		//
		// handlers.addHandler(requestLog);

		server.addBean(deploymentManager);

		server.start();
		server.join();
	}
}
