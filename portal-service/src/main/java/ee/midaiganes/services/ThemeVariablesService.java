package ee.midaiganes.services;

import javax.inject.Inject;
import javax.portlet.PortletURL;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import ee.midaiganes.model.NavItem;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layout.LayoutResourceActions;
import ee.midaiganes.portal.layoutset.LayoutSet;
import ee.midaiganes.portal.layoutset.LayoutSetResourceActions;
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

    @Transactional
    public ImmutableList<ThemeVariable> getThemeVariables(HttpServletRequest request, HttpServletResponse response) {
        PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
        ImmutableList.Builder<ThemeVariable> variables = ImmutableList.builder();
        variables.add(new ThemeVariable(LOG_IN_URL, getLogInUrl()));
        variables.add(new ThemeVariable(NAV_ITEMS, getNavItems(pageDisplay)));
        variables.add(new ThemeVariable(PAGE_DISPLAY, pageDisplay));
        try {
            addPortletURLVariables(variables, request, response);
            addPermissionVariables(variables, pageDisplay);

        } catch (WindowStateException | ResourceNotFoundException | ResourceActionNotFoundException e) {
            throw new RuntimeException(e);
        }
        return variables.build();
    }

    private void addPortletURLVariables(ImmutableList.Builder<ThemeVariable> variables, HttpServletRequest request, HttpServletResponse response) throws WindowStateException {
        variables.add(new ThemeVariable(ADD_LAYOUT_URL, makeRenderURL(request, response, MidaiganesPortlets.LAYOUT_PORTLET)));
        variables.add(new ThemeVariable(ADD_REMOVE_PORTLET_URL, makeRenderURL(request, response, MidaiganesPortlets.ADD_REMOVE_PORTLET)));
        variables.add(new ThemeVariable(CHANGE_PAGE_LAYOUT_URL, makeRenderURL(request, response, MidaiganesPortlets.CHANGE_PAGE_LAYOUT)));
    }

    private PortletURL makeRenderURL(HttpServletRequest request, HttpServletResponse response, MidaiganesPortlets portlet) throws WindowStateException {
        return portletUrlFactor.makeRenderURL(request, response, portlet.getPortletName(), MidaiganesWindowState.EXCLUSIVE);
    }

    private void addPermissionVariables(ImmutableList.Builder<ThemeVariable> variables, PageDisplay pageDisplay) throws ResourceNotFoundException, ResourceActionNotFoundException {
        long userId = getUserId(pageDisplay);
        LayoutSet layoutSet = pageDisplay.getLayoutSet();
        String layoutSetResource = layoutSet.getResource();
        variables.add(new ThemeVariable("addPagePermission", permissionService.hasUserPermission(userId, layoutSetResource, layoutSet.getId(), LayoutSetResourceActions.EDIT)));
        Layout layout = pageDisplay.getLayout();
        String layoutResource = layout.getResource();
        long layoutId = layout.getId();
        variables.add(new ThemeVariable("changePageLayoutPermission", permissionService.hasUserPermission(userId, layoutResource, layoutId, LayoutResourceActions.EDIT)));
        variables.add(new ThemeVariable("addRemovePortletPermission", permissionService.hasUserPermission(userId, layoutResource, layoutId, LayoutResourceActions.ADD_PORTLET)));
        boolean changePagePermissionsPermission = permissionService.hasUserPermission(userId, layoutResource, layoutId, LayoutResourceActions.PERMISSIONS);
        variables.add(new ThemeVariable("changePagePermissionsPermission", changePagePermissionsPermission));
    }

    private ImmutableList<NavItem> getNavItems(PageDisplay pageDisplay) {
        ImmutableList<Layout> layouts = secureLayoutRepository.getLayouts(getUserId(pageDisplay), getLayoutSetId(pageDisplay));
        Iterable<Layout> filter = Iterables.filter(layouts, l -> l.getParentId() == null);
        Iterable<NavItem> transform = Iterables.transform(filter, l -> new NavItem(l, layouts, pageDisplay.getLanguageId()));
        return Ordering.natural().immutableSortedCopy(transform);
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

        private ThemeVariable(String name, boolean value) {
            this(name, Boolean.valueOf(value));
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }
}
