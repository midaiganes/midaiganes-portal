package ee.midaiganes.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.Theme;
import ee.midaiganes.services.ThemeVariablesService;

public class ThemeServlet extends HttpServlet {
	public static final String THEME = ThemeServlet.class.getName() + ".THEME";
	private static final Logger log = LoggerFactory.getLogger(ThemeServlet.class);

	private final ThemeVariablesService themeVariablesService = ThemeVariablesService.getInstance();

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			Theme theme = (Theme) request.getAttribute(THEME);
			request = setThemeVariables(request, themeVariablesService.getThemeVariables(request));
			request.getRequestDispatcher(theme.getPortalNormalPath()).include(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private HttpServletRequest setThemeVariables(HttpServletRequest request, List<ThemeVariablesService.ThemeVariable> variables) {
		for (ThemeVariablesService.ThemeVariable tv : variables) {
			request.setAttribute(tv.getName(), tv.getValue());
		}
		return request;
	}
}
