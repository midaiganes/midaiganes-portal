package ee.midaiganes.portlet.impl;

import java.util.Locale;

import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.PortletNamespace;

public class ResourceResponseImpl extends MimeResponseImpl implements ResourceResponse {
	private final HttpServletResponse response;

	public ResourceResponseImpl(HttpServletResponse response, PortletNamespace namespace, PortletRequestImpl request) {
		super(response, namespace, request);
		this.response = response;
	}

	@Override
	public void setLocale(Locale loc) {
		response.setLocale(loc);
	}

	@Override
	public void setCharacterEncoding(String charset) {
		response.setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int len) {
		response.setContentLength(len);
	}
}
