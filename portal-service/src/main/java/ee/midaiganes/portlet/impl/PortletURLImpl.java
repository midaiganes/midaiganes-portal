package ee.midaiganes.portlet.impl;

import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PortletLifecycle;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.model.PortletNamespace;
import ee.midaiganes.util.PortletConstant;
import ee.midaiganes.util.PortletModeUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.WindowStateUtil;

public class PortletURLImpl extends BaseURLImpl implements PortletURL {
	private static final Logger log = LoggerFactory.getLogger(PortletURLImpl.class);
	private final List<WindowState> allowedWindowStates;
	private final List<PortletMode> allowedPortletModes;

	public PortletURLImpl(PortletRequestImpl request, PortletLifecycle lifecycle) {
		this(request, new PortletNamespace(request.getWindowID()), lifecycle);
	}

	public PortletURLImpl(PortletRequestImpl request, PortletNamespace namespace, PortletLifecycle lifecycle) {
		this(request.getHttpServletRequest(), namespace.getWindowID(), request.getAllowedWindowStates(), request.getAllowedPortletModes(), lifecycle, namespace
				.getPortletName());
	}

	public PortletURLImpl(HttpServletRequest request, String windowID, List<WindowState> allowedWindowStates, List<PortletMode> allowedPortletModes,
			PortletLifecycle lifecycle, PortletName portletName) {
		super(request);
		setParameter(PortletConstant.PORTLET_URL_PORLTET_WINDOWID, windowID);
		setParameter(PortletConstant.PORTLET_URL_PORTLET_LIFECYCLE, lifecycle.toString());
		if (StringPool.DEFAULT_PORTLET_WINDOWID.equals(windowID)) {
			setParameter(PortletConstant.PORTLET_URL_PORTLET_NAME, portletName.getFullName());
		}
		this.allowedWindowStates = allowedWindowStates;
		this.allowedPortletModes = allowedPortletModes;
	}

	@Override
	public void setWindowState(WindowState windowState) throws WindowStateException {
		if (!allowedWindowStates.contains(windowState)

		/* TODO !portletRequest.isWindowStateAllowed(windowState) */) {
			throw new WindowStateException("Window state not allowed: " + windowState, windowState);
		}
		setParameter(PortletConstant.PORTLET_URL_PORTLET_WINDOWSTATE, windowState.toString());
	}

	@Override
	public void setPortletMode(PortletMode portletMode) throws PortletModeException {
		if (!allowedPortletModes.contains(portletMode)/*
													 * TODO
													 * !portletRequest.
													 * isPortletModeAllowed
													 * (portletMode)
													 */) {
			throw new PortletModeException("Portlet mode not allowed: " + portletMode, portletMode);
		}
		setParameter(PortletConstant.PORTLET_URL_PORTLET_PORTLETMODE, portletMode.toString());
	}

	@Override
	public PortletMode getPortletMode() {
		String[] params = getParameter(PortletConstant.PORTLET_URL_PORTLET_PORTLETMODE);
		return PortletModeUtil.getPortletMode(params != null && params.length > 0 ? params[0] : null);
	}

	@Override
	public WindowState getWindowState() {
		String[] params = getParameter(PortletConstant.PORTLET_URL_PORTLET_WINDOWSTATE);
		return WindowStateUtil.getWindowState(params != null && params.length > 0 ? params[0] : null);
	}

	@Override
	public void removePublicRenderParameter(String name) {
		// TODO
		log.warn("not implemented");
	}
}
