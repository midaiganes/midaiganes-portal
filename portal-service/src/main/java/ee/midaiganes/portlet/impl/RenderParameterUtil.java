package ee.midaiganes.portlet.impl;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.portal.portletinstance.PortletNamespace;

public class RenderParameterUtil {

	public static RenderParameterMap getRenderparameterMap(HttpServletRequest request, PortletNamespace namespace) {
		return (RenderParameterMap) request.getAttribute("javax.portlet.p." + namespace.getNamespace() + "?renderParameters");
	}

	public static void setRenderparameterMap(PortletRequest request, PortletNamespace namespace, RenderParameterMap map) {
		request.setAttribute("javax.portlet.p." + namespace.getNamespace() + "?renderParameters", map);
	}
}
