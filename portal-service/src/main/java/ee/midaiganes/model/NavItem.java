package ee.midaiganes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.util.PortalURLUtil;

public class NavItem implements Serializable, Comparable<NavItem> {
    private static final long serialVersionUID = 1L;
    private final Layout layout;
    private final List<NavItem> childs = new ArrayList<>();

    public NavItem(Layout layout, List<Layout> layouts) {
        this.layout = layout;
        for (Layout l : layouts) {
            Long parentId = l.getParentId();
            if (parentId != null && parentId.longValue() == this.layout.getId()) {
                childs.add(new NavItem(l, layouts));
            }
        }
        Collections.sort(childs);
    }

    public List<NavItem> getChilds() {
        return childs;
    }

    public Layout getLayout() {
        return layout;
    }

    @Override
    public int compareTo(NavItem o) {
        return (int) (Math.abs(layout.getNr()) - Math.abs(o.layout.getNr()));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NavItem) {
            NavItem i = (NavItem) o;
            return layout.equals(i.layout) && childs.equals(i.childs);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return layout.hashCode();
    }

    public String getUrl() {
        return PortalURLUtil.getFullURLByFriendlyURL(getLayout().getFriendlyUrl());
    }
}
