package ee.midaiganes.portlets.layout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LayoutModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
    private String parentId;
    private String defaultLayoutTitleLanguageId;
    private Map<String, String> layoutTitles;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDefaultLayoutTitleLanguageId() {
        return defaultLayoutTitleLanguageId;
    }

    public void setDefaultLayoutTitleLanguageId(String defaultLayoutTitleLanguageId) {
        this.defaultLayoutTitleLanguageId = defaultLayoutTitleLanguageId;
    }

    public Map<String, String> getLayoutTitles() {
        if (layoutTitles == null) {
            layoutTitles = new HashMap<>();
        }
        return layoutTitles;
    }

    public void setLayoutTitles(Map<String, String> layoutTitles) {
        this.layoutTitles = layoutTitles;
    }
}
