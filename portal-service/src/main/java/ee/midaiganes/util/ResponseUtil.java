package ee.midaiganes.util;

import java.lang.reflect.Method;

import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.portlet.impl.PortletResponseImpl;

public class ResponseUtil {
    public static HttpServletResponse getHttpServletResponse(PortletResponse response) {
        try {
            Object resp = response;
            while (!(resp instanceof PortletResponseImpl)) {
                Method method = resp.getClass().getMethod("getResponse");
                resp = method.invoke(resp);
            }
            return ((PortletResponseImpl) resp).getHttpServletResponse();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Response is not correctly wrapped", e);
        }
    }
}
