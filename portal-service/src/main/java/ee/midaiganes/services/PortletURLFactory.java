package ee.midaiganes.services;

import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.portlet.PortletAndConfiguration;
import ee.midaiganes.portlet.PortletLifecycle;
import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.portlet.impl.PortletURLImpl;
import ee.midaiganes.util.StringPool;

public class PortletURLFactory {
    private final PortletRepository portletRepository;

    public PortletURLFactory(PortletRepository portletRepository) {
        this.portletRepository = portletRepository;
    }

    public PortletURL makeRenderURL(HttpServletRequest request, PortletName portletName) {
        PortletAndConfiguration portletAndConfiguration = portletRepository.getPortlet(portletName);
        return new PortletURLImpl(request, StringPool.DEFAULT_PORTLET_WINDOWID, portletAndConfiguration.getSupportedWindowStates(),
                portletAndConfiguration.getSupportedPortletModes(), PortletLifecycle.RENDER, portletName);
    }

    public PortletURL makeActionURL(HttpServletRequest request, PortletName portletName) {
        PortletAndConfiguration portletAndConfiguration = portletRepository.getPortlet(portletName);
        return new PortletURLImpl(request, StringPool.DEFAULT_PORTLET_WINDOWID, portletAndConfiguration.getSupportedWindowStates(),
                portletAndConfiguration.getSupportedPortletModes(), PortletLifecycle.ACTION, portletName);
    }

    public PortletURL makeRenderURL(HttpServletRequest request, PortletName portletName, WindowState windowState) throws WindowStateException {
        PortletURL url = makeRenderURL(request, portletName);
        url.setWindowState(windowState);
        return url;
    }
}
