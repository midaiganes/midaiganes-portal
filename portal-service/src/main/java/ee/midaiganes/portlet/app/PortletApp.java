package ee.midaiganes.portlet.app;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.MidaiganesWindowState;
import ee.midaiganes.model.PortletAndConfiguration;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletNamespace;
import ee.midaiganes.portlet.MidaiganesPortlet;
import ee.midaiganes.portlet.impl.ActionRequestImpl;
import ee.midaiganes.portlet.impl.ActionResponseImpl;
import ee.midaiganes.portlet.impl.PortletPreferencesImpl;
import ee.midaiganes.portlet.impl.RenderParameterMap;
import ee.midaiganes.portlet.impl.RenderParameterUtil;
import ee.midaiganes.portlet.impl.RenderRequestImpl;
import ee.midaiganes.portlet.impl.RenderResponseImpl;
import ee.midaiganes.portlet.impl.ResourceRequestImpl;
import ee.midaiganes.portlet.impl.ResourceResponseImpl;
import ee.midaiganes.services.PortletPreferencesRepository;
import ee.midaiganes.servlet.http.ByteArrayServletOutputStreamAndWriterResponse;
import ee.midaiganes.util.ThemeUtil;

public class PortletApp {
	private static final Logger log = LoggerFactory.getLogger(PortletApp.class);
	private final MidaiganesPortlet portlet;
	private final PortletNamespace namespace;
	private final PortletMode portletMode;
	private final WindowState windowState;
	private final PortletPreferencesRepository portletPreferencesRepository;
	private final PortletAndConfiguration portletConfiguration;
	private final PortletInstance portletInstance;

	public PortletApp(PortletInstance portletInstance, PortletMode portletMode, WindowState windowState,
			PortletPreferencesRepository portletPreferencesRepository, PortletAndConfiguration portletConfiguration) {
		this.portlet = portletConfiguration.getMidaiganesPortlet();
		if (portlet == null) {
			throw new IllegalArgumentException("portlet is null");
		}
		this.portletMode = portletMode;
		this.windowState = windowState;
		this.portletPreferencesRepository = portletPreferencesRepository;
		this.portletConfiguration = portletConfiguration;
		this.portletInstance = portletInstance;
		this.namespace = portletInstance.getPortletNamespace();
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
			portlet.render(renderRequest, getRenderResponse(response, renderRequest));
		} catch (PortletException | IOException | RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void doNormalRender(HttpServletRequest request, HttpServletResponse response) {
		try {
			ByteArrayServletOutputStreamAndWriterResponse resp = new ByteArrayServletOutputStreamAndWriterResponse(response);
			PortletPreferences portletPreferences = getPortletPreferences();
			RenderRequestImpl renderRequest = getRenderRequest(request, resp, portletPreferences);
			portlet.render(renderRequest, getRenderResponse(resp, renderRequest));
			ThemeUtil.includePortletJsp(request, response, portletInstance, new String(resp.getBytes(), resp.getCharacterEncoding()));
		} catch (ServletException | IOException | RuntimeException | PortletException e) {
			log.error(e.getMessage(), e);
		}
	}

	public boolean processAction(HttpServletRequest request, HttpServletResponse response) {
		try {
			PortletPreferences portletPreferences = getPortletPreferences();
			ActionRequest actionRequest = getActionRequest(request, response, portletPreferences);
			portlet.processAction(actionRequest, getActionResponse(response, actionRequest));
			return true;
		} catch (IOException | RuntimeException | PortletException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public void serveResource(HttpServletRequest request, HttpServletResponse response) {
		if (portlet.isResourceServingPortlet()) {
			try {
				ResourceRequestImpl resourceRequest = getResourceRequest(request, response, getPortletPreferences());
				portlet.serveResource(resourceRequest, getResourceResponse(response, resourceRequest));
			} catch (RuntimeException | IOException | PortletException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.error("portlet is not implementing ResourceServingPortlet interface");
		}
	}

	private PortletPreferences getPortletPreferences() {
		return new PortletPreferencesImpl(portletInstance.getId(), portletPreferencesRepository);
	}

	private ResourceRequestImpl getResourceRequest(HttpServletRequest request, HttpServletResponse response, PortletPreferences portletPreferences) {
		return new ResourceRequestImpl(request, response, namespace, portletMode, windowState, portletPreferences, portletConfiguration);
	}

	private ResourceResponse getResourceResponse(HttpServletResponse response, ResourceRequestImpl request) {
		return new ResourceResponseImpl(response, namespace, request);
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
