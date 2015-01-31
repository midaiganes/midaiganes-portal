package ee.midaiganes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.util.PortalURLUtil;

public class NavItem implements Serializable, Comparable<NavItem> {
    private static final long serialVersionUID = 1L;
    private final Layout layout;
    private final ImmutableList<NavItem> childs;
    private final long languageId;

    public NavItem(Layout layout, @Nonnull List<Layout> layouts, long languageId) {
        Preconditions.checkNotNull(layouts, "Layouts is null");
        this.languageId = languageId;
        this.layout = Preconditions.checkNotNull(layout, "Layout is null");
        List<NavItem> childs = new ArrayList<>();
        for (Layout l : layouts) {
            Long parentId = l.getParentId();
            if (parentId != null && parentId.longValue() == this.layout.getId()) {
                childs.add(new NavItem(l, layouts, languageId));
            }
        }
        Collections.sort(childs);
        this.childs = ImmutableList.copyOf(childs);
    }

    public List<NavItem> getChilds() {
        return childs;
    }

    public Layout getLayout() {
        return layout;
    }

    public String getLayoutTitle() {
        String title = layout.getTitle(languageId);
        return title != null ? title : layout.getDefaultLayoutTitle().getTitle();
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
