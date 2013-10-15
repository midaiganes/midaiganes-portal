package ee.midaiganes.util;

import java.lang.reflect.Method;

import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.portlet.impl.PortletResponseImpl;

public class ResponseUtil {
	public static HttpServletResponse getHttpServletResponse(PortletResponse response) {
		try {
			while (!(response instanceof PortletResponseImpl)) {
				Method method = response.getClass().getMethod("getResponse");
				response = (PortletResponse) method.invoke(response);
			}
			return ((PortletResponseImpl) response).getHttpServletResponse();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException("Response is not correctly wrapped", e);
		}
	}
}
