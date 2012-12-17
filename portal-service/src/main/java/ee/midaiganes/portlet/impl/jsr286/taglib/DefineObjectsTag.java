package ee.midaiganes.portlet.impl.jsr286.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.servlet.jsp.tagext.Tag;

public class DefineObjectsTag extends BasePortletTag {

	@Override
	public int doEndTag() {
		PortletRequest portletRequest = getPortletRequest();
		PortletResponse portletResponse = getPortletResponse();
		if (PortletRequest.RENDER_PHASE.equals(portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
			setAttribute("renderRequest", portletRequest);
			setAttribute("renderResponse", portletResponse);
		} else if (PortletRequest.RESOURCE_PHASE.equals(portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
			setAttribute("resourceRequest", portletRequest);
			setAttribute("resourceResponse", portletResponse);
		} else if (PortletRequest.ACTION_PHASE.equals(portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
			setAttribute("actionRequest", portletRequest);
			setAttribute("actionResponse", portletResponse);
		} else if (PortletRequest.EVENT_PHASE.equals(portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
			setAttribute("eventRequest", portletRequest);
			setAttribute("eventResponse", portletResponse);
		}

		setAttribute("portletConfig", getPortletConfig());
		PortletSession portletSession = portletRequest.getPortletSession(false);
		setAttribute("portletSession", portletSession);
		if (portletSession != null) {
			setAttribute("portletSessionScope", portletSession.getAttributeMap());
		} else {
			setAttribute("portletSessionScope", new HashMap<String, Object>());
		}
		PortletPreferences portletPreferencesValues = portletRequest.getPreferences();
		setAttribute("portletPreferences", portletPreferencesValues);
		Map<String, String[]> preferencesMap = portletPreferencesValues.getMap();
		if (preferencesMap == null) {
			preferencesMap = new HashMap<String, String[]>();
		}
		setAttribute("portletPreferencesValues", preferencesMap);
		return Tag.EVAL_PAGE;
	}

	private void setAttribute(String name, Object value) {
		getPageContext().setAttribute(name, value);
		getPageContext().getRequest().setAttribute(name, value);
	}
}
