package ee.midaiganes.portlets.layout;

import javax.annotation.Resource;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.Layout;
import ee.midaiganes.model.LayoutTitle;
import ee.midaiganes.model.PageLayoutName;
import ee.midaiganes.services.LanguageRepository;
import ee.midaiganes.services.LayoutRepository;
import ee.midaiganes.services.PageLayoutRepository;
import ee.midaiganes.services.exceptions.IllegalFriendlyUrlException;
import ee.midaiganes.services.exceptions.IllegalLanguageIdException;
import ee.midaiganes.services.exceptions.IllegalPageLayoutException;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

@Controller("layoutsController")
@RequestMapping("VIEW")
public class LayoutsController {
	private static final Logger log = LoggerFactory.getLogger(LayoutsController.class);

	@Resource(name = PortalConfig.LAYOUT_REPOSITORY)
	private LayoutRepository layoutRepository;

	@Resource(name = PortalConfig.LANGUAGE_REPOSITORY)
	private LanguageRepository languageRepository;

	@Resource(name = PortalConfig.PAGE_LAYOUT_REPOSITORY)
	private PageLayoutRepository pageLayoutRepository;

	@RenderMapping
	public String addLayoutView(RenderRequest request) {
		request.setAttribute("layouts", LayoutItem.getLayoutItems(layoutRepository.getLayouts(RequestUtil.getPageDisplay(request).getLayoutSet().getId())));
		return "layouts/add-page";
	}

	@RenderMapping(params = { "action=edit-layout", "id" })
	public String editLayoutView(@RequestParam("id") String id, RenderRequest request) {
		if (StringUtil.isNumber(id)) {
			Layout layout = layoutRepository.getLayout(Long.parseLong(id));
			LayoutModel layoutModel = new LayoutModel();
			layoutModel.setDefaultLayoutTitleLanguageId(languageRepository.getLanguageId(layout.getDefaultLayoutTitleLanguageId()));
			Long parentId = layout.getParentId();
			layoutModel.setParentId(Long.toString(parentId == null ? 0 : parentId.longValue()));
			layoutModel.setUrl(layout.getFriendlyUrl());
			for (String languageId : languageRepository.getSupportedLanguageIds()) {
				layoutModel.getLayoutTitles().put(languageId, StringPool.EMPTY);
			}
			for (LayoutTitle lt : layout.getLayoutTitles()) {
				layoutModel.getLayoutTitles().put(languageRepository.getLanguageId(lt.getLanguageId()), lt.getTitle());
			}
			request.setAttribute("editLayoutModel", layoutModel);
			request.setAttribute("layout", layout);
			request.setAttribute("layouts", layoutRepository.getLayouts(RequestUtil.getPageDisplay(request).getLayoutSet().getId()));

			return "layouts/edit-layout";
		}
		return addLayoutView(request);
	}

