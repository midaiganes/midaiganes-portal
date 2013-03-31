package ee.midaiganes.portlets.webcontent;

import javax.annotation.Resource;
import javax.portlet.RenderRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.portletsservices.webcontent.WebContentRepository;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

@Controller(value = "webContentController")
@RequestMapping("VIEW")
public class WebContentController {

	@Resource(name = "webContentRepository")
	private WebContentRepository webContentRepository;

	@RenderMapping
	public String view(RenderRequest request) {
		String id = request.getPreferences().getValue(StringPool.ID, StringPool.EMPTY);
		if (StringUtil.isNumber(id)) {
			request.setAttribute("webContent", webContentRepository.getWebContent(Long.parseLong(id)));
		}
		return "web-content/view";
	}
}
