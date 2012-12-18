package ee.midaiganes.portlets.welcome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller("welcomePortletController")
@RequestMapping("VIEW")
public class WelcomePortletController {
	private static final Logger log = LoggerFactory.getLogger(WelcomePortletController.class);

	@RenderMapping
	public String view() {
		log.debug("view");
		return "welcome";
	}

	@ModelAttribute("daa")
	public String getDaa() {
		return "test-daaa";
	}

	@ActionMapping
	public void action(Model model) {
		log.error("action");
		model.addAttribute("faa", "test");
	}

}
