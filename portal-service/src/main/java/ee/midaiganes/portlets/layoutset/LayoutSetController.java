package ee.midaiganes.portlets.layoutset;

import javax.annotation.Resource;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.model.LayoutSet;
import ee.midaiganes.services.LayoutSetRepository;
import ee.midaiganes.util.StringUtil;

@Controller("layoutSetController")
@RequestMapping("VIEW")
public class LayoutSetController {
	private static final Logger log = LoggerFactory.getLogger(LayoutSetController.class);

	@Resource(name = RootApplicationContext.LAYOUT_SET_REPOSITORY)
	private LayoutSetRepository layoutSetRepository;

	@RenderMapping
	public String addLayoutSetView(RenderRequest request) {
		request.setAttribute("layoutSets", layoutSetRepository.getLayoutSets());
		return "layout-set/add-layout-set";
	}

	@RenderMapping(params = { "action=edit-layout-set", "id" })
	public String editLayoutSetView(@RequestParam("id") String id, RenderRequest request) {
		LayoutSet layoutSet;
		try {
			layoutSet = layoutSetRepository.getLayoutSet(Long.parseLong(id));
		} catch (NumberFormatException e) {
			log.debug(e.getMessage(), e);
			return addLayoutSetView(request);
		}
		LayoutSetModel layoutSetModel = new LayoutSetModel();
		layoutSetModel.setHost(layoutSet.getVirtualHost());
		layoutSetModel.setId(Long.toString(layoutSet.getId()));
		request.setAttribute("editLayoutSetModel", layoutSetModel);
		return "layout-set/edit-layout-set";
	}

	@ActionMapping(params = { "action=add-layout-set" })
	public void addLayoutSetAction(@ModelAttribute("addLayoutSetModel") LayoutSetModel layoutSetModel) {
		if (!StringUtil.isEmpty(layoutSetModel.getHost())) {
			layoutSetRepository.addLayoutSet(layoutSetModel.getHost(), null);
		}
	}

	@ActionMapping(params = { "action=edit-layout-set", "id" })
	public void editLayoutSetAction(@ModelAttribute("addLayoutSetModel") LayoutSetModel layoutSetModel) {
		if (!StringUtil.isEmpty(layoutSetModel.getHost()) && StringUtil.isNumber(layoutSetModel.getId())) {
			layoutSetRepository.updateLayoutSet(Long.parseLong(layoutSetModel.getId()), layoutSetModel.getHost(), null);
		}
	}

	@ModelAttribute("addLayoutSetModel")
	public LayoutSetModel getAddLayoutSetModel() {
		return new LayoutSetModel();
	}
}
