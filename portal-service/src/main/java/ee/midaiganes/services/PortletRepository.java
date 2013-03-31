package ee.midaiganes.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.WindowState;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.portlet.MidaiganesPortlet;
import ee.midaiganes.portlet.MidaiganesResourcePortlet;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.portlet.impl.PortletConfigImpl;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.XmlUtil;

@Component(value = PortalConfig.PORTLET_REPOSITORY)
public class PortletRepository {
	private static final Logger log = LoggerFactory.getLogger(PortletRepository.class);
	private final ConcurrentHashMap<PortletName, PortletAndConfiguration> portlets = new ConcurrentHashMap<>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Resource(name = PortalConfig.PORTLET_PREFERENCES_REPOSITORY)
	private PortletPreferencesRepository portletPreferencesRepository;

	@Resource(name = PortalConfig.PORTLET_INSTANCE_REPOSITORY)
	private PortletInstanceRepository portletInstanceRepository;

	public List<PortletName> getPortletNames() {
		lock.readLock().lock();
		try {
			return new ArrayList<>(portlets.keySet());
		} finally {
			lock.readLock().unlock();
		}
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

	// TODO Before the portlet container calls the destroy method, it should
	// allow any threads that are currently processing requests within the
	// portlet object to complete execution
	public void unregisterPortlets(ServletContext sc) {
		String contextPath = sc.getContextPath();
		for (PortletName entry : getPortletNames()) {
			if (entry.getContextWithSlash().equals(contextPath)) {
				try {
					lock.writeLock().lock();
					PortletAndConfiguration conf = null;
					try {
						conf = portlets.remove(entry);
					} finally {
						lock.writeLock().unlock();
					}
					if (conf != null) {
						conf.getMidaiganesPortlet().destroy();
						log.info("portlet destroyed: {}", entry);
					}
				} catch (RuntimeException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	public PortletApp getPortletApp(PortletInstance portletInstance, PortletMode portletMode, WindowState windowState) {
		if (portletInstance != null) {
			PortletAndConfiguration portlet = getPortlet(portletInstance.getPortletNamespace().getPortletName());
			if (portlet != null) {
				return new PortletApp(portletInstance, portletMode, windowState, portletPreferencesRepository, portlet);
			}
		}
		return null;
	}

	public PortletApp getPortletApp(LayoutPortlet layoutPortlet, PortletMode portletMode, WindowState windowState) {
		return getPortletApp(layoutPortlet.getPortletInstance(), portletMode, windowState);
	}

	private PortletAndConfiguration getPortlet(PortletName portletName) {
		lock.readLock().lock();
		try {
			PortletAndConfiguration portlet = portlets.get(portletName);
			if (portlet != null) {
				return portlet;
			}
		} finally {
			lock.readLock().unlock();
		}
		log.warn("no portlet with name = {};", portletName);
		return null;
	}

	private void initializePortletApp(ServletContext servletContext, PortletAppType portletAppType) {
		for (PortletType portletType : portletAppType.getPortlet()) {
			initializePortletType(servletContext, portletType);
		}
	}

	private void initializePortletType(ServletContext servletContext, PortletType portletType) {
		log.debug("portlet = {}", portletType);
		try {
			PortletName portletName = initializePortlet(servletContext, portletType);
			log.debug("full portlet name = {}", portletName);
			if (portletName != null) {
				portletInstanceRepository.addDefaultPortletInstance(portletName);
			}
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

	private static <A extends Portlet & ResourceServingPortlet> A castToResourceServingPortlet(Portlet portlet) {
		@SuppressWarnings("unchecked")
		A obj = (A) portlet;
		return obj;
	}

	private MidaiganesPortlet getMidaiganesPortlet(Portlet portlet, Class<?> obj, PortletName portletName) {
		if (ResourceServingPortlet.class.isAssignableFrom(obj)) {
			return new MidaiganesResourcePortlet(castToResourceServingPortlet(portlet), portletName);
		}
		return new MidaiganesPortlet(portlet, portletName);
	}

	private PortletName initializePortlet(ServletContext servletContext, PortletType portletType) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class<?> obj = Class.forName(portletType.getPortletClass());
		if (Portlet.class.isAssignableFrom(obj)) {
			Portlet portlet = (Portlet) obj.newInstance();
			try {
				PortletConfig portletConfig = getPortletConfig(servletContext, portletType);
				PortletName portletName = new PortletName(getContextPathName(servletContext), portletType.getPortletName().getValue());
				MidaiganesPortlet midaiganesPortlet = getMidaiganesPortlet(portlet, obj, portletName);
				midaiganesPortlet.init(portletConfig);
				PortletAndConfiguration portletAndConfiguration = new PortletAndConfiguration(midaiganesPortlet, portletConfig, portletType);
				lock.writeLock().lock();
				try {
					portlets.put(portletName, portletAndConfiguration);
				} finally {
					lock.writeLock().unlock();
				}
				return portletName;
			} catch (PortletException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
			return null;
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
		if (contextPathName.startsWith(StringPool.SLASH)) {
			contextPathName = contextPathName.substring(1);
		}
		return contextPathName;
	}
}
