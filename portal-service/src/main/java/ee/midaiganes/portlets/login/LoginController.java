package ee.midaiganes.portlets.login;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.RootApplicationContext;
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
		return "login/loggedInView";
	}

	@ActionMapping
	public void loginAction(ActionRequest request, ActionResponse response, @ModelAttribute("loginData") LoginData loginData) {
		log.trace("Login data = {}", loginData);
		if (!UserUtil.isLoggedIn(request)) {
			User user = userRepository.getUser(loginData.getUsername(), loginData.getPassword());
			if (user != null) {
				SessionUtil.setUserId(request, user.getId());
			} else {
				log.debug("User not found {}", loginData);
			}
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
}
