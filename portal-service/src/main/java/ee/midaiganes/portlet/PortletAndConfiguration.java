package ee.midaiganes.portlet;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import ee.midaiganes.generated.xml.portlet.PortletType;
import ee.midaiganes.generated.xml.portlet.SupportsType;
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
        return getList(portletType, s -> s.getPortletMode(), pm -> PortletModeUtil.getPortletMode(pm.getValue()));
    }

    private ImmutableList<WindowState> getSupportedWindowStates(PortletType portletType) {
        return getList(portletType, s -> s.getWindowState(), ws -> WindowStateUtil.getWindowState(ws.getValue()));
    }

    private <A, B> ImmutableList<B> getList(PortletType pt, Function<SupportsType, Iterable<A>> f1, Function<A, B> f2) {
        return ImmutableList.copyOf(Iterables.filter(Iterables.transform(getConcated(pt, f1), f2), Predicates.notNull()));
    }

    private <A> Iterable<A> getConcated(PortletType portletType, Function<SupportsType, Iterable<A>> f) {
        return Iterables.concat(Iterables.transform(portletType.getSupports(), f));
    }

    @Override
    public String toString() {
        return "PortletAndConfiguration [supportedWindowStates=" + supportedWindowStates + ", supportedPortletModes=" + supportedPortletModes + "]";
    }
}
