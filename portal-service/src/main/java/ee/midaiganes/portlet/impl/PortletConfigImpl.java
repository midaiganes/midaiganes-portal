package ee.midaiganes.portlet.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PortletInitParameter;

public class PortletConfigImpl implements PortletConfig {
	private static final Logger log = LoggerFactory.getLogger(PortletConfigImpl.class);
	private final String portletName;
	private final List<PortletInitParameter> initParameters;
	private final List<Locale> supportedLocales;
	private final ResourceBundle resourceBundle;
	private final PortletContext portletContext;

	public PortletConfigImpl(ServletContext servletContext, String portletName, List<PortletInitParameter> initParameters, List<Locale> supportedLocales)
			throws IOException {
		this.portletName = portletName;
		this.initParameters = initParameters;
		this.supportedLocales = supportedLocales;
		String bundle = "javax.portlet.title=" + portletName + "\r\n";
		bundle += "javax.portlet.short-title=" + portletName + "\r\n";
		bundle += "javax.portlet.keywords=" + portletName + "\r\n";
		bundle += "javax.portlet.description=" + portletName + "\r\n";
		bundle += "javax.portlet.display-name=" + portletName + "\r\n";
		this.resourceBundle = new PropertyResourceBundle(new StringReader(bundle));
		this.portletContext = new PortletContextImpl(servletContext, this);
	}

	@Override
	public String getPortletName() {
		return portletName;
	}

	@Override
	public PortletContext getPortletContext() {
		return portletContext;
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		log.debug("not implemented '{}'", locale);
		// TODO
		return resourceBundle;
	}

	@Override
	public String getInitParameter(String name) {
		for (PortletInitParameter initParam : initParameters) {
			if (name != null && name.equals(initParam.getName())) {
				return initParam.getValue();
			}
		}
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		List<String> initParamNames = new ArrayList<String>(initParameters.size());
		for (PortletInitParameter initParam : initParameters) {
			initParamNames.add(initParam.getName());
		}
		return Collections.enumeration(initParamNames);
	}

	@Override
	public Enumeration<String> getPublicRenderParameterNames() {
		// TODO
		throw new IllegalStateException("not implemented");
	}

	@Override
	public String getDefaultNamespace() {
		// TODO
		return XMLConstants.NULL_NS_URI;
	}

	@Override
	public Enumeration<QName> getPublishingEventQNames() {
		// TODO
		throw new IllegalStateException("not implemented");
	}

	@Override
	public Enumeration<QName> getProcessingEventQNames() {
		// TODO
		throw new IllegalStateException("not implemented");
	}

	@Override
	public Enumeration<Locale> getSupportedLocales() {
		return Collections.enumeration(supportedLocales);
	}

	@Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		// TODO
		throw new IllegalStateException("not implemented");
	}
}
