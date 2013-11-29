package ee.midaiganes.beans;

import javax.annotation.PostConstruct;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import ee.midaiganes.factory.PortletURLFactory;
import ee.midaiganes.secureservices.SecureLayoutRepository;
import ee.midaiganes.secureservices.SecurePortletRepository;
import ee.midaiganes.services.DbInstallService;
import ee.midaiganes.services.GroupRepository;
import ee.midaiganes.services.LanguageRepository;
import ee.midaiganes.services.LayoutPortletRepository;
import ee.midaiganes.services.LayoutRepository;
import ee.midaiganes.services.LayoutSetRepository;
import ee.midaiganes.services.PageLayoutRepository;
import ee.midaiganes.services.PermissionRepository;
import ee.midaiganes.services.PermissionService;
import ee.midaiganes.services.PortletInstanceRepository;
import ee.midaiganes.services.PortletPreferencesRepository;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.services.RequestParser;
import ee.midaiganes.services.ResourceActionRepository;
import ee.midaiganes.services.ResourceRepository;
import ee.midaiganes.services.ThemeRepository;
import ee.midaiganes.services.ThemeVariablesService;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.services.dao.GroupDao;
import ee.midaiganes.services.dao.LayoutDao;
import ee.midaiganes.services.dao.LayoutPortletDao;
import ee.midaiganes.services.dao.LayoutSetDao;
import ee.midaiganes.services.dao.PermissionDao;
import ee.midaiganes.services.dao.PortletInstanceDao;
import ee.midaiganes.services.dao.ResourceActionDao;
import ee.midaiganes.services.dao.ResourceDao;
import ee.midaiganes.services.dao.UserDao;

@Configuration(value = "portalConfig")
@PropertySource(value = "classpath:portal.properties", name = "portalProperties")
@EnableLoadTimeWeaving(aspectjWeaving = AspectJWeaving.ENABLED)
// @EnableAspectJAutoProxy(proxyTargetClass = true)
public class PortalConfig {

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

    private LayoutRepository layoutRepository;

    @PostConstruct
    public void postConstruct() {
        BeanRepositoryUtil.register(PermissionRepository.class, permissionRepository);
        BeanRepositoryUtil.register(LanguageRepository.class, languageRepository);
        BeanRepositoryUtil.register(PortletInstanceRepository.class, portletInstanceRepository);
        BeanRepositoryUtil.register(SecureLayoutRepository.class, secureLayoutRepository);
        BeanRepositoryUtil.register(ThemeVariablesService.class, new ThemeVariablesService(PortletURLFactory.getInstance()));
        BeanRepositoryUtil.register(UserRepository.class, userRepository);
        BeanRepositoryUtil.register(RequestParser.class, new RequestParser());
        BeanRepositoryUtil.register(LayoutPortletRepository.class, layoutPortletRepository);
        BeanRepositoryUtil.register(PortletRepository.class, portletRepository);
        BeanRepositoryUtil.register(PageLayoutRepository.class, pageLayoutRepository);
        BeanRepositoryUtil.register(LayoutRepository.class, layoutRepository);
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

    @Bean(name = PORTAL_DATASOURCE, destroyMethod = "close", autowire = Autowire.NO)
    public BasicDataSource portalDataSource() {
        BasicDataSource portalDataSource = new PortalDataSource();
        portalDataSource.setDriverClassName(driver);
        portalDataSource.setUrl(url);
        portalDataSource.setUsername(username);
        portalDataSource.setPassword(password);
        portalDataSource.setMaxActive(1);
        portalDataSource.setMaxIdle(1);
        portalDataSource.setMinIdle(1);
        portalDataSource.setInitialSize(1);
        portalDataSource.setMaxWait(1000);
        portalDataSource.setTestOnBorrow(true);
        portalDataSource.setTestOnReturn(true);
        portalDataSource.setTimeBetweenEvictionRunsMillis(5000);
        portalDataSource.setTestWhileIdle(true);
        portalDataSource.setValidationQuery("SELECT 1");
        portalDataSource.setValidationQueryTimeout(1);
        return portalDataSource;
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
