package ee.midaiganes.portlets.pagelayout;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.Utils;
import ee.midaiganes.model.ContextAndName;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layout.LayoutRepository;
import ee.midaiganes.portal.pagelayout.PageLayout;
import ee.midaiganes.portal.pagelayout.PageLayoutRepository;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.util.PortalURLUtil;
import ee.midaiganes.util.RequestUtil;

public class PageLayoutController extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(PageLayoutController.class);

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String pageLayoutId = request.getParameter("pageLayoutId");
        if (pageLayoutId != null) {
            setPageLayout(request, response, pageLayoutId);
        }
    }

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        PageLayout pageLayout = Utils.getInstance().getInstance(PageLayoutRepository.class).getPageLayout(getPageLayoutId(request));
        if (pageLayout != null) {
            request.setAttribute("pageLayoutName", pageLayout.getPageLayoutName());
        }
        request.setAttribute("pageLayouts", Utils.getInstance().getInstance(PageLayoutRepository.class).getPageLayouts());
        super.include("pagelayout/view", request, response);
    }

    private void setPageLayout(ActionRequest request, ActionResponse response, String pageLayoutId) throws IOException {
        if (ContextAndName.isValidFullName(pageLayoutId)) {
            PageLayout pageLayout = Utils.getInstance().getInstance(PageLayoutRepository.class).getPageLayout(pageLayoutId);
            if (pageLayout != null) {
                Utils.getInstance().getInstance(LayoutRepository.class).updatePageLayout(getLayout(request).getId(), pageLayout.getPageLayoutName());
                response.sendRedirect(PortalURLUtil.getFullURLByFriendlyURL(RequestUtil.getPageDisplay(request).getLayout().getFriendlyUrl()));
            } else {
                log.warn("page layout not found; pageLayoutId = '{}'", pageLayoutId);
            }
        } else {
            log.warn("invalid pageLayoutId '{}'", pageLayoutId);
        }
    }

    private String getPageLayoutId(PortletRequest request) {
        return getLayout(request).getPageLayoutId();
    }

    private Layout getLayout(PortletRequest request) {
        return RequestUtil.getPageDisplay(request).getLayout();
    }
}
