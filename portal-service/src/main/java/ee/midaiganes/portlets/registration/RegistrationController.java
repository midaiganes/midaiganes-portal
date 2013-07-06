package ee.midaiganes.portlets.registration;

import javax.portlet.ActionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.services.UserRepository;
import ee.midaiganes.services.exceptions.DuplicateUsernameException;
import ee.midaiganes.util.StringUtil;

@Controller
@RequestMapping("VIEW")
public class RegistrationController {
	private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private UserRepository userRepository;

	@RenderMapping
	public String view() {
		return "registration/view";
	}

	@ActionMapping
	public void registerUser(@ModelAttribute("registrationData") RegistrationData registrationData, ActionRequest request) {
		if (!StringUtil.isEmpty(registrationData.getUsername()) && !StringUtil.isEmpty(registrationData.getPassword())) {
			try {
				log.debug("add user");
				long userid = userRepository.addUser(registrationData.getUsername(), registrationData.getPassword());
				log.debug("user added: id = {}; name = {}", userid, registrationData.getUsername());
				request.setAttribute("success", Boolean.TRUE);
			} catch (DuplicateUsernameException e) {
				log.debug("duplicate username", e);
			}
		}
	}

	@ModelAttribute("registrationData")
	public RegistrationData getRegistrationData() {
		return new RegistrationData();
	}
}
