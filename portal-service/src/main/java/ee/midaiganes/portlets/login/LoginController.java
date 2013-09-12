package ee.midaiganes.portlets.login;

import java.io.IOException;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.model.MidaiganesWindowState;
import ee.midaiganes.model.User;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.util.PortalURLUtil;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.UserUtil;

@RequestMapping("VIEW")
@Controller(value = "loginController")
public class LoginController {
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Resource(name = RootApplicationContext.USER_REPOSITORY)
	private UserRepository userRepository;

	@RenderMapping
	public String view(PortletRequest request) {
		if (!UserUtil.isLoggedIn(request)) {
			return "login/view";
		}
		return MidaiganesWindowState.EXCLUSIVE.equals(request.getWindowState()) ? "login/loggedInView" : null;
	}

	@ActionMapping
	public void loginAction(ActionRequest request, ActionResponse response, @ModelAttribute("loginData") LoginData loginData) throws IOException {
		log.trace("Login data = {}", loginData);
		if (!UserUtil.isLoggedIn(request)) {
			User user = userRepository.getUser(loginData.getUsername(), loginData.getPassword());
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

	@ActionMapping(params = { "action=logout" })
	public void logoutAction(ActionRequest request, ActionResponse response) throws IOException {
		if (UserUtil.isLoggedIn(request)) {
			SessionUtil.setUserId(request, null);
			response.sendRedirect(getAfterLogoutUrl());
		}
	}

	@ModelAttribute("loginData")
	public LoginData getLoginData() {
		return new LoginData();
	}

	@ModelAttribute("afterLoginUrl")
	public String getAfterLoginUrl() {
		return PortalURLUtil.getFullURLByFriendlyURL("");
	}

	private String getAfterLogoutUrl() {
		return PortalURLUtil.getFullURLByFriendlyURL("");
	}
}
