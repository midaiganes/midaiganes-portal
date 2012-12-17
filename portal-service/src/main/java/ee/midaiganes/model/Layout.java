package ee.midaiganes.model;

import java.io.Serializable;
import java.util.List;

public class Layout implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long layoutSetId;
	private String friendlyUrl;
	private ThemeName themeName;
	private String pageLayoutId;
	private long nr;
	private long parentId;
	private String defaultLayoutTitleLanguageId;
	private List<LayoutTitle> layoutTitles;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLayoutSetId() {
		return layoutSetId;
	}

	public void setLayoutSetId(long layoutSetId) {
		this.layoutSetId = layoutSetId;
	}

	public String getFriendlyUrl() {
		return friendlyUrl;
	}

	public void setFriendlyUrl(String friendlyUrl) {
		this.friendlyUrl = friendlyUrl;
	}

	public boolean isDefault() {
		return false;
	}

	public ThemeName getThemeName() {
		return themeName;
	}

	public void setThemeName(ThemeName themeName) {
		this.themeName = themeName;
	}

	public String getPageLayoutId() {
		return pageLayoutId;
	}

	public void setPageLayoutId(String pageLayoutId) {
		this.pageLayoutId = pageLayoutId;
	}

	public long getNr() {
		return nr;
	}

	public void setNr(long nr) {
		this.nr = nr;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public List<LayoutTitle> getLayoutTitles() {
		return layoutTitles;
	}

	public void setLayoutTitles(List<LayoutTitle> layoutTitles) {
		this.layoutTitles = layoutTitles;
	}

	public String getDefaultLayoutTitleLanguageId() {
		return defaultLayoutTitleLanguageId;
	}

	public void setDefaultLayoutTitleLanguageId(String defaultLayoutTitleLanguageId) {
		this.defaultLayoutTitleLanguageId = defaultLayoutTitleLanguageId;
	}

	public LayoutTitle getDefaultLayoutTitle() {
		if (layoutTitles != null) {
			for (LayoutTitle lt : layoutTitles) {
				if (lt.getLanguageId().equals(defaultLayoutTitleLanguageId)) {
					return lt;
				}
			}
		}
		return new LayoutTitle(id, defaultLayoutTitleLanguageId, friendlyUrl);
	}

	@Override
	public String toString() {
		return "Layout [id=" + id + ", layoutSetId=" + layoutSetId + ", friendlyUrl=" + friendlyUrl + ", themeName=" + themeName + ", pageLayoutId="
				+ pageLayoutId + ", nr=" + nr + ", parentId=" + parentId + ", defaultLayoutTitleLanguageId=" + defaultLayoutTitleLanguageId + ", layoutTitles="
				+ layoutTitles + "]";
	}
}
