package ee.midaiganes.portal.layoutset;

import ee.midaiganes.portal.permission.ResourceAction;

public enum LayoutSetResourceActions implements ResourceAction {
    EDIT;
    @Override
    public String getAction() {
        return name();
    }
}
