package ee.midaiganes.portlets.webcontent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import ee.midaiganes.portal.permission.PermissionService;
import ee.midaiganes.portal.portletinstance.PortletInstance;
import ee.midaiganes.portal.portletinstance.PortletInstanceRepository;
import ee.midaiganes.portal.portletinstance.PortletNamespace;
import ee.midaiganes.portletsservices.webcontent.WebContentRepository;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

@Singleton
public class WebContentController {
    private final WebContentRepository webContentRepository;
    private final PermissionService permissionService;
    private final PortletInstanceRepository portletInstanceRepository;

    @Inject
    public WebContentController(WebContentRepository webContentRepository, PermissionService permissionService, PortletInstanceRepository portletInstanceRepository) {
        this.webContentRepository = webContentRepository;
        this.permissionService = permissionService;
        this.portletInstanceRepository = portletInstanceRepository;
    }

    public String view(RenderRequest request, RenderResponse response) {
        String id = request.getPreferences().getValue(StringPool.ID, StringPool.EMPTY);
        if (StringUtil.isNumber(id)) {
            request.setAttribute("webContent", webContentRepository.getWebContent(Long.parseLong(id)));
        }
        request.setAttribute("hasUserEditPermission", Boolean.valueOf(hasUserEditPermission(request, response)));
        return "web-content/view";
    }

    private boolean hasUserEditPermission(PortletRequest request, PortletResponse response) {
        long userId = RequestUtil.getPageDisplay(request).getUser().getId();
        PortletNamespace portletNamespace = new PortletNamespace(response.getNamespace());
        PortletInstance portletInstance = portletInstanceRepository.getPortletInstance(portletNamespace.getPortletName(), portletNamespace.getWindowID());
        try {
            return permissionService.hasUserPermission(userId, portletInstance, "EDIT");
        } catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
