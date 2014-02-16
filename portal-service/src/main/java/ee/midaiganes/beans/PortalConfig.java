package ee.midaiganes.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.factory.PortletURLFactory;
import ee.midaiganes.portal.group.GroupDao;
import ee.midaiganes.portal.group.GroupRepository;
import ee.midaiganes.portal.layout.LayoutDao;
import ee.midaiganes.portal.layout.LayoutRepository;
import ee.midaiganes.portal.layoutportlet.LayoutPortletDao;
import ee.midaiganes.portal.layoutportlet.LayoutPortletRepository;
import ee.midaiganes.portal.layoutset.LayoutSetDao;
import ee.midaiganes.portal.layoutset.LayoutSetRepository;
import ee.midaiganes.portal.pagelayout.PageLayoutRepository;
import ee.midaiganes.portal.permission.PermissionDao;
import ee.midaiganes.portal.permission.PermissionRepository;
import ee.midaiganes.portal.permission.PermissionService;
import ee.midaiganes.portal.permission.ResourceActionDao;
import ee.midaiganes.portal.permission.ResourceActionRepository;
import ee.midaiganes.portal.permission.ResourceDao;
import ee.midaiganes.portal.permission.ResourceRepository;
import ee.midaiganes.portal.portletinstance.PortletInstanceDao;
import ee.midaiganes.portal.portletinstance.PortletInstanceRepository;
import ee.midaiganes.portal.theme.ThemeRepository;
import ee.midaiganes.portal.user.UserDao;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.secureservices.SecureLayoutRepository;
import ee.midaiganes.secureservices.SecurePortletRepository;
import ee.midaiganes.services.DbInstallService;
import ee.midaiganes.services.LanguageRepository;
import ee.midaiganes.services.PortletPreferencesRepository;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.services.RequestParser;
import ee.midaiganes.services.ThemeVariablesService;

