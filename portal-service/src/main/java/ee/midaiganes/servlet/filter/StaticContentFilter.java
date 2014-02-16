package ee.midaiganes.servlet.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticContentFilter extends HttpFilter {
	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String requestURI = request.getRequestURI();
		setContentType(response, requestURI);
		chain.doFilter(request, response);
	}

	private void setContentType(HttpServletResponse response, String requestURI) {
		if (requestURI.endsWith(".js")) {
			response.setContentType("text/javascript; charset=UTF-8");
		} else if (requestURI.endsWith(".css")) {
			response.setContentType("text/css; charset=UTF-8");
		}
	}
}