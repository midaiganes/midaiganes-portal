package ee.midaiganes.portlets.webcontent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.portletsservices.webcontent.WebContent;
import ee.midaiganes.portletsservices.webcontent.WebContentRepository;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

@Singleton
public class WebContentEditController {
    private static final Logger log = LoggerFactory.getLogger(WebContentEditController.class);
    private static final String ID = StringPool.ID;

    private final WebContentRepository webContentRepository;

    @Inject
    public WebContentEditController(WebContentRepository webContentRepository) {
        this.webContentRepository = webContentRepository;

    }

    public String editView(RenderRequest request) {
        request.setAttribute("webContents", webContentRepository.getWebContents(RequestUtil.getPageDisplay(request).getLayoutSet().getId()));
        return "web-content/edit";
    }

    public String addWebContentView() {
        return "web-content/add-web-content";
    }

    public String editWebContentView(RenderRequest request) {
        WebContent wc = getWebContent(request);
        if (wc != null) {
            request.setAttribute("webContent", wc);
            return "web-content/edit-web-content";
        }
        return editView(request);
    }

    public void addWebContent(ActionRequest request, ActionResponse response) {
        long id = webContentRepository.addWebContent(getLayoutSetId(request), request.getParameter("title"), request.getParameter("content"));
        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("action", new String[] { "edit-web-content" });
        parameters.put(ID, new String[] { Long.toString(id) });
        response.setRenderParameters(parameters);
    }

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

    public void setWebContent(ActionRequest request) throws PortletException, IOException {
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
