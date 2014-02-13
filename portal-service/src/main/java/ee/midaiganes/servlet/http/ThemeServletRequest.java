package ee.midaiganes.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.servlet.ThemeServlet;

public class ThemeServletRequest extends HttpServletRequestWrapper {
	private final Theme theme;

	public ThemeServletRequest(Theme theme, HttpServletRequest request) {
		super(request);
		this.theme = theme;
	}

	@Override
	public Object getAttribute(String name) {
		if (ThemeServlet.THEME.equals(name)) {
			return theme;
		}
		return super.getAttribute(name);
	}

}
