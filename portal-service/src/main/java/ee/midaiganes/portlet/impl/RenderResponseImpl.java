package ee.midaiganes.portlet.impl;

import java.util.Collection;

import javax.portlet.PortletMode;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.portal.portletinstance.PortletNamespace;

public class RenderResponseImpl extends MimeResponseImpl implements RenderResponse {

	public RenderResponseImpl(HttpServletResponse response, PortletNamespace namespace, PortletRequestImpl request) {
		super(response, namespace, request);
	}

	@Override
	public void setTitle(String title) {
		// TODO
	}

	@Override
	public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
		// TODO
	}

}
