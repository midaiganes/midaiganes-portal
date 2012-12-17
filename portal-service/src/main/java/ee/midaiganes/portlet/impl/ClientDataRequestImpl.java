package ee.midaiganes.portlet.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.PortletAndConfiguration;
import ee.midaiganes.model.PortletNamespace;

public class ClientDataRequestImpl extends PortletRequestImpl implements ClientDataRequest {
	private final HttpServletRequest request;

	public ClientDataRequestImpl(HttpServletRequest request, HttpServletResponse response, String lifecyclePhase, PortletNamespace namespace,
			PortletMode portletMode, WindowState windowState, PortletPreferences portletPreferences, PortletAndConfiguration portletConfiguration) {
		super(request, response, lifecyclePhase, namespace, portletMode, windowState, portletPreferences, portletConfiguration);
		this.request = request;
	}

	@Override
	public InputStream getPortletInputStream() throws IOException {
		return request.getInputStream();
	}

	@Override
	public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
		request.setCharacterEncoding(enc);
	}

	@Override
	public BufferedReader getReader() throws UnsupportedEncodingException, IOException {
		return request.getReader();
	}

	@Override
	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	public int getContentLength() {
		return request.getContentLength();
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}

}
