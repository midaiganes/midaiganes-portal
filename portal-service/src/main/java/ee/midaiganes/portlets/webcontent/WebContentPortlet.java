package ee.midaiganes.portlets.webcontent;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.portlets.BasePortlet;

@Singleton
public class WebContentPortlet extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(WebContentPortlet.class);
    private final WebContentController view;
    private final WebContentEditController edit;

    @Inject
    public WebContentPortlet(WebContentController view, WebContentEditController edit) {
        this.view = view;
        this.edit = edit;
    }

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletMode mode = request.getPortletMode();
        if (PortletMode.VIEW.equals(mode)) {
            super.include(view.view(request, response), request, response);
        } else if (PortletMode.EDIT.equals(mode)) {
            String action = request.getParameter("action");
            if ("add-web-content".equals(action)) {
                super.include(edit.addWebContentView(), request, response);
            } else if ("edit-web-content".equals(action) && request.getParameter("id") != null) {
                super.include(edit.editWebContentView(request), request, response);
            } else {
                super.include(edit.editView(request), request, response);
            }
        } else {
            log.debug("Invalid request for web content portlet.");
        }
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String action = request.getParameter("action");
        if (PortletMode.EDIT.equals(request.getPortletMode())) {
            if ("set-web-content".equals(action) && request.getParameter("id") != null) {
                edit.setWebContent(request);
            } else if ("edit-web-content".equals(action) && request.getParameter("id") != null && request.getParameter("title") != null && request.getParameter("content") != null) {
                edit.editWebContent(request, response);
            } else if ("add-web-content".equals(action) && request.getParameter("title") != null && request.getParameter("content") != null) {
                edit.addWebContent(request, response);
            }
        }
    }
}
