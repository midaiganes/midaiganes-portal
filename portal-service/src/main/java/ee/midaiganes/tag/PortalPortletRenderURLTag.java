package ee.midaiganes.tag;

import ee.midaiganes.portlet.PortletLifecycle;

public class PortalPortletRenderURLTag extends PortalPortletURLTag {

	@Override
	protected PortletLifecycle getPortletLifecycle() {
		return PortletLifecycle.RENDER;
	}
}
