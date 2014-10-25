package ee.midaiganes.portal.pagelayout;

import java.io.Serializable;

import javax.annotation.Nonnull;

public class PageLayout implements Serializable {
    private static final long serialVersionUID = 1L;
    @Nonnull
    private final PageLayoutName pageLayoutName;
    @Nonnull
    private final String layoutPath;

    public PageLayout(@Nonnull PageLayoutName pageLayoutName, @Nonnull String layoutPath) {
        this.pageLayoutName = pageLayoutName;
        this.layoutPath = layoutPath;
    }

    @Nonnull
    public String getLayoutPath() {
        return layoutPath;
    }

    @Nonnull
    public PageLayoutName getPageLayoutName() {
        return pageLayoutName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PageLayout) {
            PageLayout pl = (PageLayout) o;
            return layoutPath.equals(pl.layoutPath) && pageLayoutName.equals(pl.pageLayoutName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return layoutPath.hashCode() + pageLayoutName.hashCode();
    }

    @Override
    public String toString() {
        return "PageLayout [pageLayoutName=" + pageLayoutName + ", layoutPath=" + layoutPath + "]";
    }
}
