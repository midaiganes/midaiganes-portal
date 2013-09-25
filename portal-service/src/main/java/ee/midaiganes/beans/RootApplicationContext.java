package ee.midaiganes.beans;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ee.midaiganes.services.RequestParser;

@Configuration(value = "rootApplicationContext")
public class RootApplicationContext {

	public static final String REQUEST_PARSER = "requestParser";

	@Bean(name = REQUEST_PARSER, autowire = Autowire.NO)
	public RequestParser requestParser() {
		return new RequestParser();
	}

}
