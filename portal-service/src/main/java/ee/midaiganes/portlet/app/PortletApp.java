package ee.midaiganes.portlet.app;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.MidaiganesWindowState;
import ee.midaiganes.model.PortletAndConfiguration;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.model.PortletNamespace;
import ee.midaiganes.portlet.MidaiganesPortlet;
import ee.midaiganes.portlet.impl.ActionRequestImpl;
import ee.midaiganes.portlet.impl.ActionResponseImpl;
import ee.midaiganes.portlet.impl.PortletPreferencesImpl;
import ee.midaiganes.portlet.impl.RenderParameterMap;
import ee.midaiganes.portlet.impl.RenderParameterUtil;
import ee.midaiganes.portlet.impl.RenderRequestImpl;
import ee.midaiganes.portlet.impl.RenderResponseImpl;
import ee.midaiganes.services.PortletPreferencesRepository;
import ee.midaiganes.servlet.PortletServlet;
import ee.midaiganes.servlet.http.ByteArrayServletOutputStreamAndWriterResponse;
import ee.midaiganes.servlet.http.PortletServletRequest;
import ee.midaiganes.util.ContextUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.ThemeUtil;

public class PortletApp {
	private static final Logger log = LoggerFactory.getLogger(PortletApp.class);
	private final MidaiganesPortlet portlet;
	private final PortletNamespace namespace;
	private final PortletMode portletMode;
	private final WindowState windowState;
	private final PortletPreferencesRepository portletPreferencesRepository;
	private final PortletAndConfiguration portletConfiguration;

	public PortletApp(String windowID, PortletName portletName, PortletMode portletMode, WindowState windowState,
			PortletPreferencesRepository portletPreferencesRepository, PortletAndConfiguration portletConfiguration) {
		if (windowID == null) {
			throw new IllegalArgumentException("windowID is null");
		}
		this.portlet = portletConfiguration.getMidaiganesPortlet();
		if (portlet == null) {
			throw new IllegalArgumentException("portlet is null");
		}
		this.portletMode = portletMode;
		this.windowState = windowState;
		this.portletPreferencesRepository = portletPreferencesRepository;
		this.portletConfiguration = portletConfiguration;
		this.namespace = new PortletNamespace(portletName, windowID);
	}

	private void includePortletServlet(HttpServletRequest request, HttpServletResponse response, PortletRequest req, PortletResponse resp, String method)
			throws ServletException, IOException {
		getPortletServletDispatcher(request).include(new PortletServletRequest(request, portlet, req, resp, method), response);
	}

	private RequestDispatcher getPortletServletDispatcher(HttpServletRequest request) {
		return getPortletContext(request).getNamedDispatcher(PortletServlet.class.getName());
	}

	private ServletContext getPortletContext(HttpServletRequest request) {
		return ContextUtil.getServletContext(request, StringPool.SLASH + namespace.getPortletName().getContext());
	}

	public void doRender(HttpServletRequest request, HttpServletResponse response) {
		if (MidaiganesWindowState.EXCLUSIVE.equals(windowState)) {
			doExclusiveRender(request, response);
		} else {
			doNormalRender(request, response);
		}
	}

	private void doExclusiveRender(HttpServletRequest request, HttpServletResponse response) {
		try {
			PortletPreferences portletPreferences = getPortletPreferences();
			RenderRequestImpl renderRequest = getRenderRequest(request, response, portletPreferences);
			includePortletServlet(request, response, renderRequest, getRenderResponse(response, renderRequest), PortletServlet.PORTLET_METHOD_RENDER);
		} catch (ServletException | IOException | RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void doNormalRender(HttpServletRequest request, HttpServletResponse response) {
		try {
			ByteArrayServletOutputStreamAndWriterResponse resp = new ByteArrayServletOutputStreamAndWriterResponse(response);
			PortletPreferences portletPreferences = getPortletPreferences();
			RenderRequestImpl renderRequest = getRenderRequest(request, resp, portletPreferences);
			includePortletServlet(request, resp, renderRequest, getRenderResponse(resp, renderRequest), PortletServlet.PORTLET_METHOD_RENDER);
			ThemeUtil.includePortletJsp(request, response, namespace, new String(resp.getBytes(), resp.getCharacterEncoding()));
		} catch (ServletException | IOException | RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	public boolean processAction(HttpServletRequest request, HttpServletResponse response) {
		try {
			PortletPreferences portletPreferences = getPortletPreferences();
			ActionRequest actionRequest = getActionRequest(request, response, portletPreferences);
			includePortletServlet(request, response, actionRequest, getActionResponse(response, actionRequest), PortletServlet.PORTLET_METHOD_ACTION);
			return true;
		} catch (ServletException | IOException | RuntimeException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public void serveResource(HttpServletRequest request, HttpServletResponse response) {
		if (portlet.isResourceServingPortlet()) {
			try {
				log.warn("serveResource not implemented");
				portlet.serveResource(null, null);
			} catch (RuntimeException | IOException | PortletException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.error("portlet is not implementing ResourceServingPortlet interface");
		}
	}

	private PortletPreferences getPortletPreferences() {
		return new PortletPreferencesImpl(namespace, portletPreferencesRepository);
	}

	private ActionRequest getActionRequest(HttpServletRequest request, HttpServletResponse response, PortletPreferences portletPreferences) {
		return new ActionRequestImpl(request, response, namespace, portletMode, windowState, portletPreferences, portletConfiguration);
	}

	private ActionResponse getActionResponse(HttpServletResponse response, ActionRequest request) {
		return new ActionResponseImpl(response, request, namespace);
	}

	private RenderRequestImpl getRenderRequest(HttpServletRequest request, HttpServletResponse response, PortletPreferences portletPreferences) {
		RenderParameterMap map = RenderParameterUtil.getRenderparameterMap(request, namespace);
		if (map != null) {
			request = new RenderRequestParametersHttpServletRequest(request, map);
		}
		return new RenderRequestImpl(request, response, namespace, portletMode, windowState, portletPreferences, portletConfiguration);

	}

	private RenderResponse getRenderResponse(HttpServletResponse response, RenderRequestImpl request) {
		return new RenderResponseImpl(response, namespace, request);
	}
}
