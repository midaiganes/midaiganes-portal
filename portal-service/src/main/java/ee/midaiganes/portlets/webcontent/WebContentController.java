package ee.midaiganes.portlets.webcontent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.portlet.RenderRequest;

import ee.midaiganes.portletsservices.webcontent.WebContentRepository;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

@Singleton
public class WebContentController {
    private final WebContentRepository webContentRepository;

    @Inject
    public WebContentController(WebContentRepository webContentRepository) {
        this.webContentRepository = webContentRepository;
    }

    public String view(RenderRequest request) {
        String id = request.getPreferences().getValue(StringPool.ID, StringPool.EMPTY);
        if (StringUtil.isNumber(id)) {
            request.setAttribute("webContent", webContentRepository.getWebContent(Long.parseLong(id)));
        }
        return "web-content/view";
    }
}
