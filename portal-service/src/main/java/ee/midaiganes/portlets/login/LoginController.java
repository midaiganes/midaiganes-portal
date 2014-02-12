package ee.midaiganes.portlets.login;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.model.MidaiganesWindowState;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.util.PortalURLUtil;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.UserUtil;

public class LoginController extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        if ("logout".equals(request.getParameter("action"))) {
            logoutAction(request, response);
        } else {
            LoginData loginData = new LoginData();
            loginData.setUsername(request.getParameter("username"));
            loginData.setPassword(request.getParameter("password"));
            loginAction(request, response, loginData);
        }
    }

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        request.setAttribute("afterLoginUrl", getAfterLoginUrl());
        if (!UserUtil.isLoggedIn(request)) {
            super.include("login/view", request, response);
        } else if (MidaiganesWindowState.EXCLUSIVE.equals(request.getWindowState())) {
            super.include("login/loggedInView", request, response);
        }
    }

    private void loginAction(ActionRequest request, ActionResponse response, LoginData loginData) throws IOException {
        log.trace("Login data = {}", loginData);
        if (!UserUtil.isLoggedIn(request)) {
            User user = BeanRepositoryUtil.getBean(UserRepository.class).getUser(loginData.getUsername(), loginData.getPassword());
            if (user != null) {
                SessionUtil.setUserId(request, user.getId());
                if (WindowState.NORMAL.equals(request.getWindowState())) {
                    response.sendRedirect(getAfterLoginUrl());
                }
            } else {
                log.debug("User not found {}", loginData);
            }
        }
    }

    private void logoutAction(ActionRequest request, ActionResponse response) throws IOException {
        if (UserUtil.isLoggedIn(request)) {
            SessionUtil.setUserId(request, null);
            response.sendRedirect(getAfterLogoutUrl());
        }
    }

    private String getAfterLoginUrl() {
        return PortalURLUtil.getFullURLByFriendlyURL("");
    }

    private String getAfterLogoutUrl() {
        return PortalURLUtil.getFullURLByFriendlyURL("");
    }

}
