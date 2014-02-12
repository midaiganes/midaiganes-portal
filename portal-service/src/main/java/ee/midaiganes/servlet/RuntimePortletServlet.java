package ee.midaiganes.servlet;

import java.io.IOException;

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
import ee.midaiganes.model.PortletName;
import ee.midaiganes.model.RequestInfo.PortletURL;
import ee.midaiganes.portal.portletinstance.PortletInstance;
import ee.midaiganes.portal.portletinstance.PortletInstanceRepository;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.secureservices.SecurePortletRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;

public class RuntimePortletServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(RuntimePortletServlet.class);
    public static final String PORTLET_NAME = RuntimePortletServlet.class.getName() + ".PORTLET_NAME";

    private SecurePortletRepository portletRepository;
    private PortletInstanceRepository portletInstanceRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        portletRepository = BeanRepositoryUtil.getBean(SecurePortletRepository.class);
        portletInstanceRepository = BeanRepositoryUtil.getBean(PortletInstanceRepository.class);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PortletName portletName = (PortletName) request.getAttribute(PORTLET_NAME);
        PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
        PortletURL portletURL = pageDisplay.getPortletURL();
        try {
            PortletApp portletApp = getPortletApp(pageDisplay, portletName, portletURL);
            if (portletApp != null) {
                portletApp.doRender(request, response);
            } else {
                log.debug("portletApp = null");
            }
        } catch (PrincipalException e) {
            log.debug(e.getMessage(), e);
        }
    }

    private PortletApp getPortletApp(PageDisplay pageDisplay, PortletName portletName, PortletURL portletURL) throws PrincipalException {
        log.debug("portletName = '{}'; portletURL = '{}'", portletName, portletURL);
        if (portletURL != null && StringPool.DEFAULT_PORTLET_WINDOWID.equals(portletURL.getWindowID()) && portletName.equals(portletURL.getPortletName())) {
            return getPortletApp(pageDisplay, portletURL);
        }
        return getPortletApp(pageDisplay, portletName);
    }

    private PortletApp getPortletApp(PageDisplay pageDisplay, PortletName portletName) throws PrincipalException {
        log.debug("get portletApp by name '{}'", portletName);
        PortletInstance portletInstance = portletInstanceRepository.getDefaultPortletInstance(portletName);
        return portletRepository.getPortletApp(pageDisplay.getUser().getId(), portletInstance, PortletMode.VIEW, WindowState.NORMAL);
    }

    private PortletApp getPortletApp(PageDisplay pageDisplay, PortletURL portletURL) throws PrincipalException {
        log.debug("get portletApp by portletURL '{}'", portletURL);
        PortletInstance portletInstance = portletInstanceRepository.getDefaultPortletInstance(portletURL.getPortletName());
        return portletRepository.getPortletApp(pageDisplay.getUser().getId(), portletInstance, portletURL.getPortletMode(), portletURL.getWindowState());
    }
}
