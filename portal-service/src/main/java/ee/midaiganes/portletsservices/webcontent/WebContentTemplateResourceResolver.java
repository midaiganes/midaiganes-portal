package ee.midaiganes.portletsservices.webcontent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

@Component
public class WebContentTemplateResourceResolver implements IResourceResolver {

	@Resource
	private WebContentRepository webContentRepository;

	@Override
	public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
		Template template = webContentRepository.getTemplate(Long.parseLong(resourceName));
		if (template != null) {
			try {
				return new ByteArrayInputStream(template.getTemplateContent().getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return WebContentTemplateResourceResolver.class.getName();
	}
}