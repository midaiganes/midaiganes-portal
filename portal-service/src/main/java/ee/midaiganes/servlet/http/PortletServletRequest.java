package ee.midaiganes.servlet.http;

import javax.portlet.Portlet;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import ee.midaiganes.servlet.PortletServlet;

public class PortletServletRequest extends HttpServletRequestWrapper {
	public PortletServletRequest(HttpServletRequest request, Portlet portlet, PortletRequest portletRequest, PortletResponse portletResponse, String method) {
		super(request);
		setAttribute(PortletServlet.PORTLET_APP, portlet);
		setAttribute(PortletServlet.PORTLET_REQUEST, portletRequest);
		setAttribute(PortletServlet.PORTLET_RESPONSE, portletResponse);
		setAttribute(PortletServlet.PORTLET_METHOD, method);
	}
}
