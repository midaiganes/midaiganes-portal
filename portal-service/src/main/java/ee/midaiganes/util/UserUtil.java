package ee.midaiganes.util;

import javax.portlet.PortletRequest;

import ee.midaiganes.model.User;

public class UserUtil {
	public static boolean isLoggedIn(PortletRequest request) {
		return SessionUtil.getUserId(request) != User.DEFAULT_USER_ID;
	}
}
