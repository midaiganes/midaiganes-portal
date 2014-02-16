package ee.midaiganes.portlet.impl;

import javax.portlet.ActionRequest;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.PortletAndConfiguration;
import ee.midaiganes.portal.portletinstance.PortletNamespace;

public class ActionRequestImpl extends ClientDataRequestImpl implements ActionRequest {

	public ActionRequestImpl(HttpServletRequest request, HttpServletResponse response, PortletNamespace namespace, PortletMode portletMode,
			WindowState windowState, PortletPreferences portletPreferences, PortletAndConfiguration portletConfiguration) {
		super(request, response, PortletRequest.ACTION_PHASE, namespace, portletMode, windowState, portletPreferences, portletConfiguration);
	}
}
