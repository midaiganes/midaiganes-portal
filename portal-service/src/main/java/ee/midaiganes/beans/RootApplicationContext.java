package ee.midaiganes.beans;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ee.midaiganes.services.DbInstallService;
import ee.midaiganes.services.LayoutSetRepository;
import ee.midaiganes.services.RequestParser;
import ee.midaiganes.services.UserRepository;

@Configuration(value = "rootApplicationContext")
public class RootApplicationContext {
	public static final String LAYOUT_SET_REPOSITORY = "layoutSetRepository";
	public static final String DB_INSTALL_SERVICE = "dbInstallService";

	public static final String REQUEST_PARSER = "requestParser";

	public static final String USER_REPOSITORY = "userRepository";

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	public void postConstruct() {
		BeanUtil.addBean(UserRepository.class, userRepository);
	}

	@Bean(name = REQUEST_PARSER)
	public RequestParser requestParser() {
		return new RequestParser();
	}

	@Bean(name = LAYOUT_SET_REPOSITORY)
	public LayoutSetRepository layoutSetRepository() {
		return new LayoutSetRepository();
	}

	@Bean(name = USER_REPOSITORY)
	public UserRepository userRepository() {
		return new UserRepository();
	}

	@Bean(name = DB_INSTALL_SERVICE)
	public DbInstallService dbInstallService() {
		return new DbInstallService();
	}
}
