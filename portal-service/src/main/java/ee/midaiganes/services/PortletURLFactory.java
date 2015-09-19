package ee.midaiganes.services;

import javax.inject.Inject;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.portlet.PortletAndConfiguration;
import ee.midaiganes.portlet.PortletLifecycle;
import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.portlet.impl.PortletURLImpl;
import ee.midaiganes.util.StringPool;

public class PortletURLFactory {
    private final PortletRepository portletRepository;

    @Inject
    public PortletURLFactory(PortletRepository portletRepository) {
        this.portletRepository = portletRepository;
    }

    public PortletURL makeRenderURL(HttpServletRequest request, HttpServletResponse response, PortletName portletName) {
        PortletAndConfiguration portletAndConfiguration = portletRepository.getPortlet(portletName);
        return new PortletURLImpl(request, response, StringPool.DEFAULT_PORTLET_WINDOWID, portletAndConfiguration.getSupportedWindowStates(),
                portletAndConfiguration.getSupportedPortletModes(), PortletLifecycle.RENDER, portletName);
    }

    public PortletURL makeActionURL(HttpServletRequest request, HttpServletResponse response, PortletName portletName) {
        PortletAndConfiguration portletAndConfiguration = portletRepository.getPortlet(portletName);
        return new PortletURLImpl(request, response, StringPool.DEFAULT_PORTLET_WINDOWID, portletAndConfiguration.getSupportedWindowStates(),
                portletAndConfiguration.getSupportedPortletModes(), PortletLifecycle.ACTION, portletName);
    }

    public PortletURL makeRenderURL(HttpServletRequest request, HttpServletResponse response, PortletName portletName, WindowState windowState) throws WindowStateException {
        PortletURL url = makeRenderURL(request, response, portletName);
        url.setWindowState(windowState);
        return url;
    }
}
