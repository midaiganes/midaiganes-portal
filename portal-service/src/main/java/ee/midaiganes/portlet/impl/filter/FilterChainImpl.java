package ee.midaiganes.portlet.impl.filter;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.FilterChain;

public abstract class FilterChainImpl implements FilterChain {

	@Override
	public void doFilter(ActionRequest request, ActionResponse response) throws IOException, PortletException {
		throw new IllegalStateException();
	}

	@Override
	public void doFilter(EventRequest request, EventResponse response) throws IOException, PortletException {
		throw new IllegalStateException();
	}

	@Override
	public void doFilter(RenderRequest request, RenderResponse response) throws IOException, PortletException {
		throw new IllegalStateException();
	}

	@Override
	public void doFilter(ResourceRequest request, ResourceResponse response) throws IOException, PortletException {
		throw new IllegalStateException();
	}

}
