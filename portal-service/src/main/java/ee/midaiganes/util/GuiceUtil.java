package ee.midaiganes.util;

import javax.servlet.ServletContext;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;

public class GuiceUtil {
    public static final String SERVLET_ATTRIBUTE = GuiceUtil.class.getName();

    public static Injector getInjector(ServletContext sc) {
        return Preconditions.checkNotNull((Injector) (sc.getAttribute(GuiceUtil.SERVLET_ATTRIBUTE)));
    }
}
