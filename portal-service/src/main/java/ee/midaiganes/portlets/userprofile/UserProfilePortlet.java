package ee.midaiganes.portlets.userprofile;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import ee.midaiganes.model.User;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.StringUtil;

public class UserProfilePortlet extends BasePortlet {

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.include(getViewAndSetRequestAttributes(request), request, response);
	}

	private String getViewAndSetRequestAttributes(RenderRequest request) {
		// TODO
		UserProfile userProfile = getAndSetRequestUserProfile(request);
		if (userProfile == null) {
			// TODO invalid user
			return "user-profile/invalid-user";
		}
		return userProfile.isCurrentUser() ? "user-profile/current-user" : "user-profile/user";
	}

	private UserProfile getAndSetRequestUserProfile(PortletRequest request) {
		UserProfile userProfile = getUserProfile(request);
		if (userProfile != null) {
			request.setAttribute("userProfile", userProfile);
		}
		return userProfile;
	}

	private UserProfile getUserProfile(PortletRequest request) {
		long userId = SessionUtil.getUserId(request);
		User requestedUser = getRequestedUser(request, userId);
		if (requestedUser != null) {
			boolean viewCurrentUserProfile = requestedUser.getId() == userId;
			return new UserProfile(requestedUser, viewCurrentUserProfile);
		}
		return null;
	}

	private User getRequestedUser(PortletRequest request, long currentUserId) {
		String username = request.getParameter("username");
		if (StringUtil.isEmpty(username)) {
			return UserRepository.getInstance().getUser(currentUserId);
		}
		return UserRepository.getInstance().getUser(username);
	}
}
