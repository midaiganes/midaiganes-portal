package ee.midaiganes.factory;

import java.util.Arrays;

import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.portlet.MidaiganesWindowState;
import ee.midaiganes.portlet.PortletLifecycle;
import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.portlet.impl.PortletURLImpl;
import ee.midaiganes.util.StringPool;

public class PortletURLFactory {
	private static final PortletURLFactory instance = new PortletURLFactory();

	private PortletURLFactory() {
	}

	public static PortletURLFactory getInstance() {
		return instance;
	}

	public PortletURL makeRenderURL(HttpServletRequest request, PortletName portletName) {
		return new PortletURLImpl(request, StringPool.DEFAULT_PORTLET_WINDOWID,
				Arrays.<WindowState> asList(WindowState.NORMAL, MidaiganesWindowState.EXCLUSIVE), Arrays.<PortletMode> asList(PortletMode.VIEW,
						PortletMode.EDIT), PortletLifecycle.RENDER, portletName);
	}

	public PortletURL makeActionURL(HttpServletRequest request, PortletName portletName) {
		return new PortletURLImpl(request, StringPool.DEFAULT_PORTLET_WINDOWID,
				Arrays.<WindowState> asList(WindowState.NORMAL, MidaiganesWindowState.EXCLUSIVE), Arrays.<PortletMode> asList(PortletMode.VIEW,
						PortletMode.EDIT), PortletLifecycle.ACTION, portletName);
	}

	public PortletURL makeRenderURL(HttpServletRequest request, PortletName portletName, WindowState windowState) throws WindowStateException {
		PortletURL url = makeRenderURL(request, portletName);
		url.setWindowState(windowState);
		return url;
	}
}
