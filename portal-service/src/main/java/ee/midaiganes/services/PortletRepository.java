package ee.midaiganes.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.generated.xml.portlet.DescriptionType;
import ee.midaiganes.generated.xml.portlet.InitParamType;
import ee.midaiganes.generated.xml.portlet.PortletAppType;
import ee.midaiganes.generated.xml.portlet.PortletType;
import ee.midaiganes.generated.xml.portlet.SupportedLocaleType;
import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.model.PortletAndConfiguration;
import ee.midaiganes.model.PortletInitParameter;
import ee.midaiganes.model.PortletInitParameter.Description;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.portlet.impl.PortletConfigImpl;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.XmlUtil;

public class PortletRepository {
	private static final Logger log = LoggerFactory.getLogger(PortletRepository.class);
	private final Map<String, Map<String, PortletAndConfiguration>> portlets = new ConcurrentHashMap<String, Map<String, PortletAndConfiguration>>();

	@Resource(name = PortalConfig.PORTLET_PREFERENCES_REPOSITORY)
	private PortletPreferencesRepository portletPreferencesRepository;

	@Resource(name = PortalConfig.PORTLET_INSTANCE_REPOSITORY)
	private PortletInstanceRepository portletInstanceRepository;

	public List<PortletName> getPortletNames() {
		List<PortletName> list = new ArrayList<>();
		for (Map.Entry<String, Map<String, PortletAndConfiguration>> p : portlets.entrySet()) {
			for (String name : p.getValue().keySet()) {
				list.add(new PortletName(p.getKey(), name));
			}
		}
		return list;
	}

	public void registerPortlets(ServletContext servletContext, InputStream portletXmlInputStream) {
		try {
			PortletAppType portletAppType = XmlUtil.unmarshal(PortletAppType.class, portletXmlInputStream);
			if (portletAppType != null) {
				initializePortletApp(servletContext, portletAppType);
			}
		} catch (JAXBException e) {
			log.error(e.getMessage(), e);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	public PortletApp getPortletApp(PortletName portletName, String windowID, PortletMode portletMode, WindowState windowState) {
		if (portletName != null) {
			PortletAndConfiguration portlet = getPortlet(portletName);
			if (portlet != null) {
				return new PortletApp(windowID, portletName, portletMode, windowState, portletPreferencesRepository, portlet);
			}
		}
		return null;
	}

	public PortletApp getPortletApp(LayoutPortlet layoutPortlet, PortletMode portletMode, WindowState windowState) {
		return getPortletApp(layoutPortlet.getPortletInstance().getPortletName(), layoutPortlet.getPortletInstance().getWindowID(), portletMode, windowState);
	}

	private PortletAndConfiguration getPortlet(PortletName portletName) {
		log.debug("portlets = {}", portlets);
		Map<String, PortletAndConfiguration> map = portlets.get(portletName.getContext());
		if (map != null) {
			PortletAndConfiguration portlet = map.get(portletName.getName());
			if (portlet != null) {
				return portlet;
			}
		}
		log.warn("no portlet with name = {}; return first portlet...", portletName);
		return portlets.values().iterator().next().values().iterator().next();
	}

	private void initializePortletApp(ServletContext servletContext, PortletAppType portletAppType) {
		for (PortletType portletType : portletAppType.getPortlet()) {
			initializePortletType(servletContext, portletType);
		}
	}

	private void initializePortletType(ServletContext servletContext, PortletType portletType) {
		log.debug("portlet = {}", portletType);
		try {
			initializePortlet(servletContext, portletType);
			PortletName portletName = getPortletName(servletContext, portletType.getPortletName().getValue());
			log.debug("full portlet name = {}", portletName);
			portletInstanceRepository.addPortletInstance(portletName, StringPool.DEFAULT_PORTLET_WINDOWID);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void initializePortlet(ServletContext servletContext, PortletType portletType) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class<?> obj = Class.forName(portletType.getPortletClass());
		if (Portlet.class.isAssignableFrom(obj)) {
			Portlet portlet = (Portlet) obj.newInstance();
			try {
				PortletConfig portletConfig = getPortletConfig(servletContext, portletType);
				portlet.init(portletConfig);
				Map<String, PortletAndConfiguration> map = portlets.get(getContextPathName(servletContext));
				if (map == null) {
					map = new ConcurrentHashMap<String, PortletAndConfiguration>();
					portlets.put(getContextPathName(servletContext), map);
				}
				map.put(portletType.getPortletName().getValue(), new PortletAndConfiguration(portlet, portletConfig, portletType));
			} catch (PortletException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			throw new IllegalArgumentException(portletType.getPortletClass() + " is not implementing " + Portlet.class.getName());
		}
	}

	private PortletConfig getPortletConfig(ServletContext servletContext, PortletType portletType) throws IOException {
		return new PortletConfigImpl(servletContext, portletType.getPortletName().getValue(), getInitParameters(portletType), getSupportedLocales(portletType));
	}

	private List<PortletInitParameter> getInitParameters(PortletType portletType) {
		CopyOnWriteArrayList<PortletInitParameter> initParameters = new CopyOnWriteArrayList<PortletInitParameter>();
		for (InitParamType initParam : portletType.getInitParam()) {
			CopyOnWriteArrayList<Description> descriptions = new CopyOnWriteArrayList<>();
			for (DescriptionType description : initParam.getDescription()) {
				descriptions.add(new Description(description.getLang(), description.getValue()));
			}
			initParameters.add(new PortletInitParameter(initParam.getId(), initParam.getName().getValue(), initParam.getValue().getValue(), descriptions));
		}
		return initParameters;
	}

	private List<Locale> getSupportedLocales(PortletType portletType) {
		List<Locale> supportedLocales = new ArrayList<Locale>(portletType.getSupportedLocale().size());
		for (SupportedLocaleType supportedLocaleType : portletType.getSupportedLocale()) {
			supportedLocales.add(new Locale(supportedLocaleType.getValue()));
		}
		return supportedLocales;
	}

	private String getContextPathName(ServletContext servletContext) {
		String contextPathName = servletContext.getContextPath();
		if (contextPathName.startsWith("/")) {
			contextPathName = contextPathName.substring(1);
		}
		return contextPathName;
	}

	private PortletName getPortletName(ServletContext servletContext, String portletName) {
		return new PortletName(getContextPathName(servletContext), portletName);
	}
}
