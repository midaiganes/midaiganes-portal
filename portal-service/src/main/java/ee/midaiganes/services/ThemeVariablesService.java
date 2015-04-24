package ee.midaiganes.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import ee.midaiganes.model.NavItem;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.permission.PermissionService;
import ee.midaiganes.portlet.MidaiganesWindowState;
import ee.midaiganes.secureservices.SecureLayoutRepository;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;
import ee.midaiganes.util.MidaiganesPortlets;
import ee.midaiganes.util.PropsValues;
import ee.midaiganes.util.RequestUtil;

public class ThemeVariablesService {
    private static final String LOG_IN_URL = "logInUrl";
    private static final String NAV_ITEMS = "navItems";
    private static final String ADD_LAYOUT_URL = "addLayoutUrl";
    private static final String CHANGE_PAGE_LAYOUT_URL = "changePageLayoutUrl";
    private static final String ADD_REMOVE_PORTLET_URL = "addRemovePortletUrl";
    private static final String PAGE_DISPLAY = "pageDisplay";

    private final PortletURLFactory portletUrlFactor;
    private final SecureLayoutRepository secureLayoutRepository;
    private final PermissionService permissionService;

    @Inject
    public ThemeVariablesService(PortletURLFactory portletUrlFactor, SecureLayoutRepository secureLayoutRepository, PermissionService permissionService) {
        Preconditions.checkNotNull(portletUrlFactor, "Portlet url factory is null");
        Preconditions.checkNotNull(secureLayoutRepository, "Secure layout repository is null");

        this.portletUrlFactor = portletUrlFactor;
        this.secureLayoutRepository = secureLayoutRepository;
        this.permissionService = permissionService;
    }

    public ImmutableList<ThemeVariable> getThemeVariables(HttpServletRequest request) {
        PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
        ImmutableList.Builder<ThemeVariable> variables = ImmutableList.builder();
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

            //
            long userId = pageDisplay.getUser().getId();

            variables.add(new ThemeVariable("addPagePermission", Boolean.valueOf(permissionService.hasUserPermission(userId, pageDisplay.getLayoutSet().getResource(), pageDisplay
                    .getLayoutSet().getId(), "EDIT"))));
            variables.add(new ThemeVariable("changePageLayoutPermission", Boolean.valueOf(permissionService.hasUserPermission(userId, pageDisplay.getLayout().getResource(),
                    pageDisplay.getLayout().getId(), "EDIT"))));
            variables.add(new ThemeVariable("addRemovePortletPermission", Boolean.valueOf(permissionService.hasUserPermission(userId, pageDisplay.getLayout().getResource(),
                    pageDisplay.getLayout().getId(), "ADD_PORTLET"))));
            variables.add(new ThemeVariable("changePagePermissionsPermission", Boolean.valueOf(permissionService.hasUserPermission(userId, pageDisplay.getLayout().getResource(),
                    pageDisplay.getLayout().getId(), "PERMISSIONS"))));

        } catch (WindowStateException | ResourceNotFoundException | ResourceActionNotFoundException e) {
            throw new RuntimeException(e);
        }
        return variables.build();
    }

    private List<NavItem> getNavItems(PageDisplay pageDisplay) {
        List<NavItem> navItems = new ArrayList<>();
        List<Layout> layouts = secureLayoutRepository.getLayouts(getUserId(pageDisplay), getLayoutSetId(pageDisplay));
        for (Layout layout : layouts) {
            if (layout.getParentId() == null) {
                navItems.add(new NavItem(layout, layouts, pageDisplay.getLanguageId()));
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
