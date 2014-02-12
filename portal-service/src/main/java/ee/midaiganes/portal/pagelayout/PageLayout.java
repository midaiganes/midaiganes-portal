package ee.midaiganes.portal.pagelayout;

import java.io.Serializable;

public class PageLayout implements Serializable {
	private static final long serialVersionUID = 1L;
	private final PageLayoutName pageLayoutName;
	private final String layoutPath;

	public PageLayout(PageLayoutName pageLayoutName, String layoutPath) {
		if (pageLayoutName == null) {
			throw new IllegalArgumentException("PageLayoutName is null");
		}
		this.pageLayoutName = pageLayoutName;
		if (layoutPath == null) {
			throw new IllegalArgumentException("LayoutPath is null");
		}
		this.layoutPath = layoutPath;
	}

	public String getLayoutPath() {
		return layoutPath;
	}

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
