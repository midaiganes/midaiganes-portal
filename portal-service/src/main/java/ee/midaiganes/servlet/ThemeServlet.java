package ee.midaiganes.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import ee.midaiganes.beans.Utils;
import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.services.ThemeVariablesService;

public class ThemeServlet extends HttpServlet {
    public static final String THEME = ThemeServlet.class.getName() + ".THEME";
    private static final Logger log = LoggerFactory.getLogger(ThemeServlet.class);

    private ThemeVariablesService themeVariablesService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.themeVariablesService = Utils.getInstance().getInstance(ThemeVariablesService.class);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            Theme theme = (Theme) request.getAttribute(THEME);
            setThemeVariables(request, themeVariablesService.getThemeVariables(request, response));
            request.getRequestDispatcher(theme.getPortalNormalPath()).include(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setThemeVariables(HttpServletRequest request, ImmutableList<ThemeVariablesService.ThemeVariable> variables) {
        for (ThemeVariablesService.ThemeVariable tv : variables) {
            request.setAttribute(tv.getName(), tv.getValue());
        }
    }
}
