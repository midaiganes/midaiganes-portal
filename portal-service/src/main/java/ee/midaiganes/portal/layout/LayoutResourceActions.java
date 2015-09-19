package ee.midaiganes.portal.layout;

import ee.midaiganes.portal.permission.ResourceAction;

public enum LayoutResourceActions implements ResourceAction {
    EDIT, ADD_PORTLET, PERMISSIONS;
    @Override
    public String getAction() {
        return name();
    }
}
