package ee.midaiganes.services;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.model.PortletLifecycle;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.model.RequestInfo;
import ee.midaiganes.util.PortletConstant;
import ee.midaiganes.util.PortletModeUtil;
import ee.midaiganes.util.WindowStateUtil;

public class RequestParser {
	public RequestInfo parserRequest(HttpServletRequest request) {
		RequestInfo info = new RequestInfo();
		String p_id = request.getParameter(PortletConstant.PORTLET_URL_PORLTET_WINDOWID);
		if (p_id != null) {
			RequestInfo.PortletURL portletURL = new RequestInfo.PortletURL();
			portletURL.setWindowID(p_id);
			portletURL.setWindowState(getWindowState(request));
			portletURL.setPortletMode(getPortletMode(request));
			portletURL.setLifecycle(getPortletLifecycle(request));
			portletURL.setPortletName(getPortletName(request));
			info.setPortletURL(portletURL);
		}
		return info;
	}

	private WindowState getWindowState(HttpServletRequest request) {
		return WindowStateUtil.getWindowState(request.getParameter(PortletConstant.PORTLET_URL_PORTLET_WINDOWSTATE), WindowState.NORMAL);
	}

	private PortletMode getPortletMode(HttpServletRequest request) {
		return PortletModeUtil.getPortletMode(request.getParameter(PortletConstant.PORTLET_URL_PORTLET_PORTLETMODE), PortletMode.VIEW);
	}

	private PortletLifecycle getPortletLifecycle(HttpServletRequest request) {
		return PortletLifecycle.getLifecycle(request.getParameter(PortletConstant.PORTLET_URL_PORTLET_LIFECYCLE), PortletLifecycle.RENDER);
	}

	private PortletName getPortletName(HttpServletRequest request) {
		return PortletName.getPortletNameOrNull(request.getParameter(PortletConstant.PORTLET_URL_PORTLET_NAME));
	}
}
