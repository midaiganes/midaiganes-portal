package ee.midaiganes.portlets.webcontent;

import java.io.InputStream;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolutionValidity;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

public class ThymeleafUtil {
	static {
		createTemplateEngine();
	}

	private static void createTemplateEngine() {
		TemplateEngine templateEngine = new TemplateEngine();
		ITemplateResolver templateResolver = new ITemplateResolver() {

			@Override
			public String getName() {
				return null;
			}

			@Override
			public Integer getOrder() {
				return null;
			}

			@Override
			public TemplateResolution resolveTemplate(TemplateProcessingParameters templateProcessingParameters) {
				String resourceName = null;
				String templateMode = null;
				IResourceResolver resourceResolver = new IResourceResolver() {
					@Override
					public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
						return null;
					}

					@Override
					public String getName() {
						return null;
					}
				};
				ITemplateResolutionValidity validity = new ITemplateResolutionValidity() {

					@Override
					public boolean isCacheable() {
						return false;
					}

					@Override
					public boolean isCacheStillValid() {
						return false;
					}
				};
				TemplateResolution templateResolution = new TemplateResolution(templateProcessingParameters.getTemplateName(), resourceName, resourceResolver,
						"UTF-8", templateMode, validity);
				return templateResolution;
			}

			@Override
			public void initialize() {
			}

		};
		templateEngine.setTemplateResolver(templateResolver);
	}
}
