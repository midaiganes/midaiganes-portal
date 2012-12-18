package ee.midaiganes.portlets.webcontent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.portletsservices.webcontent.Structure;
import ee.midaiganes.portletsservices.webcontent.StructureField;
import ee.midaiganes.portletsservices.webcontent.Template;
import ee.midaiganes.portletsservices.webcontent.WebContentRepository;
import ee.midaiganes.util.StringUtil;

@Controller(value = "webContentEditController")
@RequestMapping("EDIT")
public class WebContentEditController {
	private static final Logger log = LoggerFactory.getLogger(WebContentEditController.class);

	@Resource
	private WebContentRepository webContentRepository;

	@RenderMapping
	public String edit(RenderRequest request) {
		return "web-content/edit";
	}

	@RenderMapping(params = { "action=add-web-content" })
	public String addWebContent(Model model) {
		model.addAttribute("templates", webContentRepository.getTemplates());
		return "web-content/add-web-content-choose-template";
	}

	@RenderMapping(params = { "action=add-web-content-with-template", "templateId" })
	public String addWebContentWithTemplate(Model model, @RequestParam("templateId") String templateId) {
		if (StringUtil.isNumber(templateId)) {
			Template template = webContentRepository.getTemplate(Long.parseLong(templateId));
			if (template != null) {
				Structure structure = webContentRepository.getStructureWithFields(template.getStructureId());
				model.addAttribute("structure", structure);
				model.addAttribute("templateId", templateId);
				return "web-content/add-web-content";
			}
		}
		return addWebContent(model);
	}

	@ActionMapping(params = { "action=add-web-content", "templateId", "title" })
	public void addWebContent(ActionRequest request, @RequestParam("templateId") String templateId, @RequestParam("title") String title) {
		if (StringUtil.isNumber(templateId)) {
			Template template = webContentRepository.getTemplate(Long.parseLong(templateId));
			if (template != null) {
				Structure structure = webContentRepository.getStructureWithFields(template.getStructureId());
				List<String> languageIds = new ArrayList<>();
				List<Long> structureFieldIds = new ArrayList<>();
				List<String> fieldValues = new ArrayList<>();
				for (StructureField field : structure.getStructureFields()) {
					String fieldValue = request.getParameter("field_" + field.getId());
					fieldValues.add(fieldValue);
					structureFieldIds.add(field.getId());
					languageIds.add("et_EE");
				}
				webContentRepository.addWebContent("et_EE", title, Long.parseLong(templateId), languageIds, structureFieldIds, fieldValues);
			}
		}
	}

	@RenderMapping(params = { "action=view-structures" })
	public String viewStructures(Model model) {
		model.addAttribute("structures", webContentRepository.getStructuresWithFields());
		return "web-content/view-structures";
	}

	@RenderMapping(params = { "action=view-structure", "id" })
	public String viewStructure(Model model, @RequestParam("id") String id) {
		if (StringUtil.isNumber(id)) {
			model.addAttribute("structure", webContentRepository.getStructureWithFields(Long.parseLong(id)));
			return "web-content/view-structure";
		}
		return viewStructures(model);
	}

	@RenderMapping(params = { "action=add-structure" })
	public String addStructureView() {
		return "web-content/add-structure";
	}

	@ActionMapping(params = { "action=add-structure", "name" })
	public void addStructureAction(ActionResponse response, @RequestParam("name") String name) {
		long id = webContentRepository.addStructure(name);
		Map<String, String[]> renderParameters = new HashMap<>();
		renderParameters.put("action", new String[] { "view-structure" });
		renderParameters.put("id", new String[] { Long.toString(id) });
		response.setRenderParameters(renderParameters);
	}

	@ActionMapping(params = { "action=add-structure-field", "name", "fieldType" })
	public void addStructureFieldAction(ActionResponse response, @RequestParam("name") String name, @RequestParam("fieldType") String fieldType,
			@RequestParam("structureId") String structureId) {
		if (StringUtil.isNumber(structureId)) {
			long longStructureId = Long.parseLong(structureId);
			if (webContentRepository.isValidStructureFieldType(fieldType)) {
				if (webContentRepository.getStructureWithFields(longStructureId) != null) {
					webContentRepository.addStructureField(name, fieldType, longStructureId);
					Map<String, String[]> renderParameters = new HashMap<>();
					renderParameters.put("action", new String[] { "view-structure" });
					renderParameters.put("id", new String[] { structureId });
					response.setRenderParameters(renderParameters);
					return;
				} else {
					log.warn("no structure with id '{}'", structureId);
				}
			} else {
				log.warn("invalid field type '{}'", fieldType);
			}
		} else {
			log.warn("structureId is not number '{}'", structureId);
		}
		response.setRenderParameters(new HashMap<String, String[]>());
	}

	@RenderMapping(params = { "action=add-template" })
	public String addTemplateView(Model model) {
		model.addAttribute("structures", webContentRepository.getStructuresWithFields());
		return "web-content/add-template";
	}

	@ActionMapping(params = { "action=add-template", "name", "structureId", "templateContent" })
	public void addTemplateAction(ActionResponse response, @RequestParam("name") String name, @RequestParam("structureId") String structureId,
			@RequestParam("templateContent") String templateContent) {
		if (StringUtil.isNumber(structureId)) {
			webContentRepository.addTemplate(name, Long.parseLong(structureId), templateContent);
		}
		response.setRenderParameters(new HashMap<String, String[]>());
	}

	@RenderMapping(params = { "action=web-contents-list" })
	public String webContentsListView(Model model) {
		model.addAttribute("webContents", webContentRepository.getWebContents());
		return "web-content/web-contents-list";
	}

	@ActionMapping(params = { "action=set-web-content", "id" })
	public void setWebContent(ActionRequest request, @RequestParam("id") String id) throws ReadOnlyException, ValidatorException, IOException {
		if (StringUtil.isNumber(id)) {
			PortletPreferences preferences = request.getPreferences();
			preferences.setValue(WebContentController.WEB_CONTENT_ID, id);
			preferences.store();
		}
	}
}
