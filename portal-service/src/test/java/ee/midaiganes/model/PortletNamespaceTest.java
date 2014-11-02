package ee.midaiganes.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import ee.midaiganes.portal.portletinstance.PortletNamespace;
import ee.midaiganes.portlet.PortletName;

public class PortletNamespaceTest {

    @Test
    public void getWindowID() {
        Assert.assertEquals(new PortletNamespace("faa_w_test_0000").getWindowID(), "0000");
    }

    @Test
    public void getPortletName() {
        PortletNamespace pn = new PortletNamespace("faa_w_test_0000");
        Assert.assertEquals(pn.getPortletName().getContext(), "faa");
        Assert.assertEquals(pn.getPortletName().getName(), "test");
    }

    @Test
    public void getNamespace() {
        PortletNamespace pn = new PortletNamespace("faa_w_test_0000");
        Assert.assertEquals(pn.getNamespace(), "faa_w_test_0000");
    }

    @Test
    public void getNamespaceFromPortletNameAndWindowID() {
        PortletNamespace pn = new PortletNamespace(new PortletName("test", "name"), "0000");
        Assert.assertEquals(pn.getNamespace(), "test_w_name_0000");
    }

    @Test
    public void isDefaultWindowId() {
        PortletNamespace pn = new PortletNamespace("www_w_test_0000");
        Assert.assertTrue(pn.isDefaultWindowID());
    }
}
