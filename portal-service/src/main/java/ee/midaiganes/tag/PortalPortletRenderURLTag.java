package ee.midaiganes.tag;

import ee.midaiganes.model.PortletLifecycle;

public class PortalPortletRenderURLTag extends PortalPortletURLTag {

	@Override
	protected PortletLifecycle getPortletLifecycle() {
		return PortletLifecycle.RENDER;
	}
}
