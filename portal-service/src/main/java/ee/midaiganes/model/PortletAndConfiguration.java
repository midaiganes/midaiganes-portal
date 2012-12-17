package ee.midaiganes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import ee.midaiganes.generated.xml.portlet.PortletModeType;
import ee.midaiganes.generated.xml.portlet.PortletType;
import ee.midaiganes.generated.xml.portlet.SupportsType;
import ee.midaiganes.generated.xml.portlet.WindowStateType;
import ee.midaiganes.util.PortletModeUtil;
import ee.midaiganes.util.WindowStateUtil;

public class PortletAndConfiguration {
	private final Portlet portlet;
	private final PortletConfig portletConfig;
	private final List<WindowState> supportedWindowStates;
	private final List<PortletMode> supportedPortletModes;

	public PortletAndConfiguration(Portlet portlet, PortletConfig portletConfig, PortletType portletType) {
		this.portlet = portlet;
		this.portletConfig = portletConfig;
		this.supportedWindowStates = getSupportedWindowStates(portletType);
		this.supportedPortletModes = getSupportedPortletModes(portletType);
	}

	public Portlet getPortlet() {
		return portlet;
	}

	public PortletConfig getPortletConfig() {
		return portletConfig;
	}

	public List<WindowState> getSupportedWindowStates() {
		return supportedWindowStates;
	}

	public List<PortletMode> getSupportedPortletModes() {
		return supportedPortletModes;
	}

	private List<PortletMode> getSupportedPortletModes(PortletType portletType) {
		List<PortletMode> portletModes = new ArrayList<PortletMode>();
		for (SupportsType supports : portletType.getSupports()) {
			for (PortletModeType pmt : supports.getPortletMode()) {
				PortletMode pm = PortletModeUtil.getPortletMode(pmt.getValue());
				if (pm != null && !portletModes.contains(pm)) {
					portletModes.add(pm);
				}
			}
		}
		return Collections.unmodifiableList(new ArrayList<PortletMode>(portletModes));
	}

	private List<WindowState> getSupportedWindowStates(PortletType portletType) {
		List<WindowState> windowStates = new ArrayList<WindowState>();
		for (SupportsType supports : portletType.getSupports()) {
			for (WindowStateType wst : supports.getWindowState()) {
				WindowState ws = WindowStateUtil.getWindowState(wst.getValue());
				if (ws != null && !windowStates.contains(ws)) {
					windowStates.add(ws);
				}
			}
		}
		return Collections.unmodifiableList(new ArrayList<WindowState>(windowStates));
	}

	@Override
	public String toString() {
		return "PortletAndConfiguration [portlet=" + portlet + ", supportedWindowStates=" + supportedWindowStates + ", supportedPortletModes="
				+ supportedPortletModes + "]";
	}
}
