package ee.midaiganes.portlets.usersgroups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.model.Group;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.services.GroupRepository;
import ee.midaiganes.util.PortalURLUtil;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringUtil;

public class UsersGroupsController extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(UsersGroupsController.class);

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public UsersGroupsController() {
        this.userRepository = BeanRepositoryUtil.getBean(UserRepository.class);
        this.groupRepository = BeanRepositoryUtil.getBean(GroupRepository.class);

    }

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        String action = request.getParameter("action");
        if ("view-groups".equals(action)) {
            this.viewGroups(request, response);
        } else {
            this.viewUsers(request, response);
        }
    }

    private void viewUsers(RenderRequest request, RenderResponse response) throws PortletException, IOException {
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
        super.include("users-groups/view-users", request, response);
    }

    private void viewGroups(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        List<Group> groups = groupRepository.getGroups();
        request.setAttribute("groups", groups);
        super.include("users-groups/view-groups", request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws IOException {
        String action = request.getParameter("action");
        String userId = request.getParameter("user-id");
        String groupId = request.getParameter("group-id");
        String groupName = request.getParameter("groupName");
        String userGroup = request.getParameter("userGroup");
        if ("add-user-group".equals(action) && userId != null && groupId != null) {
            addUserGroup(userId, groupId, request, response);
        } else if ("remove-user-group".equals(action) && userId != null && groupId != null) {
            removeUserGroup(userId, groupId, request, response);
        } else if ("delete-group".equals(action) && groupId != null) {
            deleteGroup(groupId, request, response);
        } else if ("add-group".equals(action) && groupName != null && userGroup != null) {
            addGroup(groupName, userGroup, request, response);
        } else {
            log.warn("Invalid request parameters");
        }
    }

    private void addUserGroup(String userId, String groupId, ActionRequest request, ActionResponse response) throws IOException {
        groupRepository.addUserGroup(Long.parseLong(userId), Long.parseLong(groupId));
        sendRedirect(request, response);
    }

    private void removeUserGroup(String userId, String groupId, ActionRequest request, ActionResponse response) throws IOException {
        groupRepository.removeUserGroup(Long.parseLong(userId), Long.parseLong(groupId));
        sendRedirect(request, response);
    }

    private void addGroup(String groupName, String userGroup, ActionRequest request, ActionResponse response) throws IOException {
        groupRepository.addGroup(groupName, Boolean.parseBoolean(userGroup));
        sendRedirect(request, response);
    }

    private void deleteGroup(String groupId, ActionRequest request, ActionResponse response) throws IOException {
        if (StringUtil.isNumber(groupId)) {
            groupRepository.deleteGroup(Long.parseLong(groupId, 10));
        }
        sendRedirect(request, response);
    }

    private void sendRedirect(ActionRequest request, ActionResponse response) throws IOException {
        response.sendRedirect(PortalURLUtil.getFullURLByFriendlyURL(RequestUtil.getPageDisplay(request).getLayout().getFriendlyUrl()));
    }
}
