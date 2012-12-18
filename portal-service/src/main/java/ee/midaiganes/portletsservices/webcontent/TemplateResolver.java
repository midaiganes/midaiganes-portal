package ee.midaiganes.portletsservices.webcontent;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.ITemplateResolutionValidity;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

@Component(value = "templateResolver")
public class TemplateResolver implements ITemplateResolver {
	private final ITemplateResolutionValidity validity;

	@Resource
	private WebContentTemplateResourceResolver resourceResolver;

	public TemplateResolver() {
		validity = new TemplateResolutionValidity();
	}

	@Override
	public String getName() {
		return TemplateResolver.class.getName();
	}

	@Override
	public Integer getOrder() {
		return null;
	}

	@Override
	public TemplateResolution resolveTemplate(TemplateProcessingParameters templateProcessingParameters) {
		String resourceName = templateProcessingParameters.getTemplateName();
		String templateMode = "XML";
		return new TemplateResolution(templateProcessingParameters.getTemplateName(), resourceName, resourceResolver, "UTF-8", templateMode, validity);
	}

	@Override
	public void initialize() {
	}

}