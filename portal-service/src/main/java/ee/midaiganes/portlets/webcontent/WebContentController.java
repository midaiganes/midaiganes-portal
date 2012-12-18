package ee.midaiganes.portlets.webcontent;

import java.io.IOException;

import javax.annotation.Resource;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.portletsservices.webcontent.TemplateEngineService;
import ee.midaiganes.portletsservices.webcontent.WebContent;
import ee.midaiganes.portletsservices.webcontent.WebContentRepository;
import ee.midaiganes.util.StringUtil;

@Controller(value = "webContentController")
@RequestMapping("VIEW")
public class WebContentController {
	final static String WEB_CONTENT_ID = "web-content-id";

	@Resource
	private TemplateEngineService templateEngineService;
	@Resource
	private WebContentRepository webContentRepository;

	@RenderMapping
	public String view(RenderRequest request, RenderResponse response, Model model) throws IOException {
		String id = request.getPreferences().getValue(WEB_CONTENT_ID, null);
		if (StringUtil.isNumber(id)) {
			WebContent webContent = webContentRepository.getWebContent(Long.parseLong(id));
			if (webContent != null) {
				String content = templateEngineService.process(webContent);
				model.addAttribute("content", content);
				/*
				 * PrintWriter pw = response.getWriter();
				 * pw.print(content);
				 * pw.flush();
				 * pw.close();
				 */
			}
		}
		return "web-content/view";
	}
}
