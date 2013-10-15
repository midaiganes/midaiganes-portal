package ee.midaiganes.tag;

import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import ee.midaiganes.model.PortletLifecycle;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.portlet.impl.PortletURLImpl;
import ee.midaiganes.portlet.impl.jsr286.taglib.PortletURLTag;
import ee.midaiganes.util.PortletModeUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.WindowStateUtil;

public abstract class PortalPortletURLTag extends PortletURLTag {
	private String windowState;
	private String portletMode;
	private String secure;
	private PortletName portletName;

	@Override
	public void release() {
		this.windowState = null;
		this.portletMode = null;
		this.secure = null;
		this.portletName = null;
		super.release();
	}

	@Override
	protected PortletURL getPortletURL() {
		return new PortletURLImpl(getHttpServletRequest(), StringPool.DEFAULT_PORTLET_WINDOWID, WindowStateUtil.getPortalSupportedWindowStates(),
				PortletModeUtil.getPortalSupportedPortletModes(), getPortletLifecycle(), getPortletName());
	}

	protected abstract PortletLifecycle getPortletLifecycle();

	@Override
	protected PortletMode getPortletMode() {
		return PortletModeUtil.getPortletMode(portletMode, PortletMode.VIEW);
	}

	@Override
	protected WindowState getWindowState() {
		return WindowStateUtil.getWindowState(windowState, WindowState.NORMAL);
	}

	@Override
	protected boolean isSecure() {
		return secure == null ? getHttpServletRequest().isSecure() : Boolean.parseBoolean(secure);
	}

	@Override
	public void setPortletMode(String portletMode) {
		this.portletMode = portletMode;
	}

	@Override
	public void setSecure(String secure) {
		this.secure = secure;
	}

	@Override
	public void setWindowState(String windowState) {
		this.windowState = windowState;
	}

	public void setPortletName(String portletName) {
		this.portletName = new PortletName(portletName);
	}

	private PortletName getPortletName() {
		return portletName;
	}
}
