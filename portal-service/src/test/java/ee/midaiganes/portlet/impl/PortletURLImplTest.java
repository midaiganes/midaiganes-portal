package ee.midaiganes.portlet.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import ee.midaiganes.portal.portletinstance.PortletNamespace;
import ee.midaiganes.portlet.PortletLifecycle;
import ee.midaiganes.portlet.PortletName;

public class PortletURLImplTest {
    private static final String URL = "http://localhost";

    @Test
    public void toStringTest() {
        HttpServletRequest request = new MockHttpServletRequest();
        Assert.assertEquals(new PortletURLImpl(request, "0000", Collections.<WindowState> emptyList(), Collections.<PortletMode> emptyList(), PortletLifecycle.ACTION,
                new PortletName("test", "fafa")).toString(), URL + "?p_l=1&p_pn=test_w_fafa&p_id=0000");
    }

    @Test
    public void toStringWitPortletRequestAndWithoutPortletName() {
        PortletRequestImpl req = mock(PortletRequestImpl.class);
        when(req.getPortletNamespace()).thenReturn(new PortletNamespace("fafafa_w_daaaa_1234"));
        when(req.getHttpServletRequest()).thenReturn(new MockHttpServletRequest());
        Assert.assertEquals(new PortletURLImpl(req, PortletLifecycle.RENDER).toString(), URL + "?p_l=0&p_id=1234");
    }
}
