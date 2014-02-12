package ee.midaiganes.model;

import ee.midaiganes.model.RequestInfo.PortletURL;
import ee.midaiganes.portal.user.User;

public class PageDisplay {
	private LayoutSet layoutSet;
	private Layout layout;
	private User user;
	private RequestInfo requestInfo;
	private Theme theme;

	public LayoutSet getLayoutSet() {
		return layoutSet;
	}

	public void setLayoutSet(LayoutSet layoutSet) {
		this.layoutSet = layoutSet;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public PortletURL getPortletURL() {
		if (requestInfo != null) {
			return requestInfo.getPortletURL();
		}
		return null;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}
}
