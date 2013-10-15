package ee.midaiganes.servlet;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.model.MidaiganesWindowState;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletLifecycle;
import ee.midaiganes.model.RequestInfo;
import ee.midaiganes.model.Theme;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.secureservices.SecurePortletRepository;
import ee.midaiganes.services.DbInstallService;
import ee.midaiganes.services.LayoutPortletRepository;
import ee.midaiganes.services.PortletInstanceRepository;
import ee.midaiganes.services.exceptions.DbInstallFailedException;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.servlet.http.ThemeServletRequest;
import ee.midaiganes.util.ContextUtil;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;

public class PortalServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(PortalServlet.class);
    private static final String WEBINF_THEME = "/WEB-INF/theme";

    @Resource(name = PortalConfig.DB_INSTALL_SERVICE)
    private DbInstallService dbInstallService;

    @Resource(name = PortalConfig.LAYOUT_PORTLET_REPOSITORY)
    private LayoutPortletRepository layoutPortletRepository;

    @Resource(name = PortalConfig.SECURE_PORTLET_REPOSITORY)
    private SecurePortletRepository portletRepository;

    @Resource(name = PortalConfig.PORTLET_INSTANCE_REPOSITORY)
    private PortletInstanceRepository portletInstanceRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            autowire();
            dbInstallService.install(config.getServletContext());
        } catch (RuntimeException | DbInstallFailedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
        RequestInfo.PortletURL portletURL = pageDisplay.getRequestInfo().getPortletURL();
        log.debug("portletURL = {}", portletURL);
        if (portletURL != null) {
            try {
                if (processPortletRequest(request, response, pageDisplay, portletURL)) {
                    return;
                }
            } catch (PrincipalException e) {
                log.debug(e.getMessage(), e);
            }
        }
        getAndIncludeTheme(request, response, pageDisplay);
    }

    private boolean processPortletRequest(HttpServletRequest request, HttpServletResponse response, PageDisplay pageDisplay, RequestInfo.PortletURL portletURL)
            throws PrincipalException {
        if (PortletLifecycle.ACTION.equals(portletURL.getLifecycle())) {
            PortletApp portletApp = getPortletApp(pageDisplay, portletURL);
            if (portletApp != null) {
                boolean success = portletApp.processAction(request, response);
                if (response.isCommitted()) {
                    return true;
                }
                if (success) {
                    if (MidaiganesWindowState.EXCLUSIVE.equals(portletURL.getWindowState())) {
                        portletApp.doRender(request, response);
                        return true;
                    }
                } else {

                    // TODO
                    log.error("action failed");
                }
            }
        } else if (PortletLifecycle.RESOURCE.equals(portletURL.getLifecycle())) {
            PortletApp portletApp = getPortletApp(pageDisplay, portletURL);
            if (portletApp != null) {
                log.debug("Found PortletApp '{}'", portletApp);
                portletApp.serveResource(request, response);
                return true;
            } else {
                log.debug("Didn't find portletApp for portletUrl '{}'", portletURL);
            }
        } else if (MidaiganesWindowState.EXCLUSIVE.equals(portletURL.getWindowState())) {
            PortletApp portletApp = getPortletApp(pageDisplay, portletURL);
            log.debug("portletApp = {}", portletApp);
            if (portletApp != null) {
                portletApp.doRender(request, response);
                return true;
            }
        }
        return false;
    }

    private PortletApp getPortletApp(PageDisplay pageDisplay, RequestInfo.PortletURL portletURL) throws PrincipalException {
        // TODO
        if (StringPool.DEFAULT_PORTLET_WINDOWID.equals(portletURL.getWindowID())) {
            if (portletURL.getPortletName() != null) {
                PortletInstance instance = portletInstanceRepository.getDefaultPortletInstance(portletURL.getPortletName());
                return portletRepository.getPortletApp(pageDisplay.getUser().getId(), instance, portletURL.getPortletMode(), portletURL.getWindowState());
            } else {
                log.debug("portletURL.getPortletName is empty");
            }
        } else {
            LayoutPortlet layoutPortlet = getLayoutPortlet(pageDisplay, portletURL);
            if (layoutPortlet != null) {
                return portletRepository.getPortletApp(pageDisplay.getUser().getId(), layoutPortlet, portletURL.getPortletMode(), portletURL.getWindowState());
            } else {
                log.debug("layoutPortlet = null");
            }
        }
        return null;
    }

    private LayoutPortlet getLayoutPortlet(PageDisplay pageDisplay, RequestInfo.PortletURL portletURL) {
        return layoutPortletRepository.getLayoutPortlet(pageDisplay.getLayout().getId(), portletURL.getWindowID());
    }

    private void getAndIncludeTheme(HttpServletRequest request, HttpServletResponse response, PageDisplay pageDisplay) throws ServletException, IOException {
        Theme theme = pageDisplay.getTheme();
        if (theme != null) {
            includeTheme(request, response, theme);
        } else {
            log.error("theme is null");
        }
    }

    private void includeTheme(HttpServletRequest request, HttpServletResponse response, Theme theme) throws ServletException, IOException {
        ServletContext servletContext = ContextUtil.getServletContext(request, theme.getThemeName().getContextWithSlash());
        if (servletContext == null) {
            log.error("servletContext = null; theme = " + theme);
        } else {
            includeTheme(request, response, theme, servletContext.getRequestDispatcher(WEBINF_THEME));
        }
    }

    private void includeTheme(HttpServletRequest request, HttpServletResponse response, Theme theme, RequestDispatcher requestDispatcher) throws ServletException, IOException {
        if (requestDispatcher == null) {
            log.error(ThemeServlet.class.getName() + " not registered");
        } else {
            requestDispatcher.include(new ThemeServletRequest(theme, request), response);
        }
    }
}
