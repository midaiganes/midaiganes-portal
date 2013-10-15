package ee.midaiganes.util;

import java.util.Arrays;
import java.util.List;

import javax.portlet.WindowState;

import ee.midaiganes.model.MidaiganesWindowState;

public class WindowStateUtil {
	private static final List<WindowState> portalSupportedWindowStates = Arrays.asList(WindowState.NORMAL, MidaiganesWindowState.EXCLUSIVE);

	public static List<WindowState> getPortalSupportedWindowStates() {
		return portalSupportedWindowStates;
	}

	public static WindowState getWindowState(String ws) {
		for (WindowState windowState : portalSupportedWindowStates) {
			if (windowState.toString().equals(ws)) {
				return windowState;
			}
		}
		return null;
	}

	public static WindowState getWindowState(String ws, WindowState def) {
		return GetterUtil.get(getWindowState(ws), def);
	}
}
