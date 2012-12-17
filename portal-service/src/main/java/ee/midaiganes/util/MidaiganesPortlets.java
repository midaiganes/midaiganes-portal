package ee.midaiganes.util;

import ee.midaiganes.model.PortletName;

public enum MidaiganesPortlets {
	LAYOUT_PORTLET("layout"), CHANGE_PAGE_LAYOUT("page-layout"), ADD_REMOVE_PORTLET("add-remove-portlet");

	private final PortletName portletName;

	private MidaiganesPortlets(String name) {
		this.portletName = new PortletName(PropsValues.PORTAL_CONTEXT.replace(StringPool.SLASH, StringPool.EMPTY), name);
	}

	public PortletName getPortletName() {
		return portletName;
	}
}
