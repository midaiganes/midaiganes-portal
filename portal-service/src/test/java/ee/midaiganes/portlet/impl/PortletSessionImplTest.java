package ee.midaiganes.portlet.impl;

import javax.portlet.PortletSession;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.portlet.MockPortletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PortletSessionImplTest {
    private PortletSessionImpl ps;
    private MockHttpSession hs;
    private MockPortletRequest req;

    @BeforeMethod
    public void beforeMethod() {
        hs = new MockHttpSession(new MockServletContext(), "666");
        req = new MockPortletRequest();
        req.setWindowID("1234");
        ps = new PortletSessionImpl(hs, req, null);
    }

    @Test
    public void getId() {
        Assert.assertEquals(ps.getId(), "666");
    }

    @Test
    public void getNullAttributeWithPortletScope() {
        Assert.assertEquals(ps.getAttribute("test"), null);
    }

    @Test
    public void getNotNullAttributeWithPortletScope() {
        hs.setAttribute("javax.portlet.p.1234?test", Boolean.TRUE);
        Assert.assertEquals(ps.getAttribute("test"), Boolean.TRUE);
    }

    @Test
    public void getNotNullAttributeWithApplicationScope() {
        hs.setAttribute("test", Boolean.TRUE);
        Assert.assertEquals(ps.getAttribute("test", PortletSession.APPLICATION_SCOPE), Boolean.TRUE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getAttributeWithIllegalScope() {
        Assert.assertEquals(ps.getAttribute("test", -9), Boolean.TRUE);
    }

    @Test
    public void getAttributeNames() {
        Assert.assertFalse(ps.getAttributeNames().hasMoreElements());
        Assert.assertFalse(ps.getAttributeNames(PortletSession.APPLICATION_SCOPE).hasMoreElements());
        Assert.assertFalse(ps.getAttributeNames(PortletSession.PORTLET_SCOPE).hasMoreElements());
        hs.setAttribute("test", Boolean.TRUE);
        Assert.assertFalse(ps.getAttributeNames().hasMoreElements());
        Assert.assertFalse(ps.getAttributeNames(PortletSession.PORTLET_SCOPE).hasMoreElements());
        Assert.assertTrue(ps.getAttributeNames(PortletSession.APPLICATION_SCOPE).hasMoreElements());
        Assert.assertEquals(ps.getAttributeNames(PortletSession.APPLICATION_SCOPE).nextElement(), "test");
        hs.setAttribute("javax.portlet.p.1234?testing..", Boolean.TRUE);
        Assert.assertTrue(ps.getAttributeNames().hasMoreElements());
        Assert.assertTrue(ps.getAttributeNames(PortletSession.PORTLET_SCOPE).hasMoreElements());
        Assert.assertEquals(ps.getAttributeNames(PortletSession.PORTLET_SCOPE).nextElement(), "testing..");
        Assert.assertTrue(ps.getAttributeNames(PortletSession.APPLICATION_SCOPE).hasMoreElements());
        Assert.assertEquals(ps.getAttributeNames(PortletSession.APPLICATION_SCOPE).nextElement(), "test");
    }

    @Test
    public void setAttribute() {
        ps.setAttribute("test", Boolean.TRUE);
        Assert.assertEquals(ps.getAttribute("test"), Boolean.TRUE);
        Assert.assertNull(ps.getAttribute("test", PortletSession.APPLICATION_SCOPE));
    }

    @Test
    public void setAttributeApplicationScope() {
        ps.setAttribute("test", Boolean.TRUE, PortletSession.APPLICATION_SCOPE);
        Assert.assertEquals(ps.getAttribute("test", PortletSession.APPLICATION_SCOPE), Boolean.TRUE);
        Assert.assertNull(ps.getAttribute("test", PortletSession.PORTLET_SCOPE));
    }
}
