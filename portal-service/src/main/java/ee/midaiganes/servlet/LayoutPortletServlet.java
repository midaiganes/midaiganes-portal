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
import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.RequestInfo.PortletURL;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.services.LayoutPortletRepository;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;

public class LayoutPortletServlet extends HttpServlet {
	public static final String ID = LayoutPortletServlet.class.getName() + ".ID";
	private static final Logger log = LoggerFactory.getLogger(LayoutPortletServlet.class);

	@Resource(name = RootApplicationContext.LAYOUT_PORTLET_REPOSITORY)
	private LayoutPortletRepository layoutPortletRepository;

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
		long id = (Long) request.getAttribute(ID);
		if (id < 0) {
			throw new ServletException("id(" + id + ") is < 0");
		}
		PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
		LayoutPortlet layoutPortlet = layoutPortletRepository.getLayoutPortlet(pageDisplay.getLayout().getId(), id);
		if (layoutPortlet != null) {
			getPortletAppAndRenderPortlet(request, response, pageDisplay, layoutPortlet);
		}
	}

	private void getPortletAppAndRenderPortlet(HttpServletRequest request, HttpServletResponse response, PageDisplay pageDisplay, LayoutPortlet layoutPortlet) {
		PortletURL portletURL = pageDisplay.getPortletURL();
		PortletApp portletApp = getPortletApp(layoutPortlet, portletURL, layoutPortlet.getPortletInstance());
		portletApp.doRender(request, response);
	}

	private PortletApp getPortletApp(LayoutPortlet layoutPortlet, PortletURL portletURL, PortletInstance pi) {
		if (portletURL != null && isCurrentPortletInUrl(portletURL, pi, portletURL.getWindowID())) {
			return portletRepository.getPortletApp(layoutPortlet, portletURL.getPortletMode(), portletURL.getWindowState());
		}
		return portletRepository.getPortletApp(layoutPortlet, PortletMode.VIEW, WindowState.NORMAL);
	}

	private boolean isCurrentPortletInUrl(PortletURL portletURL, PortletInstance pi, String urlWindowId) {
		return isDefaultWindowId(urlWindowId) ? pi.getPortletName().equals(portletURL.getPortletName()) : pi.getWindowID().equals(urlWindowId);
	}

	private boolean isDefaultWindowId(String urlWindowId) {
		return StringPool.DEFAULT_PORTLET_WINDOWID.equals(urlWindowId);
	}

}
