package ee.midaiganes.util;

import java.util.Arrays;
import java.util.List;

import javax.portlet.PortletMode;

public class PortletModeUtil {
	private static final List<PortletMode> portalSupportedPortletModes = Arrays.asList(PortletMode.VIEW, PortletMode.EDIT);

	public static PortletMode getPortletMode(String pm) {
		for (PortletMode portletMode : portalSupportedPortletModes) {
			if (portletMode.toString().equals(pm)) {
				return portletMode;
			}
		}
		return null;
	}

	public static PortletMode getPortletMode(String pm, PortletMode def) {
		PortletMode portletMode = getPortletMode(pm);
		return portletMode != null ? portletMode : def;
	}
}
