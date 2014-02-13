package ee.midaiganes.portal.layout;

import java.io.Serializable;
import java.util.List;

import ee.midaiganes.model.PortalResource;
import ee.midaiganes.portal.theme.ThemeName;

public class Layout implements Serializable, PortalResource {
    private static final long serialVersionUID = 1L;
    public static final long DEFAULT_LAYOUT_ID = 0;

    private final long id;
    private final long layoutSetId;
    private final String friendlyUrl;
    private final ThemeName themeName;
    private final String pageLayoutId;
    private final long nr;
    private final Long parentId;
    private final long defaultLayoutTitleLanguageId;
    private final List<LayoutTitle> layoutTitles;

    private static final long DEFAULT_LAYOUT_TITLE_LANGUAGE_ID = -1;

    private Layout(long layoutSetId, String friendlyUrl, ThemeName themeName, String pageLayoutId) {
        this(DEFAULT_LAYOUT_ID, layoutSetId, friendlyUrl, themeName, pageLayoutId, 0, null, DEFAULT_LAYOUT_TITLE_LANGUAGE_ID, null);
    }

    public static Layout getDefault(long layoutSetId, String friendlyUrl, ThemeName themeName, String pageLayoutId) {
        return new Layout(layoutSetId, friendlyUrl, themeName, pageLayoutId);
    }

    public Layout(long id, long layoutSetId, String friendlyUrl, ThemeName themeName, String pageLayoutId, long nr, Long parentId, long defaultLayoutTitleLanguageId,
            List<LayoutTitle> layoutTitles) {
        this.id = id;
        this.layoutSetId = layoutSetId;
        this.friendlyUrl = friendlyUrl;
        this.themeName = themeName;
        this.pageLayoutId = pageLayoutId;
        this.nr = nr;
        this.parentId = parentId;
        this.defaultLayoutTitleLanguageId = defaultLayoutTitleLanguageId;
        this.layoutTitles = layoutTitles;
    }

    public Layout withLayoutTitles(List<LayoutTitle> layoutTitles) {
        return new Layout(id, layoutSetId, friendlyUrl, themeName, pageLayoutId, nr, parentId, defaultLayoutTitleLanguageId, layoutTitles);
    }

    @Override
    public long getId() {
        return id;
    }

    public long getLayoutSetId() {
        return layoutSetId;
    }

    public String getFriendlyUrl() {
        return friendlyUrl;
    }

    public boolean isDefault() {
        return id == DEFAULT_LAYOUT_ID;
    }

    public ThemeName getThemeName() {
        return themeName;
    }

    public String getPageLayoutId() {
        return pageLayoutId;
    }

    public long getNr() {
        return nr;
    }

    public Long getParentId() {
        return parentId;
    }

    public List<LayoutTitle> getLayoutTitles() {
        return layoutTitles;
    }

    public long getDefaultLayoutTitleLanguageId() {
        return defaultLayoutTitleLanguageId;
    }

    public LayoutTitle getDefaultLayoutTitle() {
        LayoutTitle layoutTitle = getLayoutTitle(defaultLayoutTitleLanguageId);
        return layoutTitle != null ? layoutTitle : LayoutTitle.getDefault(id, defaultLayoutTitleLanguageId, friendlyUrl);
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
        return "Layout [id=" + id + ", layoutSetId=" + layoutSetId + ", friendlyUrl=" + friendlyUrl + ", themeName=" + themeName + ", pageLayoutId=" + pageLayoutId + ", nr=" + nr
                + ", parentId=" + parentId + ", defaultLayoutTitleLanguageId=" + defaultLayoutTitleLanguageId + ", layoutTitles=" + layoutTitles + "]";
    }
}
