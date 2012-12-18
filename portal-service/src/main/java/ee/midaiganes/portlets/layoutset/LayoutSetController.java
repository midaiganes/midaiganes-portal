package ee.midaiganes.portlets.layoutset;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.services.LayoutSetRepository;
import ee.midaiganes.util.StringUtil;

@Controller("layoutSetController")
@RequestMapping("VIEW")
public class LayoutSetController {

	@Resource(name = RootApplicationContext.LAYOUT_SET_REPOSITORY)
	private LayoutSetRepository layoutSetRepository;

	@RenderMapping
	public String addLayoutSetView(Model model) {
		model.addAttribute("layoutSets", layoutSetRepository.getLayoutSets());
		return "layout-set/add-layout-set";
	}

	@ActionMapping
	public void addLayoutSetAction(@ModelAttribute("addLayoutSetModel") LayoutSetModel layoutSetModel) {
		if (!StringUtil.isEmpty(layoutSetModel.getHost())) {
			layoutSetRepository.addLayoutSet(layoutSetModel.getHost(), null);
		}
	}

	@ModelAttribute("addLayoutSetModel")
	public LayoutSetModel getAddLayoutSetModel() {
		return new LayoutSetModel();
	}
}
