package ee.midaiganes.portlets.layout;

import javax.annotation.Resource;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.services.LanguageRepository;
import ee.midaiganes.services.LayoutRepository;
import ee.midaiganes.services.PageLayoutRepository;
import ee.midaiganes.services.exceptions.IllegalFriendlyUrlException;
import ee.midaiganes.services.exceptions.IllegalLanguageIdException;
import ee.midaiganes.services.exceptions.IllegalPageLayoutException;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringUtil;

@Controller("layoutsController")
@RequestMapping("VIEW")
public class LayoutsController {
	private static final Logger log = LoggerFactory.getLogger(LayoutsController.class);

	@Resource(name = RootApplicationContext.LAYOUT_REPOSITORY)
	private LayoutRepository layoutRepository;

	@Resource
	private LanguageRepository languageRepository;

	@Resource
	private PageLayoutRepository pageLayoutRepository;

	@RenderMapping
	public String addLayoutView(RenderRequest request, Model model) {
		model.addAttribute("layouts", layoutRepository.getLayouts(RequestUtil.getPageDisplay(request).getLayoutSet().getId()));
		return "layouts/add-page";
	}

	@ActionMapping
	public void addLayoutAction(@ModelAttribute("addLayoutModel") LayoutModel layout, PortletRequest request) throws IllegalFriendlyUrlException,
			IllegalLanguageIdException, IllegalPageLayoutException {
		if (layoutRepository.isFriendlyUrlValid(layout.getUrl())) {
			layoutRepository.addLayout(RequestUtil.getPageDisplay(request).getLayoutSet().getId(), layout.getUrl(), null, pageLayoutRepository
					.getDefaultPageLayout().getPageLayoutName(), null, languageRepository.getSupportedLanguageIds().get(0));
		} else {
			log.warn("invalid friendly url '{}'", layout.getUrl());
		}
	}

	@ActionMapping(params = { "action=delete", "id" })
	public void deleteLayoutAction(@RequestParam("id") String id) {
		if (StringUtil.isNumber(id)) {
			layoutRepository.deleteLayout(Long.parseLong(id));
		}
	}

	@ModelAttribute("addLayoutModel")
	public LayoutModel getAddLayoutModel() {
		return new LayoutModel();
	}
}
