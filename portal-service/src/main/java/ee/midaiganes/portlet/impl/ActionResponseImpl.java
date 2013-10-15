package ee.midaiganes.portlet.impl;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.PortletNamespace;

public class ActionResponseImpl extends StateAwareResponseImpl implements ActionResponse {
	private final HttpServletResponse response;

	public ActionResponseImpl(HttpServletResponse response, PortletRequest portletRequest, PortletNamespace namespace) {
		super(response, portletRequest, namespace);
		this.response = response;
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		response.sendRedirect(location);
	}

	@Override
	public void sendRedirect(String location, String renderUrlParamName) throws IOException {
		throw new RuntimeException("not implemented");
	}
}
