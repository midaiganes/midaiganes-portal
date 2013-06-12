package ee.midaiganes.launcher;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
		QueuedThreadPool threadPool = new QueuedThreadPool(7, 2, 60000);
		// threadPool.setMinThreads(10);
		// threadPool.setMaxThreads(200);
		threadPool.setDetailedDump(false);
		threadPool.setName("jetty-server-thread-");
		Server server = new Server(threadPool);

		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecureScheme("https");
		httpConfig.setSecurePort(8443);
		httpConfig.setOutputBufferSize(32768);
		httpConfig.setRequestHeaderSize(8192);
		httpConfig.setResponseHeaderSize(8192);

		httpConfig.setSendServerVersion(false);

		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		DefaultHandler defaultHandler = new DefaultHandler();
		handlers.setHandlers(new Handler[] { contexts, defaultHandler });

		server.setHandler(handlers);

		server.setStopAtShutdown(true);
		server.setStopTimeout(5000);
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);

		int acceptors = 2;
		int selectors = 3;
		int requests = 5;
		int maxPoolSize = acceptors + selectors + requests;
		Executor serverConnectorExecutor = new ThreadPoolExecutor(maxPoolSize, maxPoolSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100),
				new SimpleThreadFactory("server-connector"));
		serverConnectorExecutor.hashCode();
		ServerConnector serverConnector = new ServerConnector(server, serverConnectorExecutor, null, null, acceptors, selectors,
				new ConnectionFactory[] { new HttpConnectionFactory(httpConfig) });
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

	private static final class SimpleThreadFactory implements ThreadFactory {
		private static final AtomicInteger poolNumber = new AtomicInteger(1);
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		public SimpleThreadFactory(String namePrefix) {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.namePrefix = namePrefix + "-pool-" + poolNumber.getAndIncrement() + "-thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon()) {
				t.setDaemon(false);
			}
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}
}
