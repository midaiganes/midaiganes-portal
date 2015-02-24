package ee.midaiganes.services;

import java.io.InputStream;

import javax.servlet.ServletContext;

public interface PortletRegistryRepository {
    void registerPortlets(ServletContext servletContext, InputStream portletXmlInputStream);

    void unregisterPortlets(ServletContext sc);
}