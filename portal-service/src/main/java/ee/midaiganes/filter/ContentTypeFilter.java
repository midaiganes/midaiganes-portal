package ee.midaiganes.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.util.StringPool;

public class ContentTypeFilter extends HttpFilter {

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		response.setCharacterEncoding(StringPool.UTF_8);
		chain.doFilter(request, response);
	}

}
