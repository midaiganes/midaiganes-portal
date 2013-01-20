package ee.midaiganes.util;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.model.PortletNamespace;
import ee.midaiganes.model.Theme;
import ee.midaiganes.servlet.http.ThemePortletJspRequest;

public class ThemeUtil {
	private static final String PORTLET_JSP = "/portlet.jsp";

	public static void includePortletJsp(HttpServletRequest request, HttpServletResponse response, PortletNamespace namespace, String portletContent)
			throws ServletException, IOException {
		Theme theme = RequestUtil.getPageDisplay(request).getTheme();
		getRequestDispatcher(theme, request).include(new ThemePortletJspRequest(request, portletContent, namespace), response);
	}

	private static RequestDispatcher getRequestDispatcher(Theme theme, HttpServletRequest request) {
		return ContextUtil.getRequestDispatcher(request, theme.getThemeName().getContextWithSlash(), theme.getThemePath() + PORTLET_JSP);
	}
}
