package ee.midaiganes.portlets.permissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.Utils;
import ee.midaiganes.model.PortalResource;
import ee.midaiganes.portal.group.Group;
import ee.midaiganes.portal.group.GroupRepository;
import ee.midaiganes.portal.permission.PermissionService;
import ee.midaiganes.portal.permission.ResourceActionRepository;
import ee.midaiganes.portal.permission.ResourceRepository;
import ee.midaiganes.portal.portletinstance.PortletInstance;
import ee.midaiganes.portal.portletinstance.PortletInstanceRepository;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;
import ee.midaiganes.util.PropsValues;

public class PermissionsController extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(PermissionsController.class);

    private final ResourceRepository resourceRepository;
    private final PermissionService permissionService;
    private final ResourceActionRepository resourceActionRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PortletInstanceRepository portletInstanceRepository;

    public PermissionsController() {
        this.portletInstanceRepository = Utils.getInstance().getInstance(PortletInstanceRepository.class);
        this.userRepository = Utils.getInstance().getInstance(UserRepository.class);
        this.groupRepository = Utils.getInstance().getInstance(GroupRepository.class);
        this.resourceActionRepository = Utils.getInstance().getInstance(ResourceActionRepository.class);
        this.permissionService = Utils.getInstance().getInstance(PermissionService.class);
        this.resourceRepository = Utils.getInstance().getInstance(ResourceRepository.class);
    }

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        String resource = request.getParameter("resource");
        String resourcePrimKey = request.getParameter("resource-prim-key");
        String resourceId = request.getParameter("resource-id");
        try {
            if (resourceId != null && resourcePrimKey != null) {
                super.include(view(resourceId, resourcePrimKey, request), request, response);
            } else if (resource != null && resourcePrimKey != null) {
                super.include(resourceView(resource, resourcePrimKey, request), request, response);
            } else {
                super.include(defaultView(request), request, response);
            }
        } catch (ResourceNotFoundException e) {
            throw new PortletException(e);
        }
    }

    private String defaultView(RenderRequest request) throws ResourceNotFoundException {
        long resourceId = resourceRepository.getResourceId(PortletInstance.getResourceName());
        List<PortletInstance> defaultPortletInstances = portletInstanceRepository.getDefaultPortletInstances();

        request.setAttribute("resourceId", Long.valueOf(resourceId));
        request.setAttribute("portletInstances", defaultPortletInstances);
        return "permissions/portlet-instances";
    }

    private String view(String resourceId, String resourcePrimKey, RenderRequest request) throws ResourceNotFoundException {
        return getView(request, Long.parseLong(resourceId), Long.parseLong(resourcePrimKey));
    }

    private String resourceView(@Nonnull String resource, String resourcePrimKey, RenderRequest request) throws ResourceNotFoundException {
        return getView(request, resourceRepository.getResourceId(resource), Long.parseLong(resourcePrimKey));
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException {
        String id = request.getParameter("id");
        String resourcePrimKey = request.getParameter("resource-prim-key");
        String resourceId = request.getParameter("resource-id");
        if (id != null && resourceId != null && resourcePrimKey != null) {
            try {
                updatePermissions(id, resourceId, resourcePrimKey, request, response);
            } catch (Exception e) {
                throw new PortletException(e);
            }
        } else {
            log.warn("Invalid request parameters.");
        }
    }

    private void updatePermissions(String id, String resourceId, String resourcePrimKey, ActionRequest request, ActionResponse response) throws Exception {
        long resource = Long.parseLong(resourceId);
        List<String> actions = getSortedResourceActions(resource);
        updatePermissions(Long.parseLong(id), resource, Long.parseLong(resourcePrimKey), getPermissionsData(request, actions.size()));
        Map<String, String[]> renderParams = new HashMap<>(2);
        renderParams.put("resource-id", new String[] { resourceId });
        renderParams.put("resource-prim-key", new String[] { resourcePrimKey });
        response.setRenderParameters(renderParams);
        request.setAttribute("success", Boolean.TRUE);
    }

    private PermissionsData getPermissionsData(ActionRequest request, int nrOfActions) {
        PermissionsData data = new PermissionsData();
        for (int i = 0;; i++) {
            String resourcePrimKey = request.getParameter("rows[" + i + "].resourcePrimKey");
            if (resourcePrimKey == null) {
                break;
            }
            PermissionsDataRow row = new PermissionsDataRow();
            row.setResourcePrimKey(Long.parseLong(resourcePrimKey));
            for (int j = 0; j < nrOfActions; j++) {
                row.getPermissions().add(Boolean.valueOf(request.getParameter("rows[" + i + "].permissions[" + j + "]")));
            }
            data.getRows().add(row);
        }
        log.debug("Data from request is '{}'", data);
        return data;
    }

    private void updatePermissions(long resourceId, long resource2Id, long resourcePrimKey, PermissionsData data) throws Exception {
        List<String> actions = getSortedResourceActions(resource2Id);
        String[] allActions = actions.toArray(new String[actions.size()]);
        boolean[] permissions = new boolean[actions.size()];
        for (PermissionsDataRow row : data.getRows()) {
            for (int i = 0; i < actions.size(); i++) {
                permissions[i] = row.getPermissions().get(i).booleanValue();
            }
            permissionService.setPermissions(resourceId, row.getResourcePrimKey(), resource2Id, resourcePrimKey, allActions, permissions);
        }
    }

    private String getView(RenderRequest request, long resourceId, long resourcePrimKey) throws ResourceNotFoundException {
        List<? extends PortalResource> resources = getResources();
        List<String> actions = getSortedResourceActions(resourceId);
        PermissionsData permissionsData = new PermissionsData();
        for (PortalResource resource : resources) {
            List<Boolean> permissions = new ArrayList<>(actions.size());
            PermissionsDataRow row = new PermissionsDataRow();
            row.setResourcePrimKey(resource.getId());
            setRowResourceText(resource, row);
            row.setPermissions(permissions);
            permissionsData.getRows().add(row);

            for (String action : actions) {
                try {
                    permissions.add(Boolean.valueOf(permissionService.hasPermission(resource, resourceId, resourcePrimKey, action)));
                } catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        request.setAttribute("id", Long.valueOf(resourceRepository.getResourceId(PropsValues.PERMISSIONS_RESOURCE_NAME)));
        request.setAttribute("actionsList", actions);
        request.setAttribute("permissionsData", permissionsData);
        request.setAttribute("resourceId", Long.valueOf(resourceId));
        request.setAttribute("resourcePrimKey", Long.valueOf(resourcePrimKey));
        return "permissions/view";
    }

    private void setRowResourceText(PortalResource resource, PermissionsDataRow row) {
        if (resource instanceof Group) {
            row.setResourceText(((Group) resource).getName());
        } else if (resource instanceof User) {
            row.setResourceText(((User) resource).getUsername());
        }
    }

    private List<? extends PortalResource> getResources() {
        if (PropsValues.PERMISSIONS_RESOURCE_NAME.equals(Group.class.getName())) {
            return groupRepository.getGroups();
        } else if (PropsValues.PERMISSIONS_RESOURCE_NAME.equals(User.class.getName())) {
            return userRepository.getUsers(0, userRepository.getUsersCount());
        }
        throw new IllegalStateException();
    }

    private List<String> getSortedResourceActions(long resourceId) {
        List<String> actions = new ArrayList<>(resourceActionRepository.getResourceActions(resourceId));
        Collections.sort(actions);
        return actions;
    }
}
