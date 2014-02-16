package ee.midaiganes.portlet.impl.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.MimeResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.servlet.io.ServletOutputStreamImpl;

public class PortletRenderHttpServletResponse extends PortletHttpServletResponse {
	private final MimeResponse mimeResponse;

	public PortletRenderHttpServletResponse(HttpServletResponse response, MimeResponse mimeResponse) {
		super(response, mimeResponse);
		this.mimeResponse = mimeResponse;
	}

	@Override
	public void flushBuffer() throws IOException {
		mimeResponse.flushBuffer();
	}

	@Override
	public int getBufferSize() {
		return mimeResponse.getBufferSize();
	}

	@Override
	public String getCharacterEncoding() {
		return mimeResponse.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return mimeResponse.getContentType();
	}

	@Override
	public Locale getLocale() {
		return mimeResponse.getLocale();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStreamImpl(mimeResponse.getPortletOutputStream());
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return mimeResponse.getWriter();
	}

	@Override
	public boolean isCommitted() {
		return mimeResponse.isCommitted();
	}

	@Override
	public void reset() {
		mimeResponse.reset();
	}

	@Override
	public void resetBuffer() {
		mimeResponse.resetBuffer();
	}

	@Override
	public void setBufferSize(int size) {
		mimeResponse.setBufferSize(size);
	}
}