@Configuration(value = "portalConfig")
@PropertySource(value = "classpath:portal.properties", name = "portalProperties")
// @EnableLoadTimeWeaving(aspectjWeaving = AspectJWeaving.ENABLED)
// @EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class PortalConfig implements TransactionManagementConfigurer {
    private static final Logger log = LoggerFactory.getLogger(PortalConfig.class);
    public static final String PORTAL_DATASOURCE = "portalDataSource";
    public static final String PORTLET_REPOSITORY = "portletRepository";
    public static final String SECURE_PORTLET_REPOSITORY = "securePortletRepository";
    public static final String SECURE_LAYOUT_REPOSITORY = "secureLayoutRepository";
    public static final String PORTLET_PREFERENCES_REPOSITORY = "portletPreferencesRepository";
    public static final String PORTLET_INSTANCE_REPOSITORY = "portletInstanceRepository";
    public static final String LANGUAGE_REPOSITORY = "languageRepository";
    public static final String PAGE_LAYOUT_REPOSITORY = "pageLayoutRepository";
    public static final String LAYOUT_REPOSITORY = "layoutRepository";
    public static final String LAYOUT_PORTLET_REPOSITORY = "layoutPortletRepository";
    @Deprecated
    public static final String SERVLET_CONTEXT_RESOURCE_REPOSITORY = "servletContextResourceRepository";
    public static final String THEME_REPOSITORY = "themeRepository";
    public static final String RESOURCE_REPOSITORY = "resourceRepository";
    public static final String PERMISSION_REPOSITORY = "permissionRepository";
    public static final String RESOURCE_ACTION_REPOSITORY = "resourceActionRepository";
    public static final String PERMISSION_SERVICE = "permissionService";
    public static final String GROUP_REPOSITORY = "groupRepository";
    public static final String PORTAL_JDBC_TEMPLATE = "portalJdbcTemplate";
    public static final String LAYOUT_SET_REPOSITORY = "layoutSetRepository";
    public static final String USER_REPOSITORY = "userRepository";
    public static final String DB_INSTALL_SERVICE = "dbInstallService";
    public static final String TXMANAGER = "txManager";

    @Value("${jdbc.driverClassName}")
    private String driver;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.maxActive}")
    private int maxActive;

    @Value("${txManager.defaultTimeout}")
    private int txManagerDefaultTimeout;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private PortletInstanceRepository portletInstanceRepository;

    @Autowired
    private SecureLayoutRepository secureLayoutRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LayoutPortletRepository layoutPortletRepository;

    @Autowired
    private PortletRepository portletRepository;

    @Autowired
    private PageLayoutRepository pageLayoutRepository;

    @Autowired
    private LayoutRepository layoutRepository;

    @Autowired
    private SecurePortletRepository securePortletRepository;

    @Autowired
    private DbInstallService dbInstallService;

    @Autowired
    private LayoutSetRepository layoutSetRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ResourceActionRepository resourceActionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    @Qualifier("testSetup")
    private Object test;

    @PostConstruct
    public void postConstruct() {
        if (test == null) {
            throw new IllegalStateException("test bean is missing");
        }
        BeanRepositoryUtil.register(PermissionRepository.class, permissionRepository);
        BeanRepositoryUtil.register(LanguageRepository.class, languageRepository);
        BeanRepositoryUtil.register(PortletInstanceRepository.class, portletInstanceRepository);
        BeanRepositoryUtil.register(SecureLayoutRepository.class, secureLayoutRepository);
        BeanRepositoryUtil.register(ThemeVariablesService.class, new ThemeVariablesService(PortletURLFactory.getInstance(), secureLayoutRepository));
        BeanRepositoryUtil.register(UserRepository.class, userRepository);
        BeanRepositoryUtil.register(RequestParser.class, new RequestParser());
        BeanRepositoryUtil.register(LayoutPortletRepository.class, layoutPortletRepository);
        BeanRepositoryUtil.register(PortletRepository.class, portletRepository);
        BeanRepositoryUtil.register(PageLayoutRepository.class, pageLayoutRepository);
        BeanRepositoryUtil.register(LayoutRepository.class, layoutRepository);
        BeanRepositoryUtil.register(SecurePortletRepository.class, securePortletRepository);
        BeanRepositoryUtil.register(DbInstallService.class, dbInstallService);
        BeanRepositoryUtil.register(LayoutSetRepository.class, layoutSetRepository);
        BeanRepositoryUtil.register(ThemeRepository.class, themeRepository);
        BeanRepositoryUtil.register(GroupRepository.class, groupRepository);
        BeanRepositoryUtil.register(ResourceActionRepository.class, resourceActionRepository);
        BeanRepositoryUtil.register(ResourceRepository.class, resourceRepository);
    }

    @PreDestroy
    public void preDestroy() {
        SingleVmPoolUtil.destroy();
    }

    @Bean(name = "testSetup", autowire = Autowire.NO)
    public Object testSetup() {
        log.info("testing spring setup...");
        try {
            testSetup2();
            testSetup2();
            // TODO test transaction
        } catch (RuntimeException e) {
            throw new RuntimeException("spring setup is incorrect", e);
        }
        log.info("spring setup looks ok");
        return new Object();
    }

    private static boolean testSetup = false;

    @Bean(name = "testSetup2", autowire = Autowire.NO)
    public Object testSetup2() {
        if (testSetup) {
            throw new IllegalStateException("Not allowed to call testSetup2 more than once. Spring proxy not working?");
        }
        testSetup = true;
        return new Object();
    }

    @Bean(name = DB_INSTALL_SERVICE, autowire = Autowire.NO)
    public DbInstallService dbInstallService() {
        return new DbInstallService(portalJdbcTemplate());
    }

    @Bean(name = USER_REPOSITORY, autowire = Autowire.NO)
    public UserRepository userRepository() {
        return new UserRepository(new UserDao(portalJdbcTemplate()));
    }

    @Bean(name = LAYOUT_SET_REPOSITORY, autowire = Autowire.NO)
    public LayoutSetRepository layoutSetRepository() {
        return new LayoutSetRepository(new LayoutSetDao(portalJdbcTemplate()));
    }

    @Bean(name = "propertySourcesPlaceholderConfigurer", autowire = Autowire.NO)
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = TXMANAGER, autowire = Autowire.NO)
    public DataSourceTransactionManager dataSourceTransactionManager() {
        DataSourceTransactionManager txManager = new DataSourceTransactionManager(portalDataSource());
        txManager.setDefaultTimeout(txManagerDefaultTimeout);
        return txManager;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return dataSourceTransactionManager();
    }

    @Bean(name = PORTAL_DATASOURCE, destroyMethod = "close", autowire = Autowire.NO)
    public DataSource portalDataSource() {
        final org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaxActive(maxActive);
        ds.setMaxIdle(maxActive);
        ds.setMinIdle(1);
        ds.setInitialSize(1);
        ds.setMaxWait(1000);
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(true);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(5000);
        ds.setValidationInterval(30000);
        ds.setValidationQuery("SELECT 1");
        ds.setValidationQueryTimeout(1);
        ds.setJmxEnabled(true);
        ds.setLogAbandoned(true);//
        ds.setSuspectTimeout(5);

        return new LazyConnectionDataSourceProxy(ds) {
            @SuppressWarnings("unused")
            public void close() {
                ds.close();
            }
        };

    }

    @Bean(name = PORTAL_JDBC_TEMPLATE, autowire = Autowire.NO)
    public JdbcTemplate portalJdbcTemplate() {
        return new JdbcTemplate(portalDataSource());
    }

    @Bean(name = THEME_REPOSITORY, autowire = Autowire.NO)
    public ThemeRepository themeRepository() {
        return new ThemeRepository(portalJdbcTemplate());
    }

    @Bean(name = PORTLET_REPOSITORY, autowire = Autowire.NO)
    public PortletRepository portletRepository() {
        return new PortletRepository(portletPreferencesRepository(), portletInstanceRepository());
    }

    @Bean(name = SECURE_PORTLET_REPOSITORY, autowire = Autowire.NO)
    public SecurePortletRepository securePortletRepository() {
        return new SecurePortletRepository(portletRepository(), permissionRepository());
    }

    @Bean(name = SECURE_LAYOUT_REPOSITORY, autowire = Autowire.NO)
    public SecureLayoutRepository secureLayoutRepository() {
        return new SecureLayoutRepository(layoutRepository(), permissionRepository());
    }

    @Bean(name = PORTLET_PREFERENCES_REPOSITORY, autowire = Autowire.NO)
    public PortletPreferencesRepository portletPreferencesRepository() {
        return new PortletPreferencesRepository(portalJdbcTemplate());
    }

    @Bean(name = PORTLET_INSTANCE_REPOSITORY, autowire = Autowire.NO)
    public PortletInstanceRepository portletInstanceRepository() {
        return new PortletInstanceRepository(new PortletInstanceDao(portalJdbcTemplate()));
    }

    @Bean(name = LANGUAGE_REPOSITORY, autowire = Autowire.NO)
    public LanguageRepository languageRepository() {
        return new LanguageRepository(portalJdbcTemplate());
    }

    @Bean(name = PAGE_LAYOUT_REPOSITORY, autowire = Autowire.NO)
    public PageLayoutRepository pageLayoutRepository() {
        return new PageLayoutRepository();
    }

    @Deprecated
    @Bean(name = SERVLET_CONTEXT_RESOURCE_REPOSITORY, autowire = Autowire.NO)
    public ee.midaiganes.services.ServletContextResourceRepository servletContextResourceRepository() {
        return new ee.midaiganes.services.ServletContextResourceRepository();
    }

    @Bean(name = RESOURCE_REPOSITORY, autowire = Autowire.NO)
    public ResourceRepository resourceRepository() {
        return new ResourceRepository(new ResourceDao(portalJdbcTemplate()));
    }

    @Bean(name = PERMISSION_REPOSITORY, autowire = Autowire.NO)
    public PermissionRepository permissionRepository() {
        return new PermissionRepository(permissionService(), resourceRepository(), groupRepository());
    }

    @Bean(name = PERMISSION_SERVICE, autowire = Autowire.NO)
    public PermissionService permissionService() {
        return new PermissionService(new PermissionDao(portalJdbcTemplate()), resourceActionRepository());
    }

    @Bean(name = RESOURCE_ACTION_REPOSITORY, autowire = Autowire.NO)
    public ResourceActionRepository resourceActionRepository() {
        return new ResourceActionRepository(new ResourceActionDao(portalJdbcTemplate()));
    }

    @Bean(name = GROUP_REPOSITORY, autowire = Autowire.NO)
    public GroupRepository groupRepository() {
        return new GroupRepository(new GroupDao(portalJdbcTemplate()));
    }

    @Bean(name = LAYOUT_REPOSITORY, autowire = Autowire.NO)
    public LayoutRepository layoutRepository() {
        return new LayoutRepository(new LayoutDao(portalJdbcTemplate()), themeRepository(), pageLayoutRepository());
    }

    @Bean(name = LAYOUT_PORTLET_REPOSITORY, autowire = Autowire.NO)
    public LayoutPortletRepository layoutPortletRepository() {
        return new LayoutPortletRepository(new LayoutPortletDao(portalJdbcTemplate()), portletInstanceRepository());
    }
}
