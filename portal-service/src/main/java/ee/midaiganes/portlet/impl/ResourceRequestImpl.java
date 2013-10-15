package ee.midaiganes.portlet.impl;

import java.util.Collections;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.PortletAndConfiguration;
import ee.midaiganes.model.PortletNamespace;

public class ResourceRequestImpl extends ClientDataRequestImpl implements ResourceRequest {

	public ResourceRequestImpl(HttpServletRequest request, HttpServletResponse response, PortletNamespace namespace, PortletMode portletMode,
			WindowState windowState, PortletPreferences portletPreferences, PortletAndConfiguration portletConfiguration) {
		super(request, response, PortletRequest.RESOURCE_PHASE, namespace, portletMode, windowState, portletPreferences, portletConfiguration);
	}

	@Override
	public String getETag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourceID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String[]> getPrivateRenderParameterMap() {
		// TODO Auto-generated method stub
		return Collections.emptyMap();
	}

	@Override
	public String getCacheability() {
		// TODO Auto-generated method stub
		return null;
	}

}
