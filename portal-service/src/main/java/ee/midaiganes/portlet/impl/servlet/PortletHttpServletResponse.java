package ee.midaiganes.portlet.impl.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.PortletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortletHttpServletResponse extends HttpServletResponseWrapper {
    private static final Logger log = LoggerFactory.getLogger(PortletHttpServletResponse.class);

    private final PortletResponse portletResponse;

    public PortletHttpServletResponse(HttpServletResponse response, PortletResponse portletResponse) {
        super(response);
        this.portletResponse = portletResponse;

    }

    @Override
    public void addCookie(Cookie cookie) {
        log.debug("Ignore add cookie: '{}'", cookie);
    }

    @Override
    public void addDateHeader(String key, long value) {
        log.debug("Ignore add date header: '{}' -> '{}'", key, Long.valueOf(value));
    }

    @Override
    public void addHeader(String key, String value) {
        log.debug("Ignore add header: '{}' -> '{}'", key, value);
    }

    @Override
    public void addIntHeader(String key, int value) {
        log.debug("Ignore add int header: '{}' -> '{}'", key, Integer.valueOf(value));
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
        log.debug("Ignore send error: '{}'", Integer.valueOf(sc));
    }

    @Override
    public void sendError(int sc, String msg) {
        log.debug("Ignore send error: '{}' with message '{}'", Integer.valueOf(sc), msg);
    }

    @Override
    public void sendRedirect(String url) {
        log.debug("Ignore send redirect to '{}'", url);
    }

    @Override
    public void setDateHeader(String key, long val) {
        log.debug("Ignore set date header: '{}' -> '{}'", key, Long.valueOf(val));
    }

    @Override
    public void setHeader(String key, String val) {
        log.debug("Ignore set header: '{}' -> '{}'", key, val);
    }

    @Override
    public void setIntHeader(String key, int val) {
        log.debug("Ignore set int header: '{}' -> '{}'", key, Integer.valueOf(val));
    }

    @Override
    public void setStatus(int sc) {
        log.debug("Ignore set status: '{}'", Integer.valueOf(sc));
    }

    @Override
    public void setStatus(int sc, String msg) {
        log.debug("Ignore set status: '{}' with message '{}'", Integer.valueOf(sc), msg);
    }

    @Override
    public void flushBuffer() throws IOException {
        log.debug("Ignore flush buffer");
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
        log.debug("Ignore reset");
    }

    @Override
    public void resetBuffer() {
        log.debug("Ignore reset buffer");
    }

    @Override
    public void setBufferSize(int size) {
        log.debug("Ignore set buffer size to '{}'", Integer.valueOf(size));
    }

    @Override
    public void setCharacterEncoding(String enc) {
        log.debug("Ignore set character encoding to '{}'", enc);
    }

    @Override
    public void setContentLength(int len) {
        log.debug("Ignore set content length to '{}'", Integer.valueOf(len));
    }

    @Override
    public void setContentType(String t) {
        log.debug("Ignore set content type to '{}'", t);
    }

    @Override
    public void setLocale(Locale locale) {
        log.debug("Ignore set locale to '{}'", locale);
    }
}
