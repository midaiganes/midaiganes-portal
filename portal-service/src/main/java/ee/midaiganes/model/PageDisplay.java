package ee.midaiganes.model;

import ee.midaiganes.model.RequestInfo.PortletURL;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layoutset.LayoutSet;
import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.portal.user.User;

public class PageDisplay {
    private final LayoutSet layoutSet;
    private final Layout layout;
    private final User user;
    private final RequestInfo requestInfo;
    private final Theme theme;
    private final long languageId;

    public PageDisplay(LayoutSet layoutSet, Layout layout, User user, RequestInfo requestInfo, Theme theme, long languageId) {
        this.layoutSet = layoutSet;
        this.layout = layout;
        this.user = user;
        this.requestInfo = requestInfo;
        this.theme = theme;
        this.languageId = languageId;
    }

    public PageDisplay(PageDisplay pd, User user) {
        this(pd.getLayoutSet(), pd.getLayout(), user, pd.getRequestInfo(), pd.getTheme(), pd.getLanguageId());
    }

    public LayoutSet getLayoutSet() {
        return layoutSet;
    }

    public Layout getLayout() {
        return layout;
    }

    public User getUser() {
        return user;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public PortletURL getPortletURL() {
        if (requestInfo != null) {
            return requestInfo.getPortletURL();
        }
        return null;
    }

    public Theme getTheme() {
        return theme;
    }

    public long getLanguageId() {
        return languageId;
    }
}
