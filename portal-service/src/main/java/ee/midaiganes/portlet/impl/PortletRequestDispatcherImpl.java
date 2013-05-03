package ee.midaiganes.portlet.impl;

import java.io.IOException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.portlet.impl.servlet.PortletHttpServletRequest;
import ee.midaiganes.portlet.impl.servlet.PortletRenderHttpServletResponse;
import ee.midaiganes.util.PortletConstant;

public class PortletRequestDispatcherImpl implements PortletRequestDispatcher {
	private static final Logger log = LoggerFactory.getLogger(PortletRequestDispatcherImpl.class);
	private final RequestDispatcher requestDispatcher;
	private final PortletConfig portletConfig;

	public PortletRequestDispatcherImpl(RequestDispatcher requestDispatcher, PortletConfig portletConfig) {
		this.requestDispatcher = requestDispatcher;
		this.portletConfig = portletConfig;
	}

	@Override
	public void include(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		// TODO
		HttpServletRequest servletRequest = (HttpServletRequest) request.getAttribute(PortletConstant.JAVAX_PORTLET_SERVLETREQUEST);
		HttpServletResponse servletResponse = (HttpServletResponse) request.getAttribute(PortletConstant.JAVAX_PORTLET_SERVLETRESPONSE);
		try {
			requestDispatcher.include(new PortletHttpServletRequest(servletRequest, portletConfig, request, response), new PortletRenderHttpServletResponse(
					servletResponse, response));
		} catch (ServletException e) {
			throw new PortletException(e);
		}
	}

	@Override
	public void include(PortletRequest request, PortletResponse response) throws PortletException, IOException {
		// TODO
		Object lifecyclePhase = request.getAttribute(PortletRequest.LIFECYCLE_PHASE);
		if (PortletRequest.RENDER_PHASE.equals(lifecyclePhase) && request instanceof RenderRequest && response instanceof RenderResponse) {
			include((RenderRequest) request, (RenderResponse) response);
		} else {
			throw new IllegalStateException("not implemented: lifecyclePhase='" + lifecyclePhase + "'; request='" + request + "';response='" + response + "'");
		}
	}

	@Override
	public void forward(PortletRequest request, PortletResponse response) throws PortletException, IOException {
		// TODO
		log.error("forward");
		throw new IllegalStateException("not implemented");
	}

}
