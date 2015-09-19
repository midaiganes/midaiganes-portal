package ee.midaiganes.model;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.util.PortalURLUtil;

public class NavItem implements Serializable, Comparable<NavItem> {
    private static final long serialVersionUID = 1L;
    private final Layout layout;
    private final ImmutableList<NavItem> childs;
    private final long languageId;

    public NavItem(Layout layout, @Nonnull ImmutableList<Layout> layouts, long languageId) {
        Preconditions.checkNotNull(layouts, "Layouts is null");
        this.languageId = languageId;
        this.layout = Preconditions.checkNotNull(layout, "Layout is null");
        this.childs = findChilds(layout.getId(), layouts, languageId);
    }

    private static ImmutableList<NavItem> findChilds(long layoutId, ImmutableList<Layout> layouts, long languageId) {
        Iterable<Layout> filter = Iterables.filter(layouts, l -> {
            Long parentId = l.getParentId();
            return parentId != null && parentId.longValue() == layoutId;
        });
        Iterable<NavItem> transform = Iterables.transform(filter, l -> new NavItem(l, layouts, languageId));
        return Ordering.natural().immutableSortedCopy(transform);
    }

    public ImmutableList<NavItem> getChilds() {
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
