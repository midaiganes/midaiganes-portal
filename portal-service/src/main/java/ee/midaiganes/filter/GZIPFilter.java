package ee.midaiganes.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.servlet.http.GZIPResponse;

public class GZIPFilter extends HttpFilter {

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String acceptEncoding = request.getHeader("Accept-Encoding");
		if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
			GZIPResponse gzipresponse = new GZIPResponse(response);
			gzipresponse.setHeader("Content-Encoding", "gzip");
			chain.doFilter(request, gzipresponse);
			gzipresponse.flushBuffer(true);
		} else {
			chain.doFilter(request, response);
		}
	}
}
