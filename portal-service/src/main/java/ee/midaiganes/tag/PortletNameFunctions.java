package ee.midaiganes.tag;

import ee.midaiganes.model.PortletName;
import ee.midaiganes.util.PropsValues;
import ee.midaiganes.util.StringPool;

public class PortletNameFunctions {
    public static String getMidaiganesPortletName(String name) {
        return new PortletName(PropsValues.PORTAL_CONTEXT.replace(StringPool.SLASH, StringPool.EMPTY), name).getFullName();
    }
}
