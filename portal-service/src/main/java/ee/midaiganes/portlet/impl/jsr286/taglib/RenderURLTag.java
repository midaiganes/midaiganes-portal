package ee.midaiganes.portlet.impl.jsr286.taglib;

import javax.portlet.PortletURL;

public class RenderURLTag extends PortletURLTag {

	@Override
	protected PortletURL getPortletURL() {
		return getMimeResponse().createRenderURL();
	}
}
