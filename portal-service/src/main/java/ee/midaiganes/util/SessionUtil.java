package ee.midaiganes.util;

import java.io.Serializable;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ee.midaiganes.model.DefaultUser;

public class SessionUtil {
	private static final String USER_ID = "USERID";

	public static long getUserId(HttpServletRequest request) {
		Long userid = getAttribute(request.getSession(false), USER_ID);
		return userid != null ? userid.longValue() : DefaultUser.DEFAULT_USER_ID;
	}

	public static void setUserId(PortletRequest request, Long userId) {
		setUserId(RequestUtil.getHttpServletRequest(request), userId);
	}

	public static void setUserId(HttpServletRequest request, Long userId) {
		setAttribute(request.getSession(), USER_ID, userId);
	}

	public static void setAttribute(HttpSession session, String name, Serializable value) {
		if (session != null) {
			if (value != null) {
				session.setAttribute(name, value);
			} else {
				session.removeAttribute(name);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T getAttribute(HttpSession session, String name) {
		if (session == null) {
			return null;
		}
		return (T) session.getAttribute(name);
	}
}
