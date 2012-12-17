package ee.midaiganes.beans;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import ee.midaiganes.services.LanguageRepository;
import ee.midaiganes.services.PortletInstanceRepository;
import ee.midaiganes.services.PortletPreferencesRepository;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.services.ThemeRepository;

@Configuration
@PropertySource(value = "classpath:/META-INF/portal.properties")
@EnableLoadTimeWeaving(aspectjWeaving = AspectJWeaving.ENABLED)
// @EnableAspectJAutoProxy(proxyTargetClass = true)
public class PortalConfig {

	public static final String PORTLET_REPOSITORY = "portletRepository";
	public static final String PORTLET_PREFERENCES_REPOSITORY = "portletPreferencesRepository";
	public static final String PORTLET_INSTANCE_REPOSITORY = "portletInstanceRepository";
	public static final String LANGUAGE_REPOSITORY = "languageRepository";
	@Deprecated
	public static final String SERVLET_CONTEXT_RESOURCE_REPOSITORY = "servletContextResourceRepository";
	public static final String THEME_REPOSITORY = "themeRepository";
	public static final String PORTAL_JDBC_TEMPLATE = "portalJdbcTemplate";
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

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = TXMANAGER)
	public DataSourceTransactionManager dataSourceTransactionManager() {
		DataSourceTransactionManager txManager = new DataSourceTransactionManager(portalDataSource());
		txManager.setDefaultTimeout(txManagerDefaultTimeout);
		return txManager;
	}

	@Bean(name = "portalDataSource", destroyMethod = "close")
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

	@Bean(name = PORTAL_JDBC_TEMPLATE)
	public JdbcTemplate portalJdbcTemplate() {
		return new JdbcTemplate(portalDataSource());
	}

	@Bean(name = THEME_REPOSITORY)
	public ThemeRepository themeRepository() {
		return new ThemeRepository();
	}

	@Bean(name = PORTLET_REPOSITORY)
	public PortletRepository portletRepository() {
		return new PortletRepository();
	}

	@Bean(name = PORTLET_PREFERENCES_REPOSITORY)
	public PortletPreferencesRepository portletPreferencesRepository() {
		return new PortletPreferencesRepository();
	}

	@Bean(name = PORTLET_INSTANCE_REPOSITORY)
	public PortletInstanceRepository portletInstanceRepository() {
		return new PortletInstanceRepository();
	}

	@Bean(name = LANGUAGE_REPOSITORY)
	public LanguageRepository LanguageRepository() {
		return new LanguageRepository();
	}

	@Deprecated
	@Bean(name = SERVLET_CONTEXT_RESOURCE_REPOSITORY)
	public ee.midaiganes.services.ServletContextResourceRepository servletContextResourceRepository() {
		return new ee.midaiganes.services.ServletContextResourceRepository();
	}
}
