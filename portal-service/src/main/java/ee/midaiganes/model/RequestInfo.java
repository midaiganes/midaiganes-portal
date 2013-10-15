package ee.midaiganes.model;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

public class RequestInfo {
	private PortletURL portletURL;

	public static class PortletURL {
		private WindowState windowState;
		private PortletMode portletMode;
		private PortletLifecycle lifecycle;
		private String windowID;
		private PortletName portletName;

		public WindowState getWindowState() {
			return windowState;
		}

		public void setWindowState(WindowState windowState) {
			this.windowState = windowState;
		}

		public PortletMode getPortletMode() {
			return portletMode;
		}

		public void setPortletMode(PortletMode portletMode) {
			this.portletMode = portletMode;
		}

		public PortletLifecycle getLifecycle() {
			return lifecycle;
		}

		public void setLifecycle(PortletLifecycle lifecycle) {
			this.lifecycle = lifecycle;
		}

		public String getWindowID() {
			return windowID;
		}

		public void setWindowID(String windowID) {
			this.windowID = windowID;
		}

		public PortletName getPortletName() {
			return portletName;
		}

		public void setPortletName(PortletName portletName) {
			this.portletName = portletName;
		}

		@Override
		public String toString() {
			return "PortletURL [windowState=" + windowState + ", portletMode=" + portletMode + ", lifecycle=" + lifecycle + ", windowID=" + windowID
					+ ", portletName=" + portletName + "]";
		}
	}

	public PortletURL getPortletURL() {
		return portletURL;
	}

	public void setPortletURL(PortletURL portletURL) {
		this.portletURL = portletURL;
	}

	@Override
	public String toString() {
		return "RequestInfo [portletURL=" + portletURL + "]";
	}
}
