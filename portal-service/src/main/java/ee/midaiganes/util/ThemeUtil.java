package ee.midaiganes.util;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.Theme;
import ee.midaiganes.portal.portletinstance.PortletInstance;
import ee.midaiganes.servlet.http.ThemePortletJspRequest;

public class ThemeUtil {

	public static void includePortletJsp(HttpServletRequest request, HttpServletResponse response, PortletInstance portletInstance, String portletContent)
			throws ServletException, IOException {
		Theme theme = RequestUtil.getPageDisplay(request).getTheme();
		getRequestDispatcher(theme, request).include(new ThemePortletJspRequest(request, portletContent, portletInstance), response);
	}

	private static RequestDispatcher getRequestDispatcher(Theme theme, HttpServletRequest request) {
		return ContextUtil.getRequestDispatcher(request, theme.getThemeName().getContextWithSlash(), theme.getPortletPath());
	}
}
