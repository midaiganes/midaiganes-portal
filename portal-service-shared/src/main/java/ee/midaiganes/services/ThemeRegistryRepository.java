package ee.midaiganes.services;

import java.io.InputStream;

public interface ThemeRegistryRepository {

    void registerThemes(String contextPath, InputStream themeXmlInputStream);

    void unregisterThemes(String contextPath);

}