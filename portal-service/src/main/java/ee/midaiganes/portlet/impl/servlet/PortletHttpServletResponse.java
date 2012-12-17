package ee.midaiganes.portlet.impl.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.PortletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class PortletHttpServletResponse extends HttpServletResponseWrapper {

	private final PortletResponse portletResponse;

	public PortletHttpServletResponse(HttpServletResponse response, PortletResponse portletResponse) {
		super(response);
		this.portletResponse = portletResponse;

	}

	@Override
	public void addCookie(Cookie cookie) {
	}

	@Override
	public void addDateHeader(String key, long value) {
	}

	@Override
	public void addHeader(String key, String value) {
	}

	@Override
	public void addIntHeader(String key, int value) {
	}

	@Override
	public boolean containsHeader(String key) {
		return false;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		return portletResponse.encodeURL(url);
	}

	@Override
	public String encodeURL(String url) {
		return portletResponse.encodeURL(url);
	}

	@Override
	public void sendError(int sc) {
	}

	@Override
	public void sendError(int sc, String msg) {
	}

	@Override
	public void sendRedirect(String url) {
	}

	@Override
	public void setDateHeader(String key, long val) {
	}

	@Override
	public void setHeader(String key, String val) {
	}

	@Override
	public void setIntHeader(String key, int val) {
	}

	@Override
	public void setStatus(int sc) {
	}

	@Override
	public void setStatus(int sc, String msg) {
	}

	@Override
	public void flushBuffer() throws IOException {
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return null;
	}

	@Override
	public boolean isCommitted() {
		return true;
	}

	@Override
	public void reset() {
	}

	@Override
	public void resetBuffer() {
	}

	@Override
	public void setBufferSize(int size) {
	}

	@Override
	public void setCharacterEncoding(String enc) {
	}

	@Override
	public void setContentLength(int len) {
	}

	@Override
	public void setContentType(String t) {
	}

	@Override
	public void setLocale(Locale locale) {
	}
}
