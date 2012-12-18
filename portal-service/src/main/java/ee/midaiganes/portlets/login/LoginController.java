package ee.midaiganes.portlets.login;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

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
	public String view() {
		return "login/view";
	}

	@RenderMapping(params = "loggedIn")
	public String loggedInView() {
		return "login/loggedInView";
	}

	@ActionMapping
	public void loginAction(ActionRequest request, ActionResponse response, @ModelAttribute("loginData") LoginData loginData) {
		User user = userRepository.getUser(loginData.getUsername(), loginData.getPassword());
		if (user != null) {
			SessionUtil.setUserId(request, user.getId());
			response.setRenderParameter("loggedIn", "1");
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
