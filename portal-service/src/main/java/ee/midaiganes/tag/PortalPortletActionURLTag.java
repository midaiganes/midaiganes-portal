package ee.midaiganes.tag;

import ee.midaiganes.model.PortletLifecycle;

public class PortalPortletActionURLTag extends PortalPortletURLTag {

	@Override
	protected PortletLifecycle getPortletLifecycle() {
		return PortletLifecycle.ACTION;
	}
}