package ee.midaiganes.portlets.webcontent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.portletsservices.webcontent.WebContent;
import ee.midaiganes.portletsservices.webcontent.WebContentRepository;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

@Controller(value = "webContentEditController")
@RequestMapping("EDIT")
public class WebContentEditController {
	private static final Logger log = LoggerFactory.getLogger(WebContentEditController.class);
	private static final String ID = StringPool.ID;

	@Resource(name = "webContentRepository")
	private WebContentRepository webContentRepository;

	@RenderMapping
	public String editView(RenderRequest request) {
		request.setAttribute("webContents", webContentRepository.getWebContents(RequestUtil.getPageDisplay(request).getLayoutSet().getId()));
		return "web-content/edit";
	}

	@RenderMapping(params = { "action=add-web-content" })
	public String addWebContentView() {
		return "web-content/add-web-content";
	}

	@RenderMapping(params = { "action=edit-web-content", ID })
	public String editWebContentView(RenderRequest request) {
		WebContent wc = getWebContent(request);
		if (wc != null) {
			request.setAttribute("webContent", wc);
			return "web-content/edit-web-content";
		}
		return editView(request);
	}

	@ActionMapping(params = { "action=add-web-content", "title", "content" })
	public void addWebContent(ActionRequest request, ActionResponse response) {
		long id = webContentRepository.addWebContent(getLayoutSetId(request), request.getParameter("title"), request.getParameter("content"));
		Map<String, String[]> parameters = new HashMap<>();
		parameters.put("action", new String[] { "edit-web-content" });
		parameters.put(ID, new String[] { Long.toString(id) });
		response.setRenderParameters(parameters);
	}

	@ActionMapping(params = { "action=edit-web-content", "id", "title", "content" })
	public void editWebContent(ActionRequest request, ActionResponse response) {
		WebContent wc = getWebContent(request);
		if (wc != null) {
			webContentRepository.updateWebContent(wc.getId(), request.getParameter("title"), request.getParameter("content"));
		} else {
			log.warn("Web content not found.");
		}
		String redirectUrl = request.getParameter("redirect");
		if (!StringUtil.isEmpty(redirectUrl)) {
			try {
				response.sendRedirect(redirectUrl);
				return;
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}
		}
		Map<String, String[]> parameters = new HashMap<>();
		parameters.put("action", new String[] { "edit-web-content" });
		parameters.put(ID, new String[] { request.getParameter(ID) });
		response.setRenderParameters(parameters);
	}

	@ActionMapping(params = { "action=set-web-content", ID })
	public void setWebContent(ActionRequest request) throws Exception {
		String id = request.getParameter(ID);
		if (getWebContent(request) != null) {
			PortletPreferences preferences = request.getPreferences();
			preferences.setValue(ID, id);
			preferences.store();
		}
	}

	private WebContent getWebContent(PortletRequest request) {
		String id = request.getParameter(ID);
		if (StringUtil.isNumber(id)) {
			return webContentRepository.getWebContent(Long.parseLong(id));
		}
		return null;
	}

	private static long getLayoutSetId(PortletRequest request) {
		return RequestUtil.getPageDisplay(request).getLayoutSet().getId();
	}
}
