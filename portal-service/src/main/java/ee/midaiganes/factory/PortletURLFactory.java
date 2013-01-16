package ee.midaiganes.factory;

import java.util.Arrays;

import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.model.MidaiganesWindowState;
import ee.midaiganes.model.PortletLifecycle;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.portlet.impl.PortletURLImpl;
import ee.midaiganes.util.StringPool;

@Component(value = RootApplicationContext.PORTLET_URL_FACTORY)
public class PortletURLFactory {
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
