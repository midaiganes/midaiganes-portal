package ee.midaiganes.portlet.impl.jsr286.taglib;

import javax.portlet.PortletURL;

public class ActionURLTag extends PortletURLTag {

	@Override
	protected PortletURL getPortletURL() {
		return getMimeResponse().createActionURL();
	}

}
