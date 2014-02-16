package ee.midaiganes.servlet.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layout.LayoutRepository;
import ee.midaiganes.portal.layoutset.LayoutSet;
import ee.midaiganes.portal.layoutset.LayoutSetRepository;
import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.portal.theme.ThemeName;
import ee.midaiganes.portal.theme.ThemeRepository;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.secureservices.SecureLayoutRepository;
import ee.midaiganes.services.RequestParser;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.StringPool;

public class PortalFilter extends HttpFilter {
    private static final Logger log = LoggerFactory.getLogger(PortalFilter.class);

    @Resource(name = PortalConfig.LAYOUT_SET_REPOSITORY)
    private LayoutSetRepository layoutSetRepository;

    @Resource(name = PortalConfig.LAYOUT_REPOSITORY)
    private LayoutRepository layoutRepository;

    @Resource(name = PortalConfig.USER_REPOSITORY)
    private UserRepository userRepository;

    @Resource(name = PortalConfig.THEME_REPOSITORY)
    private ThemeRepository themeRepository;

    private RequestParser requestParser;
    private SecureLayoutRepository secureLayoutRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.secureLayoutRepository = BeanRepositoryUtil.getBean(SecureLayoutRepository.class);
        this.requestParser = BeanRepositoryUtil.getBean(RequestParser.class);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            PageDisplay pageDisplay = new PageDisplay();
            pageDisplay.setRequestInfo(requestParser.parserRequest(request));
            LayoutSet layoutSet = getLayoutSet(request.getServerName());
            pageDisplay.setLayoutSet(layoutSet);
            User user = getUser(request);
            pageDisplay.setUser(user);
            Layout layout = getLayout(user.getId(), layoutSet.getId(), RequestUtil.getFriendlyURL(request.getRequestURI()));
            pageDisplay.setLayout(layout);
            pageDisplay.setTheme(getTheme(layout, layoutSet));
            RequestUtil.setPageDisplay(request, pageDisplay);
            if (pageDisplay.getLayout().isDefault()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Theme getTheme(Layout layout, LayoutSet layoutSet) {
        ThemeName themeName = layout.getThemeName();
        if (themeName != null) {
            Theme theme = themeRepository.getTheme(themeName);
            if (theme != null) {
                return theme;
            }
        }
        themeName = layoutSet.getThemeName();
        if (themeName != null) {
            Theme theme = themeRepository.getTheme(themeName);
            if (theme != null) {
                return theme;
            }
        }
        return themeRepository.getDefaultTheme();
    }

    private Layout getLayout(long userId, long layoutSetId, String friendlyUrl) {
        try {
            final Layout layout;
            if (StringPool.SLASH.equals(friendlyUrl)) {
                layout = secureLayoutRepository.getHomeLayout(userId, layoutSetId);
            } else {
                layout = secureLayoutRepository.getLayout(userId, layoutSetId, friendlyUrl);
            }
            if (layout != null) {
                return layout;
            }
        } catch (PrincipalException e) {
            // TODO handle this..
            log.info("User '{}' is not allowd to access '{}'", Long.valueOf(userId), friendlyUrl);
        }
        return layoutRepository.get404Layout(layoutSetId, friendlyUrl);
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
        if (!User.isDefaultUserId(userid)) {
            user = userRepository.getUser(userid);
        }
        return user != null ? user : User.getDefault();
    }
}
