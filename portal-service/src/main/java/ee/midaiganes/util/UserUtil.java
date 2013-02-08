package ee.midaiganes.util;

import javax.portlet.PortletRequest;

import ee.midaiganes.model.DefaultUser;

public class UserUtil {
	public static boolean isLoggedIn(PortletRequest request) {
		return SessionUtil.getUserId(request) != DefaultUser.DEFAULT_USER_ID;
	}
}
