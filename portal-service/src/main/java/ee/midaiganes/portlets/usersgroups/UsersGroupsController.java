package ee.midaiganes.portlets.usersgroups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.Group;
import ee.midaiganes.model.User;
import ee.midaiganes.services.GroupRepository;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.util.PortalURLUtil;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringUtil;

@Controller("usersGroupsController")
@RequestMapping("VIEW")
public class UsersGroupsController {

	@Resource(name = PortalConfig.GROUP_REPOSITORY)
	private GroupRepository groupRepository;

	@Resource(name = PortalConfig.USER_REPOSITORY)
	private UserRepository userRepository;

	@RenderMapping
	public String viewUsers(RenderRequest request) {
		List<User> users = userRepository.getUsers(0, userRepository.getUsersCount());
		List<Group> groups = groupRepository.getGroups();
		List<UsersListData> usersList = new ArrayList<>(users.size());
		for (User user : users) {
			long[] userGroupIds = groupRepository.getUserGroupIds(user.getId());

			List<Group> userGroups = new ArrayList<>();
			List<Group> notUserGroups = new ArrayList<>();
			for (Group g : groups) {
				if (g.isUserGroup()) {
					boolean added = false;
					for (long userGroupId : userGroupIds) {
						if (g.getId() == userGroupId) {
							userGroups.add(g);
							added = true;
							break;
						}
					}
					if (!added) {
						notUserGroups.add(g);
					}
				}
			}
			usersList.add(new UsersListData(user, userGroups, notUserGroups));
		}
		request.setAttribute("users", usersList);
		return "users-groups/view-users";
	}

	@RenderMapping(params = { "action=view-groups" })
	public String viewGroups(RenderRequest request) {
		List<Group> groups = groupRepository.getGroups();
		request.setAttribute("groups", groups);
		return "users-groups/view-groups";
	}

	@ActionMapping(params = { "action=add-user-group", "user-id", "group-id" })
	public void addUserGroup(@RequestParam("user-id") String userId, @RequestParam("group-id") String groupId, ActionRequest request, ActionResponse response)
			throws IOException {
		groupRepository.addUserGroup(Long.parseLong(userId), Long.parseLong(groupId));
		sendRedirect(request, response);
	}

	@ActionMapping(params = { "action=remove-user-group", "user-id", "group-id" })
	public void removeUserGroup(@RequestParam("user-id") String userId, @RequestParam("group-id") String groupId, ActionRequest request, ActionResponse response)
			throws IOException {
		groupRepository.removeUserGroup(Long.parseLong(userId), Long.parseLong(groupId));
		sendRedirect(request, response);
	}

	@ActionMapping(params = { "action=add-group", "groupName", "userGroup" })
	public void addGroup(@RequestParam("groupName") String groupName, @RequestParam("userGroup") String userGroup, ActionRequest request,
			ActionResponse response) throws IOException {
		groupRepository.addGroup(groupName, Boolean.parseBoolean(userGroup));
		sendRedirect(request, response);
	}

	@ActionMapping(params = { "action=delete-group", "group-id" })
	public void deleteGroup(@RequestParam("group-id") String groupId, ActionRequest request, ActionResponse response) throws IOException {
		if (StringUtil.isNumber(groupId)) {
			groupRepository.deleteGroup(Long.parseLong(groupId, 10));
		}
		sendRedirect(request, response);
	}

	private void sendRedirect(ActionRequest request, ActionResponse response) throws IOException {
		response.sendRedirect(PortalURLUtil.getFullURLByFriendlyURL(RequestUtil.getPageDisplay(request).getLayout().getFriendlyUrl()));
	}
}
