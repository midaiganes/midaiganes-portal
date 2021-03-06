package ee.midaiganes.portlet;

import ee.midaiganes.model.ContextAndName;
import ee.midaiganes.util.StringUtil;

public class PortletName extends ContextAndName {
    private static final long serialVersionUID = 1L;

    public PortletName(String fullPortletName) {
        super(fullPortletName);
    }

    public PortletName(String context, String name) {
        super(context, name);
    }

    public static PortletName getPortletNameOrNull(String portletName) {
        if (!StringUtil.isEmpty(portletName)) {
            String[] names = ContextAndName.split(portletName);
            if (names.length == 2) {
                return new PortletName(names[0], names[1]);
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof PortletName) && super.equals(o);
    }
}
