package ee.midaiganes.portletsservices.webcontent;

import org.thymeleaf.templateresolver.ITemplateResolutionValidity;

public class TemplateResolutionValidity implements ITemplateResolutionValidity {

	@Override
	public boolean isCacheable() {
		return false;
	}

	@Override
	public boolean isCacheStillValid() {
		return false;
	}
}