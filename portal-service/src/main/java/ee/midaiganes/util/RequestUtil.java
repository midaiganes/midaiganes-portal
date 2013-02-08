package ee.midaiganes.util;

import java.lang.reflect.Method;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.portlet.impl.PortletRequestImpl;

public class RequestUtil {
	private static final String PAGE_DISPLAY = "PAGE_DISPLAY";
	private static final String GET_REQUEST = "getRequest";

	public static PageDisplay getPageDisplay(HttpServletRequest request) {
		return (PageDisplay) request.getAttribute(PAGE_DISPLAY);
	}

	public static PageDisplay getPageDisplay(PortletRequest request) {
		return getPageDisplay(getHttpServletRequest(request));
	}

	public static String getFriendlyURL(String requestURI) {
		int length = PropsValues.PORTAL_CONTEXT.length();
		if (length > 0 && requestURI.startsWith(PropsValues.PORTAL_CONTEXT + StringPool.SLASH)) {
			return requestURI.substring(length);
		}
		return requestURI;
	}

	public static HttpServletRequest getHttpServletRequest(PortletRequest request) {
		try {
			while (!(request instanceof PortletRequestImpl)) {
				Method method = request.getClass().getMethod(GET_REQUEST);
				request = (PortletRequest) method.invoke(request);
			}
			return ((PortletRequestImpl) request).getHttpServletRequest();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException("Request is not correctly wrapped", e);
		}
	}

	public static void setPageDisplay(HttpServletRequest request, PageDisplay pageDisplay) {
		request.setAttribute(PAGE_DISPLAY, pageDisplay);
	}
}
