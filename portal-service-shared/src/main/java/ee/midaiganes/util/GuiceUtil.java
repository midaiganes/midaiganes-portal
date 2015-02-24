package ee.midaiganes.util;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;

public class GuiceUtil {
    public static final String SERVLET_ATTRIBUTE = GuiceUtil.class.getName();

    /**
     * @return current servlet context injector
     */
    @Nonnull
    public static Injector getInjector(ServletContext sc) {
        return Preconditions.checkNotNull(_getInjector(sc));
    }

    /**
     * @return portal servlet context injector
     */
    @Nonnull
    public static Injector getPortalInjector(ServletContext sc) {
        return getInjector(Preconditions.checkNotNull(sc.getContext(System.getProperty("portal.context"))));
    }

    @Nonnull
    public static Injector getCurrentOrPortalInjector(ServletContext sc) {
        Injector injector = _getInjector(sc);
        return injector != null ? injector : getPortalInjector(sc);
    }

    private static Injector _getInjector(ServletContext sc) {
        return (Injector) (sc.getAttribute(GuiceUtil.SERVLET_ATTRIBUTE));
    }
}
