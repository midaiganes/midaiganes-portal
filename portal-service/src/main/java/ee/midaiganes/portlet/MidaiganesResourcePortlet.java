package ee.midaiganes.portlet;

import javax.management.MBeanServer;
import javax.portlet.Portlet;
import javax.portlet.ResourceServingPortlet;

public class MidaiganesResourcePortlet extends MidaiganesPortlet implements ResourceServingPortlet {

    public <A extends Portlet & ResourceServingPortlet> MidaiganesResourcePortlet(A portlet, PortletName portletName, MBeanServer mBeanServer) {
        super(portlet, portletName, mBeanServer);
    }
}
