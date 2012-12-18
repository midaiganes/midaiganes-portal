package ee.midaiganes.portlet.impl;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortletContextImpl implements PortletContext {
	private static final Logger log = LoggerFactory.getLogger(PortletContextImpl.class);
	private final ServletContext servletContext;
	private final PortletConfig portletConfig;

	public PortletContextImpl(ServletContext servletContext, PortletConfig portletConfig) {
		this.servletContext = servletContext;
		this.portletConfig = portletConfig;
	}

	@Override
	public String getServerInfo() {
		return servletContext.getServerInfo();
	}

	@Override
	public PortletRequestDispatcher getRequestDispatcher(String path) {
		RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(path);
		if (requestDispatcher != null) {
			return new PortletRequestDispatcherImpl(requestDispatcher, portletConfig);
		}
		return null;
	}

	@Override
	public PortletRequestDispatcher getNamedDispatcher(String name) {
		// TODO
		throw new RuntimeException("not implemented");
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return servletContext.getResourceAsStream(path);
	}

	@Override
	public int getMajorVersion() {
		return 2;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public String getMimeType(String file) {
		return servletContext.getMimeType(file);
	}

	@Override
	public String getRealPath(String path) {
		return servletContext.getRealPath(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return servletContext.getResourcePaths(path);
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return servletContext.getResource(path);
	}

	@Override
	public Object getAttribute(String name) {
		return servletContext.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return servletContext.getAttributeNames();
	}

	@Override
	public String getInitParameter(String name) {
		return servletContext.getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return servletContext.getInitParameterNames();
	}

	@Override
	public void log(String msg) {
		log.info(msg);
	}

	@Override
	public void log(String message, Throwable throwable) {
		log.error(message, throwable);
	}

	@Override
	public void removeAttribute(String name) {
		servletContext.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object object) {
		servletContext.setAttribute(name, object);
	}

	@Override
	public String getPortletContextName() {
		return servletContext.getServletContextName();
	}

	@Override
	public Enumeration<String> getContainerRuntimeOptions() {
		// TODO
		return Collections.<String> enumeration(Arrays.<String> asList("javax.portlet.actionScopedRequestAttributes", "javax.portlet.escapeXml"));
	}
}
