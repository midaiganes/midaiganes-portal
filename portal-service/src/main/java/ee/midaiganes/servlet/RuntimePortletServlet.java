package ee.midaiganes.servlet;

import java.io.IOException;

import javax.annotation.Resource;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.model.RequestInfo.PortletURL;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;

public class RuntimePortletServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(RuntimePortletServlet.class);
	public static final String PORTLET_NAME = RuntimePortletServlet.class.getName() + ".PORTLET_NAME";

	@Resource(name = PortalConfig.PORTLET_REPOSITORY)
	private PortletRepository portletRepository;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			autowire();
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PortletName portletName = (PortletName) request.getAttribute(PORTLET_NAME);
		PortletURL portletURL = RequestUtil.getPageDisplay(request).getPortletURL();
		PortletApp portletApp = getPortletApp(portletName, portletURL);
		if (portletApp != null) {
			portletApp.doRender(request, response);
		} else {
			log.debug("portletApp = null");
		}
	}

	private PortletApp getPortletApp(PortletName portletName, PortletURL portletURL) {
		log.debug("portletName = '{}'; portletURL = '{}'", portletName, portletURL);
		if (portletURL != null && StringPool.DEFAULT_PORTLET_WINDOWID.equals(portletURL.getWindowID()) && portletName.equals(portletURL.getPortletName())) {
			return getPortletApp(portletURL);
		} else {
			return getPortletApp(portletName);
		}
	}

	private PortletApp getPortletApp(PortletName portletName) {
		log.debug("get portletApp by name '{}'", portletName);
		return portletRepository.getPortletApp(portletName, StringPool.DEFAULT_PORTLET_WINDOWID, PortletMode.VIEW, WindowState.NORMAL);
	}

	private PortletApp getPortletApp(PortletURL portletURL) {
		log.debug("get portletApp by portletURL '{}'", portletURL);
		return portletRepository.getPortletApp(portletURL.getPortletName(), StringPool.DEFAULT_PORTLET_WINDOWID, portletURL.getPortletMode(),
				portletURL.getWindowState());
	}
}
