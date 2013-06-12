package ee.midaiganes.beans;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ee.midaiganes.factory.PortletURLFactory;
import ee.midaiganes.services.DbInstallService;
import ee.midaiganes.services.LayoutPortletRepository;
import ee.midaiganes.services.LayoutRepository;
import ee.midaiganes.services.LayoutSetRepository;
import ee.midaiganes.services.RequestParser;
import ee.midaiganes.services.ThemeVariablesService;
import ee.midaiganes.services.UserRepository;

@Configuration(value = "rootApplicationContext")
public class RootApplicationContext {
	public static final String LAYOUT_SET_REPOSITORY = "layoutSetRepository";
	public static final String DB_INSTALL_SERVICE = "dbInstallService";
	public static final String LAYOUT_PORTLET_REPOSITORY = "layoutPortletRepository";
	public static final String REQUEST_PARSER = "requestParser";
	public static final String THEME_VARIABLES_SERVICE = "themeVariablesService";
	public static final String LAYOUT_REPOSITORY = "layoutRepository";
	public static final String USER_REPOSITORY = "userRepository";
	public static final String PORTLET_URL_FACTORY = "portletURLFactory";

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	public void postConstruct() {
		UserRepository.setInstance(userRepository);
	}

	@Bean(name = THEME_VARIABLES_SERVICE)
	public ThemeVariablesService themeVariablesService() {
		return new ThemeVariablesService();
	}

	@Bean(name = PORTLET_URL_FACTORY)
	public PortletURLFactory portletURLFactory() {
		return new PortletURLFactory();
	}

	@Bean(name = REQUEST_PARSER)
	public RequestParser requestParser() {
		return new RequestParser();
	}

	@Bean(name = LAYOUT_SET_REPOSITORY)
	public LayoutSetRepository layoutSetRepository() {
		return new LayoutSetRepository();
	}

	@Bean(name = LAYOUT_REPOSITORY)
	public LayoutRepository layoutRepository() {
		return new LayoutRepository();
	}

	@Bean(name = USER_REPOSITORY)
	public UserRepository userRepository() {
		return new UserRepository();
	}

	@Bean(name = DB_INSTALL_SERVICE)
	public DbInstallService dbInstallService() {
		return new DbInstallService();
	}

	@Bean(name = LAYOUT_PORTLET_REPOSITORY)
	public LayoutPortletRepository layoutPortletRepository() {
		return new LayoutPortletRepository();
	}
}
