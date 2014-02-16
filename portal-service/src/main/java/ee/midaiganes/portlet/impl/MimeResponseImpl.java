package ee.midaiganes.portlet.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.PortletLifecycle;
import ee.midaiganes.portal.portletinstance.PortletNamespace;

public class MimeResponseImpl extends PortletResponseImpl implements MimeResponse {
	private final HttpServletResponse response;
	private final PortletRequestImpl request;
	private final PortletNamespace namespace;

	public MimeResponseImpl(HttpServletResponse response, PortletNamespace namespace, PortletRequestImpl request) {
		super(response, namespace);
		this.response = response;
		this.request = request;
		this.namespace = namespace;
	}

	@Override
	public String getContentType() {
		return response.getContentType();
	}

	@Override
	public void setContentType(String type) {

		// TODO Auto-generated method stub

	}

	@Override
	public String getCharacterEncoding() {
		return response.getCharacterEncoding();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return response.getWriter();
	}

	@Override
	public Locale getLocale() {
		return response.getLocale();
	}

	@Override
	public void setBufferSize(int size) {
		response.setBufferSize(size);
	}

	@Override
	public int getBufferSize() {
		return response.getBufferSize();
	}

	@Override
	public void flushBuffer() throws IOException {
		response.flushBuffer();
	}

	@Override
	public void resetBuffer() {
		response.resetBuffer();
	}

	@Override
	public boolean isCommitted() {
		return response.isCommitted();
	}

	@Override
	public void reset() {
		response.reset();
	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		return response.getOutputStream();
	}

	@Override
	public PortletURL createRenderURL() {
		return createPortletURL(PortletLifecycle.RENDER);
	}

	@Override
	public PortletURL createActionURL() {
		return createPortletURL(PortletLifecycle.ACTION);
	}

	@Override
	public ResourceURL createResourceURL() {
		return new ResourceURLImpl(request, namespace, PortletLifecycle.RESOURCE);
	}

	@Override
	public CacheControl getCacheControl() {
		// TODO Auto-generated method stub
		return null;
	}

	private PortletURL createPortletURL(PortletLifecycle lifecycle) {
		return new PortletURLImpl(request, namespace, lifecycle);
	}
}
