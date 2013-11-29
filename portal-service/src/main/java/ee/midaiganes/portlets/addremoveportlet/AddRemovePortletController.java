package ee.midaiganes.portlets.addremoveportlet;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.services.LayoutPortletRepository;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.util.LongUtil;
import ee.midaiganes.util.PortalURLUtil;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringUtil;

/**
 * TODO refactor bad parameter names
 */
@Controller(value = "addRemovePortletController")
@RequestMapping("view")
public class AddRemovePortletController {
    private static final Logger log = LoggerFactory.getLogger(AddRemovePortletController.class);

    @Resource(name = PortalConfig.PORTLET_REPOSITORY)
    private PortletRepository portletRepository;

    @Resource(name = PortalConfig.LAYOUT_PORTLET_REPOSITORY)
    private LayoutPortletRepository layoutPortletRepository;

    @RenderMapping
    public String portletListView() {
        return "add-remove-portlet/view";
    }

    @RenderMapping(params = { "action=add-portlet", "portletId", "portletBoxId" })
    public void addPortletView() {
        log.info("add portlet view");
    }

    @RenderMapping(params = { "action=move" })
    public void movePortletView() {
        log.debug("move portlet view");
    }

    @ActionMapping(params = { "action=add-portlet", "portletId", "portletBoxId" })
    public void addPortlet(ActionRequest request, @RequestParam("portletId") String portletId, @RequestParam("portletBoxId") String portletBoxId,
            @RequestParam("boxIndex") String boxIndex) {
        PortletName portletName = new PortletName(portletId);
        PageDisplay pageDisplay = RequestUtil.getPageDisplay(request);
        int rowId = StringUtil.isNumber(portletBoxId) ? Integer.parseInt(portletBoxId) : -1;
        if (rowId > 0) {
            if (portletRepository.getPortletNames().contains(portletName)) {
                int boxIndex_ = StringUtil.isNumber(boxIndex) ? Integer.parseInt(boxIndex) : -1;
                layoutPortletRepository.addLayoutPortlet(pageDisplay.getLayout().getId(), rowId, portletName, boxIndex_);
            } else {
                log.warn("invalid portletId '{}'", portletId);
            }
        } else {
            log.warn("invalid rowId '{}'", portletBoxId);
        }
    }

    @ActionMapping(params = { "action=move", "window-id", "portletBoxId", "boxIndex" })
    public void movePortlet(ActionRequest request, @RequestParam("window-id") String windowID, @RequestParam("portletBoxId") String portletBoxId,
            @RequestParam("boxIndex") String boxIndex) {
        if (LongUtil.isNonNegativeLong(portletBoxId) && LongUtil.isNonNegativeLong(boxIndex)) {
            long boxId = Long.parseLong(portletBoxId);
            long boxIdx = Long.parseLong(boxIndex);
            long layoutId = RequestUtil.getPageDisplay(request).getLayout().getId();
            layoutPortletRepository.moveLayoutPortlet(windowID, layoutId, boxId, boxIdx);
            log.info("Portlet with windowId '" + windowID + "' on layout '" + layoutId + "' moved in box '" + boxId + "' to '" + boxIdx + "'");
        } else {
            log.warn("Invalid boxId '{}' or boxIndex '{}'", portletBoxId, boxIndex);
        }
    }

    @ActionMapping(params = { "action=remove-portlet", "window-id" })
    public void removePortletAction(@RequestParam("window-id") String windowID, ActionRequest request, ActionResponse response) throws IOException {
        layoutPortletRepository.deleteLayoutPortlet(windowID);
        response.sendRedirect(PortalURLUtil.getFullURLByFriendlyURL(RequestUtil.getPageDisplay(request).getLayout().getFriendlyUrl()));
    }

    @ModelAttribute("portletNames")
    public List<PortletName> getPortletNames() {
        return portletRepository.getPortletNames();
    }
}
