package ee.midaiganes.portlet.impl;

import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.portal.portletinstance.PortletNamespace;
import ee.midaiganes.portlet.PortletLifecycle;
import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.util.PortletConstant;
import ee.midaiganes.util.StringPool;

public class ResourceURLImpl extends BaseURLImpl implements ResourceURL {

    public ResourceURLImpl(PortletRequestImpl request, PortletNamespace namespace, PortletLifecycle lifecycle) {
        this(request.getHttpServletRequest(), request.getHttpServletResponse(), namespace.getWindowID(), lifecycle, request.getPortletNamespace().getPortletName());
    }

    private ResourceURLImpl(HttpServletRequest request, HttpServletResponse response, String windowID, PortletLifecycle lifecycle, PortletName portletName) {
        super(request, response);
        setParameter(PortletConstant.PORTLET_URL_PORLTET_WINDOWID, windowID);
        setParameter(PortletConstant.PORTLET_URL_PORTLET_LIFECYCLE, lifecycle.toString());
        if (StringPool.DEFAULT_PORTLET_WINDOWID.equals(windowID)) {
            setParameter(PortletConstant.PORTLET_URL_PORTLET_NAME, portletName.getFullName());
        }
    }

    @Override
    public void setResourceID(String resourceID) {
        // TODO
    }

    @Override
    public String getCacheability() {
        // TODO
        return null;
    }

    @Override
    public void setCacheability(String cacheLevel) {
        // TODO
    }
}
