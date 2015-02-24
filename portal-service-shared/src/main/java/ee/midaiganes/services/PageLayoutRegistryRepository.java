package ee.midaiganes.services;

import java.io.InputStream;

public interface PageLayoutRegistryRepository {

    void registerPageLayouts(String contextPath, InputStream pageLayoutXmlStream);

    void unregisterPageLayouts(String contextPath);

}