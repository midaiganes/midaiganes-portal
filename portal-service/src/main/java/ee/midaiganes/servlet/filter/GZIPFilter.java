package ee.midaiganes.servlet.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.HttpHeaders;

import ee.midaiganes.servlet.http.GZIPResponse;

public class GZIPFilter extends HttpFilter {
    private static final String GZIP = "gzip";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String acceptEncoding = request.getHeader(HttpHeaders.ACCEPT_ENCODING);
        if (acceptEncoding != null && acceptEncoding.contains(GZIP)) {
            // TODO acceptEncoding.contains = bug
            GZIPResponse gzipresponse = new GZIPResponse(response);
            gzipresponse.setHeader(HttpHeaders.CONTENT_TYPE, GZIP);
            chain.doFilter(request, gzipresponse);
            gzipresponse.flushBuffer(true);
        } else {
            chain.doFilter(request, response);
        }
    }
}
