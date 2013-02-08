package ee.midaiganes.portlet;

import javax.portlet.Portlet;
import javax.portlet.ResourceServingPortlet;

import ee.midaiganes.model.PortletName;

public class MidaiganesResourcePortlet extends MidaiganesPortlet implements ResourceServingPortlet {

	public <A extends Portlet & ResourceServingPortlet> MidaiganesResourcePortlet(A portlet, PortletName portletName) {
		super(portlet, portletName);
	}
}
