package ee.midaiganes.util;

import javax.servlet.ServletContext;

import com.google.inject.Injector;

import ee.midaiganes.servlet.listener.GuiceContextLoaderListener;

public class GuiceUtil {
    public static Injector getInjector(ServletContext sc) {
        return (Injector) (sc.getAttribute(GuiceContextLoaderListener.SERVLET_ATTRIBUTE));
    }
}
