package ee.midaiganes.portlets.addremoveportlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.Utils;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.portal.layoutportlet.LayoutPortletRepository;
import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.util.LongUtil;
import ee.midaiganes.util.PortalURLUtil;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringUtil;

/**
 * TODO refactor bad parameter names
 */
public class AddRemovePortletController extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(AddRemovePortletController.class);

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        if ("move".equals(request.getParameter("move"))) {
            log.debug("move portlet view");
        } else if ("add-portlet".equals(request.getParameter("action")) && request.getParameter("portletId") != null && request.getParameter("portletBoxId") != null) {
            log.info("add portlet view");
        } else {
            request.setAttribute("portletNames", Utils.getInstance().getInstance(PortletRepository.class).getPortletNames());
            super.include("add-remove-portlet/view", request, response);
        }
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String action = request.getParameter("action");
        String windowId = request.getParameter("window-id");
        if ("remove-portlet".equals(action) && windowId != null) {
            removePortletAction(windowId, request, response);
        } else {
            String portletId = request.getParameter("portletId");
            String portletBoxId = request.getParameter("portletBoxId");
            String boxIndex = request.getParameter("boxIndex");
            if ("add-portlet".equals(action) && portletId != null && portletBoxId != null) {
                addPortlet(request, portletId, portletBoxId, boxIndex);
            } else {
                if ("move".equals(action) && windowId != null && portletBoxId != null && boxIndex != null) {
                    movePortlet(request, windowId, portletBoxId, boxIndex);
                }
            }
        }
    }

    private void addPortlet(ActionRequest request, String portletId, String portletBoxId, String boxIndex) {
        PortletName portletName = new PortletName(portletId);
        PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
        int rowId = StringUtil.isNumber(portletBoxId) ? Integer.parseInt(portletBoxId) : -1;
        if (rowId > 0) {
            if (Utils.getInstance().getInstance(PortletRepository.class).getPortletNames().contains(portletName)) {
                int boxIndex_ = StringUtil.isNumber(boxIndex) ? Integer.parseInt(boxIndex) : -1;
                Utils.getInstance().getInstance(LayoutPortletRepository.class).addLayoutPortlet(pageDisplay.getLayout().getId(), rowId, portletName, boxIndex_);
            } else {
                log.warn("invalid portletId '{}'", portletId);
            }
        } else {
            log.warn("invalid rowId '{}'", portletBoxId);
        }
    }

    private void movePortlet(ActionRequest request, String windowID, String portletBoxId, String boxIndex) {
        if (LongUtil.isNonNegativeLong(portletBoxId) && LongUtil.isNonNegativeLong(boxIndex)) {
            long boxId = Long.parseLong(portletBoxId);
            long boxIdx = Long.parseLong(boxIndex);
            long layoutId = RequestUtil.getPageDisplay(request).getLayout().getId();
            Utils.getInstance().getInstance(LayoutPortletRepository.class).moveLayoutPortlet(windowID, layoutId, boxId, boxIdx);
            log.info("Portlet with windowId '" + windowID + "' on layout '" + layoutId + "' moved in box '" + boxId + "' to '" + boxIdx + "'");
        } else {
            log.warn("Invalid boxId '{}' or boxIndex '{}'", portletBoxId, boxIndex);
        }
    }

    private void removePortletAction(String windowID, ActionRequest request, ActionResponse response) throws IOException {
        Utils.getInstance().getInstance(LayoutPortletRepository.class).deleteLayoutPortlet(windowID);
        response.sendRedirect(PortalURLUtil.getFullURLByFriendlyURL(RequestUtil.getPageDisplay(request).getLayout().getFriendlyUrl()));
    }
}
