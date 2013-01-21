package ee.midaiganes.portlets.pagelayout;

import java.util.List;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
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

import ee.midaiganes.model.ContextAndName;
import ee.midaiganes.model.Layout;
import ee.midaiganes.model.PageLayout;
import ee.midaiganes.services.LayoutRepository;
import ee.midaiganes.services.PageLayoutRepository;
import ee.midaiganes.util.RequestUtil;

@Controller("pageLayoutController")
@RequestMapping("VIEW")
public class PageLayoutController {
	private static final Logger log = LoggerFactory.getLogger(PageLayoutController.class);

	@Resource
	private PageLayoutRepository pageLayoutRepository;

	@Resource
	private LayoutRepository layoutRepository;

	@RenderMapping
	public String view(RenderRequest request, Model model) {
		PageLayout pageLayout = pageLayoutRepository.getPageLayout(getPageLayoutId(request));
		if (pageLayout != null) {
			model.addAttribute("pageLayoutName", pageLayout.getPageLayoutName());
		}
		return "pagelayout/view";
	}

	@ActionMapping(params = { "pageLayoutId" })
	public void setPageLayout(ActionRequest request, @RequestParam("pageLayoutId") String pageLayoutId) {
		if (ContextAndName.isValidFullName(pageLayoutId)) {
			PageLayout pageLayout = pageLayoutRepository.getPageLayout(pageLayoutId);
			if (pageLayout != null) {
				layoutRepository.updatePageLayout(getLayout(request).getId(), pageLayout.getPageLayoutName());
			} else {
				log.warn("page layout not found; pageLayoutId = '{}'", pageLayoutId);
			}
		} else {
			log.warn("invalid pageLayoutId '{}'", pageLayoutId);
		}
	}

	@ModelAttribute("pageLayouts")
	public List<PageLayout> getPageLayouts() {
		return pageLayoutRepository.getPageLayouts();
	}

	private String getPageLayoutId(PortletRequest request) {
		return getLayout(request).getPageLayoutId();
	}

	private Layout getLayout(PortletRequest request) {
		return RequestUtil.getPageDisplay(request).getLayout();
	}
}
