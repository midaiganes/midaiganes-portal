package ee.midaiganes.portlet.impl.jsr286.taglib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.servlet.jsp.tagext.Tag;

public class DefineObjectsTag extends BasePortletTag {
	private static final String RENDER_REQUEST = "renderRequest";
	private static final String RENDER_RESPONSE = "renderResponse";
	private static final String RESOURCE_REQUEST = "resourceRequest";
	private static final String RESOURCE_RESPONSE = "resourceResponse";
	private static final String ACTION_REQUEST = "actionRequest";
	private static final String ACTION_RESPONSE = "actionResponse";
	private static final String EVENT_REQUEST = "eventRequest";
	private static final String EVENT_RESPONSE = "eventResponse";
	private static final String PORTLET_CONFIG = "portletConfig";
	private static final String PORTLET_SESSION = "portletSession";
	private static final String PORTLET_SESSION_SCOPE = "portletSessionScope";
	private static final String PORTLET_PREFERENCES = "portletPreferences";
	private static final String PORTLET_PREFERENCES_VALUES = "portletPreferencesValues";

	@Override
	public int doEndTag() {
		PortletRequest portletRequest = getPortletRequest();
		PortletResponse portletResponse = getPortletResponse();
		if (PortletRequest.RENDER_PHASE.equals(portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
			setAttribute(RENDER_REQUEST, portletRequest);
			setAttribute(RENDER_RESPONSE, portletResponse);
		} else if (PortletRequest.RESOURCE_PHASE.equals(portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
			setAttribute(RESOURCE_REQUEST, portletRequest);
			setAttribute(RESOURCE_RESPONSE, portletResponse);
		} else if (PortletRequest.ACTION_PHASE.equals(portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
			setAttribute(ACTION_REQUEST, portletRequest);
			setAttribute(ACTION_RESPONSE, portletResponse);
		} else if (PortletRequest.EVENT_PHASE.equals(portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
			setAttribute(EVENT_REQUEST, portletRequest);
			setAttribute(EVENT_RESPONSE, portletResponse);
		}

		setAttribute(PORTLET_CONFIG, getPortletConfig());
		PortletSession portletSession = portletRequest.getPortletSession(false);
		setAttribute(PORTLET_SESSION, portletSession);
		if (portletSession != null) {
			setAttribute(PORTLET_SESSION_SCOPE, portletSession.getAttributeMap());
		} else {
			setAttribute(PORTLET_SESSION_SCOPE, Collections.emptyMap());
		}
		PortletPreferences portletPreferencesValues = portletRequest.getPreferences();
		setAttribute(PORTLET_PREFERENCES, portletPreferencesValues);
		Map<String, String[]> preferencesMap = portletPreferencesValues.getMap();
		if (preferencesMap == null) {
			preferencesMap = new HashMap<String, String[]>();
		}
		setAttribute(PORTLET_PREFERENCES_VALUES, preferencesMap);
		return Tag.EVAL_PAGE;
	}

	private void setAttribute(String name, Object value) {
		getPageContext().setAttribute(name, value);
		getHttpServletRequest().setAttribute(name, value);
	}
}
