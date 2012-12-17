package ee.midaiganes.portlet.impl.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.filter.ActionFilter;

public class ActionFilterChainImpl extends FilterChainImpl {
	private final Enumeration<ActionFilter> filters;

	public ActionFilterChainImpl(Enumeration<ActionFilter> filters) {
		this.filters = filters;
	}

	@Override
	public void doFilter(ActionRequest request, ActionResponse response) throws IOException, PortletException {
		if (filters.hasMoreElements()) {
			ActionFilter filter = filters.nextElement();
			filter.doFilter(request, response, this);
		}
	}
}
