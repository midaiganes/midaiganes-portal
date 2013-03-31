package ee.midaiganes.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.model.DefaultUser;
import ee.midaiganes.model.Layout;
import ee.midaiganes.model.LayoutSet;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.model.Theme;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.model.User;
import ee.midaiganes.services.LayoutRepository;
import ee.midaiganes.services.LayoutSetRepository;
import ee.midaiganes.services.RequestParser;
import ee.midaiganes.services.ThemeRepository;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.SessionUtil;

public class PortalFilter extends HttpFilter {
	private static final Logger log = LoggerFactory.getLogger(PortalFilter.class);

	@Resource(name = RootApplicationContext.LAYOUT_SET_REPOSITORY)
	private LayoutSetRepository layoutSetRepository;

	@Resource(name = RootApplicationContext.LAYOUT_REPOSITORY)
	private LayoutRepository layoutRepository;

	@Resource(name = RootApplicationContext.USER_REPOSITORY)
	private UserRepository userRepository;

	@Resource(name = RootApplicationContext.REQUEST_PARSER)
	private RequestParser requestParser;

	@Resource(name = PortalConfig.THEME_REPOSITORY)
	private ThemeRepository themeRepository;

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			PageDisplay pageDisplay = new PageDisplay();
			pageDisplay.setRequestInfo(requestParser.parserRequest(request));
			pageDisplay.setLayoutSet(getLayoutSet(request.getServerName()));
			pageDisplay.setLayout(getLayout(pageDisplay.getLayoutSet(), RequestUtil.getFriendlyURL(request.getRequestURI())));
			pageDisplay.setUser(getUser(request));
			pageDisplay.setTheme(getTheme(pageDisplay));
			RequestUtil.setPageDisplay(request, pageDisplay);
			if (pageDisplay.getLayout().isDefault()) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private Theme getTheme(PageDisplay pageDisplay) {
		ThemeName themeName = pageDisplay.getLayout().getThemeName();
		if (themeName != null) {
			Theme theme = themeRepository.getTheme(themeName);
			if (theme != null) {
				return theme;
			}
		}
		themeName = pageDisplay.getLayoutSet().getThemeName();
		if (themeName != null) {
			Theme theme = themeRepository.getTheme(themeName);
			if (theme != null) {
				return theme;
			}
		}
		return themeRepository.getDefaultTheme();
	}

	private Layout getLayout(LayoutSet layoutSet, String friendlyUrl) {
		Layout layout = layoutRepository.getLayout(layoutSet.getId(), friendlyUrl);
		if (layout != null) {
			return layout;
		}
		return layoutRepository.getDefaultLayout(layoutSet.getId(), friendlyUrl);
	}

	private LayoutSet getLayoutSet(String virtualHost) {
		LayoutSet layoutSet = layoutSetRepository.getLayoutSet(virtualHost);
		if (layoutSet != null) {
			return layoutSet;
		}
		return layoutSetRepository.getDefaultLayoutSet(virtualHost);
	}

	private User getUser(HttpServletRequest request) {
		long userid = SessionUtil.getUserId(request);
		User user = null;
		if (userid != DefaultUser.DEFAULT_USER_ID) {
			user = userRepository.getUser(userid);
		}
		return user != null ? user : new DefaultUser();
	}
}
