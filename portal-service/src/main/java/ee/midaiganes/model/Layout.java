package ee.midaiganes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Layout implements Serializable, PortalResource {
	private static final long serialVersionUID = 1L;

	private long id;
	private long layoutSetId;
	private String friendlyUrl;
	private ThemeName themeName;
	private String pageLayoutId;
	private long nr;
	private Long parentId;
	private long defaultLayoutTitleLanguageId;
	private List<LayoutTitle> layoutTitles;

	public Layout() {
	}

	public Layout(Layout layout) {
		id = layout.id;
		layoutSetId = layout.layoutSetId;
		friendlyUrl = layout.friendlyUrl;
		themeName = layout.themeName == null ? null : new ThemeName(layout.themeName);
		pageLayoutId = layout.pageLayoutId;
		nr = layout.nr;
		parentId = layout.parentId;
		defaultLayoutTitleLanguageId = layout.defaultLayoutTitleLanguageId;
		if (layout.layoutTitles != null) {
			layoutTitles = new ArrayList<>(layout.layoutTitles.size());
			for (LayoutTitle lt : layout.layoutTitles) {
				layoutTitles.add(new LayoutTitle(lt));
			}
		}
	}

	@Override
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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<LayoutTitle> getLayoutTitles() {
		return layoutTitles;
	}

	public void setLayoutTitles(List<LayoutTitle> layoutTitles) {
		this.layoutTitles = layoutTitles;
	}

	public long getDefaultLayoutTitleLanguageId() {
		return defaultLayoutTitleLanguageId;
	}

	public void setDefaultLayoutTitleLanguageId(long defaultLayoutTitleLanguageId) {
		this.defaultLayoutTitleLanguageId = defaultLayoutTitleLanguageId;
	}

	public LayoutTitle getDefaultLayoutTitle() {
		LayoutTitle layoutTitle = getLayoutTitle(defaultLayoutTitleLanguageId);
		return layoutTitle != null ? layoutTitle : new LayoutTitle(id, defaultLayoutTitleLanguageId, friendlyUrl);
	}

	/**
	 * @return LayoutTitle or null
	 */
	public LayoutTitle getLayoutTitle(long languageId) {
		if (layoutTitles != null) {
			for (LayoutTitle layoutTitle : layoutTitles) {
				if (layoutTitle.getLanguageId() == languageId) {
					return layoutTitle;
				}
			}
		}
		return null;
	}

	public String getTitle(long languageId) {
		LayoutTitle layoutTitle = getLayoutTitle(languageId);
		return layoutTitle != null ? layoutTitle.getTitle() : getDefaultLayoutTitle().getTitle();
	}

	@Override
	public String getResource() {
		return Layout.class.getName();
	}

	@Override
	public String toString() {
		return "Layout [id=" + id + ", layoutSetId=" + layoutSetId + ", friendlyUrl=" + friendlyUrl + ", themeName=" + themeName + ", pageLayoutId="
				+ pageLayoutId + ", nr=" + nr + ", parentId=" + parentId + ", defaultLayoutTitleLanguageId=" + defaultLayoutTitleLanguageId + ", layoutTitles="
				+ layoutTitles + "]";
	}
}
