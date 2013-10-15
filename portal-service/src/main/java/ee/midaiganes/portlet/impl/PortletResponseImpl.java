package ee.midaiganes.portlet.impl;

import javax.portlet.PortletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import ee.midaiganes.model.PortletNamespace;

public class PortletResponseImpl implements PortletResponse {
	private final HttpServletResponse response;
	private final PortletNamespace namespace;

	public PortletResponseImpl(HttpServletResponse response, PortletNamespace namespace) {
		this.response = response;
		this.namespace = namespace;
	}

	@Override
	public void addProperty(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperty(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String encodeURL(String path) {
		return response.encodeURL(path);
	}

	@Override
	public String getNamespace() {
		return namespace.getNamespace();
	}

	@Override
	public void addProperty(Cookie cookie) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addProperty(String key, Element element) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpServletResponse getHttpServletResponse() {
		return this.response;
	}
}
