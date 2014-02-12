package ee.midaiganes.servlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletNamespace;
import ee.midaiganes.model.RequestInfo.PortletURL;
import ee.midaiganes.portal.layoutportlet.LayoutPortlet;
import ee.midaiganes.portal.layoutportlet.LayoutPortletRepository;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.secureservices.SecurePortletRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.ThemeUtil;

public class LayoutPortletServlet extends HttpServlet {
    public static final String ID = LayoutPortletServlet.class.getName() + ".ID";
    private static final Logger log = LoggerFactory.getLogger(LayoutPortletServlet.class);

    private LayoutPortletRepository layoutPortletRepository;
    private SecurePortletRepository portletRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        layoutPortletRepository = BeanRepositoryUtil.getBean(LayoutPortletRepository.class);
        portletRepository = BeanRepositoryUtil.getBean(SecurePortletRepository.class);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = ((Long) request.getAttribute(ID)).longValue();
        if (id < 0) {
            throw new ServletException("id(" + id + ") is < 0");
        }
        PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
        List<LayoutPortlet> layoutPortlets = layoutPortletRepository.getLayoutPortlets(pageDisplay.getLayout().getId(), id);
        if (layoutPortlets != null) {
            for (LayoutPortlet layoutPortlet : layoutPortlets) {
                getPortletAppAndRenderPortlet(request, response, pageDisplay, layoutPortlet);
            }
        }
    }

    private void getPortletAppAndRenderPortlet(HttpServletRequest request, HttpServletResponse response, PageDisplay pageDisplay, LayoutPortlet layoutPortlet) {
        PortletURL portletURL = pageDisplay.getPortletURL();
        try {
            PortletApp portletApp = getPortletApp(pageDisplay.getUser().getId(), layoutPortlet, portletURL, layoutPortlet.getPortletInstance());
            if (portletApp != null) {
                portletApp.doRender(request, response);
            } else {
                log.info("portlet app not found for layout portlet: {}", layoutPortlet);
                includePortletJsp(request, response, layoutPortlet.getPortletInstance());
            }
        } catch (PrincipalException e) {
            log.debug(e.getMessage(), e);
        }
    }

    private void includePortletJsp(HttpServletRequest request, HttpServletResponse response, PortletInstance portletInstance) {
        try {
            ThemeUtil.includePortletJsp(request, response, portletInstance, "portlet is undeployed");
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private PortletApp getPortletApp(long userId, LayoutPortlet layoutPortlet, PortletURL portletURL, PortletInstance pi) throws PrincipalException {
        if (portletURL != null && isCurrentPortletInUrl(portletURL, pi.getPortletNamespace(), portletURL.getWindowID())) {
            return portletRepository.getPortletApp(userId, layoutPortlet, portletURL.getPortletMode(), portletURL.getWindowState());
        }
        return portletRepository.getPortletApp(userId, layoutPortlet, PortletMode.VIEW, WindowState.NORMAL);
    }

    private boolean isCurrentPortletInUrl(PortletURL portletURL, PortletNamespace pn, String urlWindowId) {
        return isDefaultWindowId(urlWindowId) ? pn.getPortletName().equals(portletURL.getPortletName()) : pn.getWindowID().equals(urlWindowId);
    }

    private boolean isDefaultWindowId(String urlWindowId) {
        return StringPool.DEFAULT_PORTLET_WINDOWID.equals(urlWindowId);
    }

}
