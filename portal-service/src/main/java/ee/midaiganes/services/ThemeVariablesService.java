package ee.midaiganes.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.factory.PortletURLFactory;
import ee.midaiganes.model.Layout;
import ee.midaiganes.model.MidaiganesWindowState;
import ee.midaiganes.model.NavItem;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.util.MidaiganesPortlets;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;

public class ThemeVariablesService {
	private static final String LOG_IN_URL = "logInUrl";
	private static final String NAV_ITEMS = "navItems";
	private static final String ADD_LAYOUT_URL = "addLayoutUrl";
	private static final String CHANGE_PAGE_LAYOUT_URL = "changePageLayoutUrl";
	private static final String THEME_JAVASCRIPT_DIR = "themeJavascriptDir";
	private static final String ADD_REMOVE_PORTLET_URL = "addRemovePortletUrl";

	@Resource(name = RootApplicationContext.LAYOUT_REPOSITORY)
	private LayoutRepository layoutRepository;

	@Resource(name = RootApplicationContext.PORTLET_URL_FACTORY)
	private PortletURLFactory portletURLFactory;

	public List<ThemeVariable> getThemeVariables(HttpServletRequest request) {
		PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
		List<ThemeVariable> variables = new ArrayList<ThemeVariable>();
		variables.add(new ThemeVariable(LOG_IN_URL, getLogInUrl()));
		variables.add(new ThemeVariable(THEME_JAVASCRIPT_DIR, pageDisplay.getTheme().getThemeName().getContext() + pageDisplay.getTheme().getThemePath()
				+ StringPool.SLASH + pageDisplay.getTheme().getJavascriptPath()));
		variables.add(new ThemeVariable(NAV_ITEMS, getNavItems(pageDisplay)));
		try {
			variables.add(new ThemeVariable(ADD_LAYOUT_URL, portletURLFactory.makeRenderURL(request, MidaiganesPortlets.LAYOUT_PORTLET.getPortletName(),
					MidaiganesWindowState.EXCLUSIVE)));
			variables.add(new ThemeVariable(ADD_REMOVE_PORTLET_URL, portletURLFactory.makeRenderURL(request, MidaiganesPortlets.ADD_REMOVE_PORTLET.getPortletName(),
					MidaiganesWindowState.EXCLUSIVE)));
			variables.add(new ThemeVariable(CHANGE_PAGE_LAYOUT_URL, portletURLFactory.makeRenderURL(request,
					MidaiganesPortlets.CHANGE_PAGE_LAYOUT.getPortletName(), MidaiganesWindowState.EXCLUSIVE)));
		} catch (WindowStateException e) {
			throw new RuntimeException(e);
		}
		return variables;
	}

	private List<NavItem> getNavItems(PageDisplay pageDisplay) {
		List<NavItem> navItems = new ArrayList<NavItem>();
		List<Layout> layouts = layoutRepository.getLayouts(pageDisplay.getLayoutSet().getId());
		for (Layout layout : layouts) {
			if (layout.getParentId() == 0) {
				navItems.add(new NavItem(layout, layouts));
			}
		}
		Collections.sort(navItems);
		return navItems;
	}

	private String getLogInUrl() {
		return "/portal/login";
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
