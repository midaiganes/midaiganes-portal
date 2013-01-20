package ee.midaiganes.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import ee.midaiganes.model.PortletNamespace;

public class ThemePortletJspRequest extends HttpServletRequestWrapper {
	private static final String PORTLET_CONTENT = "portletContent";
	private static final String PORTLET_NAMESPACE = "portletNamespace";
	private final String portletContent;
	private final PortletNamespace namespace;

	public ThemePortletJspRequest(HttpServletRequest request, String portletContent, PortletNamespace namespace) {
		super(request);
		this.portletContent = portletContent;
		this.namespace = namespace;
	}

	@Override
	public Object getAttribute(String name) {
		if (PORTLET_CONTENT.equals(name)) {
			return portletContent;
		}
		if (PORTLET_NAMESPACE.equals(name)) {
			return namespace;
		}
		return super.getAttribute(name);
	}
}
