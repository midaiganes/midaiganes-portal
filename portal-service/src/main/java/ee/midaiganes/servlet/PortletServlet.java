package ee.midaiganes.servlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.portlet.MidaiganesPortlet;

public class PortletServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(PortletServlet.class);
	public static final String PORTLET_APP = PortletServlet.class.getName() + ".PORTLET_APP";
	public static final String PORTLET_REQUEST = PortletServlet.class.getName() + ".PORTLET_REQUEST";
	public static final String PORTLET_RESPONSE = PortletServlet.class.getName() + ".PORTLET_RESPONSE";
	public static final String PORTLET_METHOD = PortletServlet.class.getName() + ".PORTLET_METHOD";
	public static final String PORTLET_METHOD_RENDER = "0";
	public static final String PORTLET_METHOD_ACTION = "1";
	public static final String PORTLET_METHOD_RESOURCE = "2";

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		MidaiganesPortlet portlet = (MidaiganesPortlet) request.getAttribute(PORTLET_APP);
		PortletRequest portletRequest = (PortletRequest) request.getAttribute(PORTLET_REQUEST);
		PortletResponse portletResponse = (PortletResponse) request.getAttribute(PORTLET_RESPONSE);
		String method = (String) request.getAttribute(PORTLET_METHOD);
		removeAttributes(request);
		if (portlet != null) {
			try {
				executePortlet(portlet, portletRequest, portletResponse, method);
			} catch (IOException | PortletException | RuntimeException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.warn("portlet = null");
		}
	}

	private void executePortlet(MidaiganesPortlet portlet, PortletRequest portletRequest, PortletResponse portletResponse, String method)
			throws PortletException, IOException {
		if (PORTLET_METHOD_RENDER.equals(method)) {
			portlet.render((RenderRequest) portletRequest, (RenderResponse) portletResponse);
		} else if (PORTLET_METHOD_ACTION.equals(method)) {
			portlet.processAction((ActionRequest) portletRequest, (ActionResponse) portletResponse);
		} else if (PORTLET_METHOD_RESOURCE.equals(method)) {
			portlet.serveResource((ResourceRequest) portletRequest, (ResourceResponse) portletResponse);
		} else {
			log.warn("unknown method: '{}'", method);
		}
	}

	private void removeAttributes(HttpServletRequest request) {
		request.removeAttribute(PORTLET_APP);
		request.removeAttribute(PORTLET_METHOD);
		request.removeAttribute(PORTLET_REQUEST);
		request.removeAttribute(PORTLET_RESPONSE);
		request.removeAttribute(PORTLET_METHOD);
	}
}
