package ee.midaiganes.launcher;

import java.util.concurrent.BlockingQueue;
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
        int minThreads = 1;
        int acceptors = 1;// 2;
        int selectors = 1;// 30;
        int maxThreads = minThreads + (acceptors + selectors);
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, 60000);
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

        // The standard rule of thumb for the number of Accepters to configure
        // is one per CPU on a given machine.

        int requests = 5;
        int maxPoolSize = acceptors + selectors + requests;
        Executor serverConnectorExecutor = new JettyThreadPoolExecutor(maxPoolSize, maxPoolSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100 * maxPoolSize),
                new JettyThreadPoolExecutor.SimpleThreadFactory("server-connector"));

        JettyScheduledExecutorScheduler scheduler = new JettyScheduledExecutorScheduler();
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfig);
        httpConnectionFactory.setInputBufferSize(1024 * 8);
        ServerConnector serverConnector = new ServerConnector(server, serverConnectorExecutor, scheduler, null, acceptors, selectors,
                new ConnectionFactory[] { httpConnectionFactory });
        serverConnector.setHost("0.0.0.0");
        serverConnector.setPort(8080);
        serverConnector.setIdleTimeout(5000);// 30000

        server.addConnector(serverConnector);

        DeploymentManager deploymentManager = new DeploymentManager();
        deploymentManager.setContexts(contexts);
        deploymentManager.setContextAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/servlet-api-[^/]*\\.jar$");

        WebAppProvider webappprovider = new WebAppProvider();
        webappprovider.setMonitoredDirName(System.getProperty("deploy.dir"));
        webappprovider.setDefaultsDescriptor(System.getProperty("project.basedir") + "/conf/jetty-webdefault.xml");
        webappprovider.setScanInterval(30);
        webappprovider.setExtractWars(true);
        webappprovider.setConfigurationManager(new PropertiesConfigurationManager());
        deploymentManager.addAppProvider(webappprovider);

        server.addBean(deploymentManager);
        server.start();
        server.join();
    }

    private static final class JettyThreadPoolExecutor extends ThreadPoolExecutor {

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

        public JettyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }
    }
}
