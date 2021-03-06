package ee.midaiganes.servlet.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.StringUtil;

public class LoginFilter extends HttpFilter {
    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);
    private static final String LOGIN = "login";
    private static final String PASSWORD = "pwd";
    private static final String REDIRECT = "redirect";

    @Inject
    private UserRepository userRepository;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String redirect = doLoginAndGetRedirectUrl(request);
        if (!StringUtil.isEmpty(redirect)) {
            try {
                response.sendRedirect(redirect);
                return;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        chain.doFilter(request, response);
    }

    private String doLoginAndGetRedirectUrl(HttpServletRequest request) {
        if (User.isDefaultUserId(SessionUtil.getUserId(request))) {
            log.debug("not logged in");
            String login = request.getParameter(LOGIN);
            String password = request.getParameter(PASSWORD);
            String redirect = request.getParameter(REDIRECT);
            if (!StringUtil.isEmpty(login) && StringUtil.isEmpty(password)) {
                User user = userRepository.getUser(login, password);
                if (user != null) {
                    SessionUtil.setUserId(request, Long.valueOf(user.getId()));
                    RequestUtil.setPageDisplay(request, new PageDisplay(RequestUtil.getPageDisplay(request), user));
                    if (!StringUtil.isEmpty(redirect)) {
                        return removeRedirectLoop(redirect, login, password);
                    }
                }
            }
        }
        return null;
    }

    private String removeRedirectLoop(String redirect, String login, String pwd) {
        return redirect.replace(LOGIN + "=" + login, "").replace(PASSWORD + "=" + pwd, "");
    }
}
