package ee.midaiganes.portlet.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import ee.midaiganes.util.StringPool;

public class PortletSessionImpl implements PortletSession {
	private static final String PORTLET_SCOPE_PREFIX = "javax.portlet.p.";
	private final HttpSession session;
	private final String portletScopePrefix;
	private final PortletContext portletContext;

	public PortletSessionImpl(HttpSession session, PortletRequest request, PortletContext portletContext) {
		this.session = session;
		this.portletContext = portletContext;
		this.portletScopePrefix = PORTLET_SCOPE_PREFIX + request.getWindowID() + StringPool.QUESTION;
	}

	@Override
	public Object getAttribute(String name) {
		return getAttribute(name, PortletSession.PORTLET_SCOPE);
	}

	@Override
	public Object getAttribute(String name, int scope) {
		if (scope == PortletSession.PORTLET_SCOPE) {
			return session.getAttribute(portletScopePrefix + name);
		} else if (scope == PortletSession.APPLICATION_SCOPE) {
			return session.getAttribute(name);
		}
		throw new IllegalArgumentException("invalid scope " + scope);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return getAttributeNames(PortletSession.PORTLET_SCOPE);
	}

	@Override
	public Enumeration<String> getAttributeNames(int scope) {
		if (scope != PortletSession.APPLICATION_SCOPE && scope != PortletSession.PORTLET_SCOPE) {
			throw new IllegalArgumentException("invalid scope " + scope);
		}
		Enumeration<String> names = session.getAttributeNames();
		if (scope == PortletSession.PORTLET_SCOPE) {
			List<String> list = new ArrayList<String>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				if (name.startsWith(portletScopePrefix)) {
					list.add(name.substring(portletScopePrefix.length()));
				}
			}
			names = Collections.enumeration(list);
		}
		return names;
	}

	@Override
	public long getCreationTime() {
		return session.getCreationTime();
	}

	@Override
	public String getId() {
		return session.getId();
	}

	@Override
	public long getLastAccessedTime() {
		return session.getLastAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	@Override
	public void invalidate() {
		session.invalidate();
	}

	@Override
	public boolean isNew() {
		return session.isNew();
	}

	@Override
	public void removeAttribute(String name) {
		removeAttribute(name, PortletSession.PORTLET_SCOPE);
	}

	@Override
	public void removeAttribute(String name, int scope) {
		if (scope == PortletSession.PORTLET_SCOPE) {
			session.removeAttribute(portletScopePrefix + name);
		} else if (scope == PortletSession.APPLICATION_SCOPE) {
			session.removeAttribute(name);
		} else {
			throw new IllegalArgumentException("invalid scope " + scope);
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		setAttribute(name, value, PortletSession.PORTLET_SCOPE);
	}

	@Override
	public void setAttribute(String name, Object value, int scope) {
		if (scope == PortletSession.PORTLET_SCOPE) {
			session.setAttribute(portletScopePrefix + name, value);
		} else if (scope == PortletSession.APPLICATION_SCOPE) {
			session.setAttribute(name, value);
		} else {
			throw new IllegalArgumentException("invalid scope " + scope);
		}
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		session.setMaxInactiveInterval(interval);
	}

	@Override
	public PortletContext getPortletContext() {
		return portletContext;
	}

	@Override
	public Map<String, Object> getAttributeMap() {
		return getAttributeMap(PortletSession.PORTLET_SCOPE);
	}

	@Override
	public Map<String, Object> getAttributeMap(int scope) {
		if (scope == PortletSession.APPLICATION_SCOPE || scope == PortletSession.PORTLET_SCOPE) {
			Enumeration<String> names = getAttributeNames(scope);
			Map<String, Object> map = new HashMap<String, Object>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				map.put(name, getAttribute(name, scope));
			}
			return map;
		}
		throw new IllegalArgumentException("invalid scope " + scope);
	}
}