	@ActionMapping
	public void addLayoutAction(@ModelAttribute("addLayoutModel") LayoutModel layout, PortletRequest request) throws IllegalFriendlyUrlException,
			IllegalLanguageIdException, IllegalPageLayoutException {
		if (layoutRepository.isFriendlyUrlValid(layout.getUrl())) {
			Long parentId = getParentId(layout.getParentId());
			long layoutSetId = RequestUtil.getPageDisplay(request).getLayoutSet().getId();
			PageLayoutName defaultPageLayoutName = pageLayoutRepository.getDefaultPageLayout().getPageLayoutName();
			// TODO
			long languageId = languageRepository.getId(languageRepository.getSupportedLanguageIds().get(0)).longValue();
			layoutRepository.addLayout(layoutSetId, layout.getUrl(), null, defaultPageLayoutName, parentId, languageId);
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

	@ActionMapping(params = { "action=edit-layout", "id" })
	public void editLayoutAction(@RequestParam("id") String id, @ModelAttribute("addLayoutModel") LayoutModel layoutModel) throws NumberFormatException,
			IllegalFriendlyUrlException, IllegalLanguageIdException, IllegalPageLayoutException {
		if (StringUtil.isNumber(id)) {
			Layout layout = layoutRepository.getLayout(Long.parseLong(id));
			if (layout != null) {
				updateLayout(layoutModel, layout);
			} else {
				log.warn("Invalid layout id '{}'", id);
			}
		}
	}

	@ActionMapping(params = { "action=move-up", "id" })
	public void moveUpAction(@RequestParam("id") String id) {
		if (StringUtil.isNumber(id)) {
			if (!layoutRepository.moveLayoutUp(Long.parseLong(id))) {
				log.warn("Layout not moved up: '{}'", id);
			}
		} else {
			log.warn("Can't move layout up: invalid id '{}'", id);
		}
	}

	@ActionMapping(params = { "action=move-down", "id" })
	public void moveDownAction(@RequestParam("id") String id) {
		if (StringUtil.isNumber(id)) {
			if (!layoutRepository.moveLayoutDown(Long.parseLong(id))) {
				log.warn("Layout not moved down: '{}'", id);
			}
		} else {
			log.warn("Can't move layout down: invalid id '{}'", id);
		}
	}

	private void updateLayout(LayoutModel layoutModel, Layout layout) throws IllegalFriendlyUrlException, IllegalLanguageIdException,
			IllegalPageLayoutException {
		PageLayoutName pageLayoutName = new PageLayoutName(layout.getPageLayoutId());
		Long parentId = StringUtil.isEmpty(layoutModel.getParentId()) ? null : Long.valueOf(layoutModel.getParentId());
		long defaultLayoutTitleLanguageId = languageRepository.getId(layoutModel.getDefaultLayoutTitleLanguageId()).longValue();
		layoutRepository.updateLayout(layoutModel.getUrl(), pageLayoutName, parentId, defaultLayoutTitleLanguageId, layout.getId());
		updateLayoutTitles(layoutModel, layout);
	}

	private void updateLayoutTitles(LayoutModel layoutModel, Layout layout) {
		for (String languageId : languageRepository.getSupportedLanguageIds()) {
			Long l = languageRepository.getId(languageId);
			String layoutTitle = layoutModel.getLayoutTitles().get(languageId);
			if (!StringUtil.isEmpty(layoutTitle)) {
				addOrUpdateLayoutTitle(layout, languageId, layoutTitle);
			} else if (layout.getLayoutTitle(l.longValue()) != null) {
				deleteLayoutTitle(layout.getId(), languageId);
			}
		}
	}

	private void deleteLayoutTitle(long layoutId, String languageId) {
		Long lid = languageRepository.getId(languageId);
		if (lid != null) {
			layoutRepository.deleteLayoutTitle(layoutId, lid.longValue());
		} else {
			log.warn("Can't delete LayoutTitle ({}). Invalid language id '{}'", Long.valueOf(layoutId), languageId);
		}
	}

	private void addOrUpdateLayoutTitle(Layout layout, String languageId, String layoutTitle) {
		Long lid = languageRepository.getId(languageId);
		if (lid != null) {
			addOrUpdateLayoutTitle(layout, layoutTitle, lid.longValue());
		} else {
			log.warn("Can't add/update LayoutTitle. Invalid language id '{}'", languageId);
		}
	}

	private void addOrUpdateLayoutTitle(Layout layout, String val, long lid) {
		if (layout.getLayoutTitle(lid) == null) {
			layoutRepository.addLayoutTitle(layout.getId(), lid, val);
		} else {
			layoutRepository.updateLayoutTitle(layout.getId(), lid, val);
		}
	}

	@ModelAttribute("addLayoutModel")
	public LayoutModel getAddLayoutModel() {
		return new LayoutModel();
	}

	private Long getParentId(String parentId) {
		if (StringUtil.isEmpty(parentId)) {
			return null;
		}
		return Long.valueOf(parentId);
	}
}
