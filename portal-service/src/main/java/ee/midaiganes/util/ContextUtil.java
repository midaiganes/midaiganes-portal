package ee.midaiganes.util;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ContextUtil {

	public static ServletContext getServletContext(HttpServletRequest request, String uriPath) {
		return request.getServletContext().getContext(uriPath);
	}

	public static ServletContext getPortalServletContext(HttpServletRequest request) {
		return getServletContext(request, PropsValues.PORTAL_CONTEXT);
	}

	public static RequestDispatcher getRequestDispatcher(HttpServletRequest request, String uriPath, String path) {
		return getRequestDispatcher(getServletContext(request, uriPath), path);
	}

	public static RequestDispatcher getRequestDispatcher(ServletContext context, String path) {
		return context.getRequestDispatcher(path);
	}
}
