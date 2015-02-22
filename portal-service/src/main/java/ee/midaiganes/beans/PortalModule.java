package ee.midaiganes.beans;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.management.MBeanServer;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

import ee.midaiganes.aspect.Service;
import ee.midaiganes.aspect.ServiceMethodInterceptor;
import ee.midaiganes.cache.SingleVmPool;
import ee.midaiganes.portal.group.GroupDao;
import ee.midaiganes.portal.group.GroupRepository;
import ee.midaiganes.portal.layout.LayoutDao;
import ee.midaiganes.portal.layout.LayoutRepository;
import ee.midaiganes.portal.layoutportlet.LayoutPortletDao;
import ee.midaiganes.portal.layoutportlet.LayoutPortletRepository;
import ee.midaiganes.portal.layoutset.LayoutSetDao;
import ee.midaiganes.portal.layoutset.LayoutSetRepository;
import ee.midaiganes.portal.pagelayout.PageLayoutRepository;
import ee.midaiganes.portal.permission.PermissionDao;
import ee.midaiganes.portal.permission.PermissionRepository;
import ee.midaiganes.portal.permission.PermissionService;
import ee.midaiganes.portal.permission.ResourceActionDao;
import ee.midaiganes.portal.permission.ResourceActionRepository;
import ee.midaiganes.portal.permission.ResourceDao;
import ee.midaiganes.portal.permission.ResourceRepository;
import ee.midaiganes.portal.portletinstance.PortletInstanceDao;
import ee.midaiganes.portal.portletinstance.PortletInstanceRepository;
import ee.midaiganes.portal.theme.ThemeRepository;
import ee.midaiganes.portal.user.UserDao;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.secureservices.SecureLayoutRepository;
import ee.midaiganes.secureservices.SecurePortletRepository;
import ee.midaiganes.services.DbInstallService;
import ee.midaiganes.services.LanguageRepository;
import ee.midaiganes.services.PortletPreferencesRepository;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.services.PortletURLFactory;
import ee.midaiganes.services.ServletContextResourceRepository;
import ee.midaiganes.services.ThemeVariablesService;
import ee.midaiganes.services.portal.PortalService;
import ee.midaiganes.services.portal.PortalServiceImpl;
import ee.midaiganes.util.PropsValues;

public class PortalModule extends AbstractModule {

    private static class ServiceMethodMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method t) {
            int modifier = t.getModifiers();
            return Modifier.isPublic(modifier) && !Modifier.isStatic(modifier) && t.getParameterTypes().length == 1 && !isVoid(t.getReturnType());
        }

        private boolean isVoid(Class<?> klass) {
            return void.class.equals(klass) || Void.class.equals(klass);
        }
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();

        Names.bindProperties(binder(), PropsValues.PropsUtil.properties);

        bindInterceptor(Matchers.annotatedWith(Service.class), new ServiceMethodMatcher(), new ServiceMethodInterceptor());

        install(new DatabaseModule());

        bind(MBeanServer.class).toProvider(new Provider<MBeanServer>() {
            @Override
            public MBeanServer get() {
                return ManagementFactory.getPlatformMBeanServer();
            }
        }).in(Singleton.class);

        bind(DbInstallService.class).asEagerSingleton();

        bind(UserRepository.class).in(Singleton.class);
        bind(UserDao.class).in(Singleton.class);

        bind(LayoutSetRepository.class).in(Singleton.class);
        bind(LayoutSetDao.class).in(Singleton.class);

        bind(ThemeRepository.class).in(Singleton.class);

        bind(PortletRepository.class).in(Singleton.class);

        bind(SecurePortletRepository.class).in(Singleton.class);

        bind(SecureLayoutRepository.class).in(Singleton.class);

        bind(PortletPreferencesRepository.class).in(Singleton.class);

        bind(PortletInstanceRepository.class).in(Singleton.class);
        bind(PortletInstanceDao.class).in(Singleton.class);

        bind(LanguageRepository.class).in(Singleton.class);

        bind(PageLayoutRepository.class).in(Singleton.class);

        bind(ServletContextResourceRepository.class).in(Singleton.class);

        bind(ResourceRepository.class).in(Singleton.class);
        bind(ResourceDao.class).in(Singleton.class);

        bind(PermissionRepository.class).in(Singleton.class);
        bind(PermissionDao.class).in(Singleton.class);

        bind(PermissionService.class).in(Singleton.class);

        bind(ResourceActionRepository.class).in(Singleton.class);
        bind(ResourceActionDao.class).in(Singleton.class);

        bind(GroupRepository.class).in(Singleton.class);
        bind(GroupDao.class).in(Singleton.class);

        bind(LayoutRepository.class).in(Singleton.class);
        bind(LayoutDao.class).in(Singleton.class);

        bind(LayoutPortletRepository.class).in(Singleton.class);
        bind(LayoutPortletDao.class).in(Singleton.class);

        bind(PortalService.class).to(PortalServiceImpl.class).in(Singleton.class);

        bind(ThemeVariablesService.class).in(Singleton.class);

        bind(PortletURLFactory.class).in(Singleton.class);

        bind(SingleVmPool.class).in(Singleton.class);
    }
}
