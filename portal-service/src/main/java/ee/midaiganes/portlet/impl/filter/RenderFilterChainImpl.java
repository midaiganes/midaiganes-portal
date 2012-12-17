package ee.midaiganes.portlet.impl.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.RenderFilter;

public class RenderFilterChainImpl extends FilterChainImpl {
	private final Enumeration<RenderFilter> filters;

	public RenderFilterChainImpl(Enumeration<RenderFilter> filters) {
		this.filters = filters;
	}

	@Override
	public void doFilter(RenderRequest request, RenderResponse response) throws IOException, PortletException {
		if (filters.hasMoreElements()) {
			RenderFilter filter = filters.nextElement();
			filter.doFilter(request, response, this);
		}
	}
}
