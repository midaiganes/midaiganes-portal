package ee.midaiganes.util;

import javax.portlet.PortletRequest;

import ee.midaiganes.portal.user.User;

public class UserUtil {
    public static boolean isLoggedIn(PortletRequest request) {
        return !User.isDefaultUserId(SessionUtil.getUserId(request));
    }
}
