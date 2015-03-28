package ee.midaiganes.portlet;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import ee.midaiganes.generated.xml.portlet.PortletModeType;
import ee.midaiganes.generated.xml.portlet.PortletType;
import ee.midaiganes.generated.xml.portlet.SupportsType;
import ee.midaiganes.generated.xml.portlet.WindowStateType;
import ee.midaiganes.util.PortletModeUtil;
import ee.midaiganes.util.WindowStateUtil;

public class PortletAndConfiguration {
    private final MidaiganesPortlet midaiganesPortlet;
    private final PortletConfig portletConfig;
    private final ImmutableList<WindowState> supportedWindowStates;
    private final ImmutableList<PortletMode> supportedPortletModes;

    public PortletAndConfiguration(MidaiganesPortlet portlet, PortletConfig portletConfig, PortletType portletType) {
        this.midaiganesPortlet = portlet;
        this.portletConfig = portletConfig;
        this.supportedWindowStates = getSupportedWindowStates(portletType);
        this.supportedPortletModes = getSupportedPortletModes(portletType);
    }

    public MidaiganesPortlet getMidaiganesPortlet() {
        return midaiganesPortlet;
    }

    public PortletConfig getPortletConfig() {
        return portletConfig;
    }

    public ImmutableList<WindowState> getSupportedWindowStates() {
        return supportedWindowStates;
    }

    public ImmutableList<PortletMode> getSupportedPortletModes() {
        return supportedPortletModes;
    }

    private ImmutableList<PortletMode> getSupportedPortletModes(PortletType portletType) {
        ImmutableSet.Builder<PortletMode> portletModes = ImmutableSet.builder();

        for (SupportsType supports : portletType.getSupports()) {
            for (PortletModeType pmt : supports.getPortletMode()) {
                PortletMode pm = PortletModeUtil.getPortletMode(pmt.getValue());
                if (pm != null) {
                    portletModes.add(pm);
                }
            }
        }
        return portletModes.build().asList();
    }

    private ImmutableList<WindowState> getSupportedWindowStates(PortletType portletType) {
        ImmutableSet.Builder<WindowState> windowStates = ImmutableSet.builder();
        for (SupportsType supports : portletType.getSupports()) {
            for (WindowStateType wst : supports.getWindowState()) {
                WindowState ws = WindowStateUtil.getWindowState(wst.getValue());
                if (ws != null) {
                    windowStates.add(ws);
                }
            }
        }
        return windowStates.build().asList();
    }

    @Override
    public String toString() {
        return "PortletAndConfiguration [supportedWindowStates=" + supportedWindowStates + ", supportedPortletModes=" + supportedPortletModes + "]";
    }
}
