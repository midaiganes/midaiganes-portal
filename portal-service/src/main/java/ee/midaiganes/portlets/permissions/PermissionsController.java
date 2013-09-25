package ee.midaiganes.portlets.permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.BeanUtil;
import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.Group;
import ee.midaiganes.model.PortalResource;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.User;
import ee.midaiganes.services.GroupRepository;
import ee.midaiganes.services.PermissionRepository;
import ee.midaiganes.services.PortletInstanceRepository;
import ee.midaiganes.services.ResourceActionRepository;
import ee.midaiganes.services.ResourceRepository;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;
import ee.midaiganes.util.PropsValues;

@Controller(value = "permissionsController")
@RequestMapping("VIEW")
public class PermissionsController {
	private static final Logger log = LoggerFactory.getLogger(PermissionsController.class);

	@Resource(name = PortalConfig.RESOURCE_REPOSITORY)
	private ResourceRepository resourceRepository;

	@Resource(name = PortalConfig.PERMISSION_REPOSITORY)
	private PermissionRepository permissionRepository;

	@Resource(name = PortalConfig.RESOURCE_ACTION_REPOSITORY)
	private ResourceActionRepository resourceActionRepository;

	@Resource(name = PortalConfig.GROUP_REPOSITORY)
	private GroupRepository groupRepository;

	@Resource(name = PortalConfig.USER_REPOSITORY)
	private UserRepository userRepository;

	private final PortletInstanceRepository portletInstanceRepository;

	public PermissionsController() {
		this.portletInstanceRepository = BeanUtil.getBean(PortletInstanceRepository.class);
	}

	@RenderMapping
	public String defaultView(RenderRequest request) throws ResourceNotFoundException {
		long resourceId = resourceRepository.getResourceId(PortletInstance.getResourceName());
		List<PortletInstance> defaultPortletInstances = portletInstanceRepository.getDefaultPortletInstances();

		request.setAttribute("resourceId", Long.valueOf(resourceId));
		request.setAttribute("portletInstances", defaultPortletInstances);
		return "permissions/portlet-instances";
	}

	@RenderMapping(params = { "resource-id", "resource-prim-key" })
	public String view(@RequestParam("resource-id") String resourceId, @RequestParam("resource-prim-key") String resourcePrimKey, RenderRequest request)
			throws ResourceNotFoundException {
		return getView(request, Long.parseLong(resourceId), Long.parseLong(resourcePrimKey));
	}

	@RenderMapping(params = { "resource", "resource-prim-key" })
	public String resourceView(@RequestParam("resource") String resource, @RequestParam("resource-prim-key") String resourcePrimKey, RenderRequest request)
			throws ResourceNotFoundException {
		return getView(request, resourceRepository.getResourceId(resource), Long.parseLong(resourcePrimKey));
	}

	@ActionMapping(params = { "id", "resource-id", "resource-prim-key" })
	public void updatePermissions(@RequestParam("id") String id, @RequestParam("resource-id") String resourceId,
			@RequestParam("resource-prim-key") String resourcePrimKey, ActionRequest request, ActionResponse response) throws Exception {
		long resource = Long.parseLong(resourceId);
		List<String> actions = getSortedResourceActions(resource);
		updatePermissions(Long.parseLong(id), resource, Long.parseLong(resourcePrimKey), getPermissionsData(request, actions.size()));
		Map<String, String[]> renderParams = new HashMap<>(2);
		renderParams.put("resource-id", new String[] { resourceId });
		renderParams.put("resource-prim-key", new String[] { resourcePrimKey });
		response.setRenderParameters(renderParams);
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
			permissionRepository.setPermissions(resourceId, row.getResourcePrimKey(), resource2Id, resourcePrimKey, allActions, permissions);
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
					permissions.add(Boolean.valueOf(permissionRepository.hasPermission(resource, resourceId, resourcePrimKey, action)));
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
