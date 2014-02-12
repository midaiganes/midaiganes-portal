package ee.midaiganes.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import ee.midaiganes.portal.portletinstance.PortletInstance;
import ee.midaiganes.util.RequestUtil;

public class ThemePortletJspRequest extends HttpServletRequestWrapper {
	private static final String PORTLET_CONTENT = "portletContent";
	private static final String PORTLET_NAMESPACE = "portletNamespace";
	private static final String PORTLET_INSTANCE = "portletInstance";
	private static final String PAGE_DISPLAY = "pageDisplay";
	private final String portletContent;
	private final PortletInstance portletInstance;

	public ThemePortletJspRequest(HttpServletRequest request, String portletContent, PortletInstance portletInstance) {
		super(request);
		this.portletContent = portletContent;
		this.portletInstance = portletInstance;
	}

	@Override
	public Object getAttribute(String name) {
		if (PORTLET_CONTENT.equals(name)) {
			return portletContent;
		} else if (PORTLET_NAMESPACE.equals(name)) {
			return portletInstance.getPortletNamespace();
		} else if (PORTLET_INSTANCE.equals(name)) {
			return portletInstance;
		} else if (PAGE_DISPLAY.equals(name)) {
			return RequestUtil.getPageDisplay((HttpServletRequest) super.getRequest());
		}
		return super.getAttribute(name);
	}
}
