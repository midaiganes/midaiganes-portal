package ee.midaiganes.portlet.impl;

import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ee.midaiganes.portal.portletinstance.PortletNamespace;
import ee.midaiganes.portlet.PortletAndConfiguration;
import ee.midaiganes.util.PortletConstant;

public class PortletRequestImpl implements PortletRequest {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final String lifecyclePhase;
    private final PortletNamespace namespace;
    private final PortletMode portletMode;
    private final WindowState windowState;
    private final PortletPreferences portletPreferences;
    private final List<WindowState> windowStates;
    private final List<PortletMode> portletModes;
    private final PortletAndConfiguration portletConfiguration;

    public PortletRequestImpl(HttpServletRequest request, HttpServletResponse response, String lifecyclePhase, PortletNamespace namespace, PortletMode portletMode,
            WindowState windowState, PortletPreferences portletPreferences, PortletAndConfiguration portletConfiguration) {
        this.request = request;
        this.response = response;
        this.lifecyclePhase = lifecyclePhase;
        this.namespace = namespace;
        this.portletMode = portletMode;
        this.windowState = windowState;
        this.portletPreferences = portletPreferences;
        this.portletConfiguration = portletConfiguration;
        this.windowStates = portletConfiguration.getSupportedWindowStates();
        this.portletModes = portletConfiguration.getSupportedPortletModes();
    }

    @Override
    public boolean isWindowStateAllowed(WindowState state) {
        return windowStates.contains(state);
    }

    @Override
    public boolean isPortletModeAllowed(PortletMode mode) {
        return portletModes.contains(mode);
    }

    @Override
    public PortletMode getPortletMode() {
        return portletMode;
    }

    @Override
    public WindowState getWindowState() {
        return windowState;
    }

    @Override
    public PortletPreferences getPreferences() {
        return portletPreferences;
    }

    @Override
    public PortletSession getPortletSession() {
        return getPortletSession(true);
    }

    @Override
    public PortletSession getPortletSession(boolean create) {
        HttpSession session = request.getSession(create);
        if (session == null) {
            return null;
        }
        return new PortletSessionImpl(session, this, portletConfiguration.getPortletConfig().getPortletContext());
    }

    @Override
    public String getProperty(String name) {
        return request.getHeader(name);
    }

    @Override
    public Enumeration<String> getProperties(String name) {
        return request.getHeaders(name);
    }

    @Override
    public Enumeration<String> getPropertyNames() {
        return request.getHeaderNames();
    }

    @Override
    public PortalContext getPortalContext() {
        // TODO
        return null;
    }

    @Override
    public String getAuthType() {
        return request.getAuthType();
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    @Override
    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    @Override
    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    @Override
    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    @Override
    public Object getAttribute(String name) {
        if (PortletRequest.LIFECYCLE_PHASE.equals(name)) {
            return lifecyclePhase;
        } else if (PortletConstant.JAVAX_PORTLET_SERVLETREQUEST.equals(name)) {
            return request;
        } else if (PortletConstant.JAVAX_PORTLET_SERVLETRESPONSE.equals(name)) {
            return response;
        }
        return request.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return request.getAttributeNames();
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return request.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    @Override
    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }

    @Override
    public String getResponseContentType() {
        // TODO
        return "text/html";
    }

    @Override
    public Enumeration<String> getResponseContentTypes() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    public Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return request.getLocales();
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
    public String getWindowID() {
        return namespace.getNamespace();
    }

    @Override
    public Cookie[] getCookies() {
        return request.getCookies();
    }

    @Override
    public Map<String, String[]> getPrivateParameterMap() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    public Map<String, String[]> getPublicParameterMap() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    public HttpServletResponse getHttpServletResponse() {
        return response;
    }

    public List<WindowState> getAllowedWindowStates() {
        return windowStates;
    }

    public List<PortletMode> getAllowedPortletModes() {
        return portletModes;
    }

    public PortletNamespace getPortletNamespace() {
        return namespace;
    }
}
