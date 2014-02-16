package ee.midaiganes.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.factory.PortletURLFactory;
import ee.midaiganes.model.NavItem;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portlet.MidaiganesWindowState;
import ee.midaiganes.secureservices.SecureLayoutRepository;
import ee.midaiganes.util.MidaiganesPortlets;
import ee.midaiganes.util.PropsValues;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.Validate;

public class ThemeVariablesService {
    private static final String LOG_IN_URL = "logInUrl";
    private static final String NAV_ITEMS = "navItems";
    private static final String ADD_LAYOUT_URL = "addLayoutUrl";
    private static final String CHANGE_PAGE_LAYOUT_URL = "changePageLayoutUrl";
    private static final String ADD_REMOVE_PORTLET_URL = "addRemovePortletUrl";
    private static final String PAGE_DISPLAY = "pageDisplay";

    private final PortletURLFactory portletUrlFactor;
    private final SecureLayoutRepository secureLayoutRepository;

    public ThemeVariablesService(PortletURLFactory portletUrlFactor, SecureLayoutRepository secureLayoutRepository) {
        Validate.notNull(portletUrlFactor, "Portlet url factory is null");
        Validate.notNull(secureLayoutRepository, "Secure layout repository is null");

        this.portletUrlFactor = portletUrlFactor;
        this.secureLayoutRepository = secureLayoutRepository;
    }

    public List<ThemeVariable> getThemeVariables(HttpServletRequest request) {
        PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
        List<ThemeVariable> variables = new ArrayList<>();
        variables.add(new ThemeVariable(LOG_IN_URL, getLogInUrl()));
        variables.add(new ThemeVariable(NAV_ITEMS, getNavItems(pageDisplay)));
        variables.add(new ThemeVariable(PAGE_DISPLAY, pageDisplay));
        try {
            variables.add(new ThemeVariable(ADD_LAYOUT_URL, portletUrlFactor.makeRenderURL(request, MidaiganesPortlets.LAYOUT_PORTLET.getPortletName(),
                    MidaiganesWindowState.EXCLUSIVE)));
            variables.add(new ThemeVariable(ADD_REMOVE_PORTLET_URL, portletUrlFactor.makeRenderURL(request, MidaiganesPortlets.ADD_REMOVE_PORTLET.getPortletName(),
                    MidaiganesWindowState.EXCLUSIVE)));
            variables.add(new ThemeVariable(CHANGE_PAGE_LAYOUT_URL, portletUrlFactor.makeRenderURL(request, MidaiganesPortlets.CHANGE_PAGE_LAYOUT.getPortletName(),
                    MidaiganesWindowState.EXCLUSIVE)));
        } catch (WindowStateException e) {
            throw new RuntimeException(e);
        }
        return variables;
    }

    private List<NavItem> getNavItems(PageDisplay pageDisplay) {
        List<NavItem> navItems = new ArrayList<>();
        List<Layout> layouts = secureLayoutRepository.getLayouts(getUserId(pageDisplay), getLayoutSetId(pageDisplay));
        for (Layout layout : layouts) {
            if (layout.getParentId() == null) {
                navItems.add(new NavItem(layout, layouts));
            }
        }
        Collections.sort(navItems);
        return navItems;
    }

    private long getLayoutSetId(PageDisplay pageDisplay) {
        return pageDisplay.getLayoutSet().getId();
    }

    private long getUserId(PageDisplay pageDisplay) {
        return pageDisplay.getUser().getId();
    }

    private String getLogInUrl() {
        return PropsValues.LOGIN_URL;
    }

    public static class ThemeVariable {
        private final String name;
        private final Object value;

        private ThemeVariable(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }
}
