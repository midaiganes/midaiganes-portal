package ee.midaiganes.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
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

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

import ee.midaiganes.beans.PortalBeans;
import ee.midaiganes.generated.xml.portlet.DescriptionType;
import ee.midaiganes.generated.xml.portlet.InitParamType;
import ee.midaiganes.generated.xml.portlet.PortletAppType;
import ee.midaiganes.generated.xml.portlet.PortletType;
import ee.midaiganes.generated.xml.portlet.SupportedLocaleType;
import ee.midaiganes.portal.layoutportlet.LayoutPortlet;
import ee.midaiganes.portal.portletinstance.PortletInstance;
import ee.midaiganes.portal.portletinstance.PortletInstanceRepository;
import ee.midaiganes.portlet.MidaiganesPortlet;
import ee.midaiganes.portlet.MidaiganesResourcePortlet;
import ee.midaiganes.portlet.PortletAndConfiguration;
import ee.midaiganes.portlet.PortletInitParameter;
import ee.midaiganes.portlet.PortletInitParameter.Description;
import ee.midaiganes.portlet.PortletName;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.portlet.impl.PortletConfigImpl;
import ee.midaiganes.util.GuiceUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.TimeProviderUtil;
import ee.midaiganes.util.XmlUtil;

@Resource(name = PortalBeans.PORTLET_REPOSITORY)
public class PortletRepository {
    private static final Logger log = LoggerFactory.getLogger(PortletRepository.class);
    private final ConcurrentHashMap<PortletName, PortletAndConfiguration> portlets = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReadLock readLock = lock.readLock();
    private final WriteLock writeLock = lock.writeLock();

    private final PortletPreferencesRepository portletPreferencesRepository;

    private final PortletInstanceRepository portletInstanceRepository;

    @Inject
    public PortletRepository(PortletPreferencesRepository portletPreferencesRepository, PortletInstanceRepository portletInstanceRepository) {
        this.portletPreferencesRepository = portletPreferencesRepository;
        this.portletInstanceRepository = portletInstanceRepository;
    }

    public List<PortletName> getPortletNames() {
        readLock.lock();
        try {
            return new ArrayList<>(portlets.keySet());
        } finally {
            readLock.unlock();
        }
    }

    public void registerPortlets(ServletContext servletContext, InputStream portletXmlInputStream) {
        try {
            PortletAppType portletAppType = XmlUtil.unmarshal(PortletAppType.class, portletXmlInputStream);
            if (portletAppType != null) {
                initializePortletApp(servletContext, portletAppType, GuiceUtil.getInjector(servletContext));
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
                    writeLock.lock();
                    PortletAndConfiguration conf = null;
                    try {
                        conf = portlets.remove(entry);
                    } finally {
                        writeLock.unlock();
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

    public PortletAndConfiguration getPortlet(PortletName portletName) {
        readLock.lock();
        try {
            PortletAndConfiguration portlet = portlets.get(portletName);
            if (portlet != null) {
                return portlet;
            }
        } finally {
            readLock.unlock();
        }
        log.warn("no portlet with name = {};", portletName);
        return null;
    }

    private void initializePortletApp(ServletContext servletContext, PortletAppType portletAppType, Injector injector) {
        for (PortletType portletType : portletAppType.getPortlet()) {
            initializePortletType(servletContext, portletType, injector);
        }
    }

    private void initializePortletType(ServletContext servletContext, PortletType portletType, Injector injector) {
        log.debug("portlet = {}", portletType);
        try {
            PortletName portletName = initializePortlet(servletContext, portletType, injector);
            log.debug("full portlet name = {}", portletName);
            if (portletName != null) {
                portletInstanceRepository.addDefaultPortletInstance(portletName);
            }
        } catch (ClassNotFoundException e) {
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

    private static class PortletModule extends AbstractModule {
        private final Class<? extends Portlet> portletClass;

        private PortletModule(Class<? extends Portlet> portletClass) {
            this.portletClass = portletClass;
        }

        @Override
        protected void configure() {
            bind(Portlet.class).to(portletClass).in(Singleton.class);
        }
    }

    private PortletName initializePortlet(ServletContext servletContext, PortletType portletType, Injector injector) throws ClassNotFoundException {
        Class<?> obj = Class.forName(portletType.getPortletClass());
        if (Portlet.class.isAssignableFrom(obj)) {
            @SuppressWarnings("unchecked")
            final Class<? extends Portlet> portletClass = (Class<? extends Portlet>) obj;
            Injector portletInjector = injector.createChildInjector(new PortletModule(portletClass));
            Portlet portlet = portletInjector.getInstance(Portlet.class);
            try {
                PortletConfig portletConfig = getPortletConfig(servletContext, portletType);
                PortletName portletName = new PortletName(getContextPathName(servletContext), portletType.getPortletName().getValue());
                MidaiganesPortlet midaiganesPortlet = getMidaiganesPortlet(portlet, obj, portletName);
                long start = TimeProviderUtil.currentTimeMillis();
                midaiganesPortlet.init(portletConfig);
                log.info("Portlet '{}' init took: {}ms", portletName, Long.valueOf(System.currentTimeMillis() - start));
                PortletAndConfiguration portletAndConfiguration = new PortletAndConfiguration(midaiganesPortlet, portletConfig, portletType);
                writeLock.lock();
                try {
                    portlets.put(portletName, portletAndConfiguration);
                } finally {
                    writeLock.unlock();
                }
                return portletName;
            } catch (PortletException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }
        throw new IllegalArgumentException(portletType.getPortletClass() + " is not implementing " + Portlet.class.getName());
    }

    private PortletConfig getPortletConfig(ServletContext servletContext, PortletType portletType) throws IOException {
        return new PortletConfigImpl(servletContext, portletType.getPortletName().getValue(), getInitParameters(portletType), getSupportedLocales(portletType));
    }

    private List<PortletInitParameter> getInitParameters(PortletType portletType) {
        CopyOnWriteArrayList<PortletInitParameter> initParameters = new CopyOnWriteArrayList<>();
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
        List<Locale> supportedLocales = new ArrayList<>(portletType.getSupportedLocale().size());
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
