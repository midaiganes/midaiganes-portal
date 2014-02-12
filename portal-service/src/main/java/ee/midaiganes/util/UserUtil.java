package ee.midaiganes.util;

import javax.portlet.PortletRequest;

import ee.midaiganes.portal.user.User;

public class UserUtil {
	public static boolean isLoggedIn(PortletRequest request) {
		return SessionUtil.getUserId(request) != User.DEFAULT_USER_ID;
	}
}
