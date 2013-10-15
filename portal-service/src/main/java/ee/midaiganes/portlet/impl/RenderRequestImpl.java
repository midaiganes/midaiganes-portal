package ee.midaiganes.portlet.impl;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.PortletAndConfiguration;
import ee.midaiganes.model.PortletNamespace;

public class RenderRequestImpl extends PortletRequestImpl implements RenderRequest {
	private String ETag;

	public RenderRequestImpl(HttpServletRequest request, HttpServletResponse response, PortletNamespace namespace, PortletMode portletMode,
			WindowState windowState, PortletPreferences portletPreferences, PortletAndConfiguration portletConfiguration) {
		super(request, response, PortletRequest.RENDER_PHASE, namespace, portletMode, windowState, portletPreferences, portletConfiguration);
	}

	@Override
	public String getETag() {
		// TODO Auto-generated method stub
		return ETag;
	}

	@Override
	public String getProperty(String name) {
		if (RenderRequest.ETAG.equals(name)) {
			return ETag;
		}
		return super.getProperty(name);
	}
}
