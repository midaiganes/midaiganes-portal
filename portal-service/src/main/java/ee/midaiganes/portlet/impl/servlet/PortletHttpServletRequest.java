package ee.midaiganes.portlet.impl.servlet;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import ee.midaiganes.util.PortletConstant;

public class PortletHttpServletRequest extends HttpServletRequestWrapper {
	private final PortletConfig portletConfig;
	private final PortletRequest portletRequest;
	private final PortletResponse portletResponse;

	public PortletHttpServletRequest(HttpServletRequest request, PortletConfig portletConfig, PortletRequest portletRequest, PortletResponse response) {
		super(request);
		this.portletConfig = portletConfig;
		this.portletRequest = portletRequest;
		this.portletResponse = response;
	}

	@Override
	public Object getAttribute(String name) {
		if (PortletConstant.JAVAX_PORTLET_CONFIG.equals(name)) {
			return portletConfig;
		} else if (PortletConstant.JAVAX_PORTLET_REQUEST.equals(name)) {
			return portletRequest;
		} else if (PortletConstant.JAVAX_PORTLET_RESPONSE.equals(name)) {
			return portletResponse;
		}
		return super.getAttribute(name);
	}

}
