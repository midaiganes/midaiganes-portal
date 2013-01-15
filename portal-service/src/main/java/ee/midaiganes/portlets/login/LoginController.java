package ee.midaiganes.portlets.login;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.model.DefaultUser;
import ee.midaiganes.model.User;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.util.PortalURLUtil;
import ee.midaiganes.util.SessionUtil;

@RequestMapping("VIEW")
@Controller(value = "loginController")
public class LoginController {

	@Resource
	private UserRepository userRepository;

	@RenderMapping
	public String view(PortletRequest request) {
		if (SessionUtil.getUserId(request) == DefaultUser.DEFAULT_USER_ID) {
			return "login/view";
		}
		return "login/loggedInView";
	}

	@ActionMapping
	public void loginAction(ActionRequest request, ActionResponse response, @ModelAttribute("loginData") LoginData loginData) {
		if (SessionUtil.getUserId(request) == DefaultUser.DEFAULT_USER_ID) {
			User user = userRepository.getUser(loginData.getUsername(), loginData.getPassword());
			if (user != null) {
				SessionUtil.setUserId(request, user.getId());
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
