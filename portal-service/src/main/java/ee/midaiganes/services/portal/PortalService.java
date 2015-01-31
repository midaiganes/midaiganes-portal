package ee.midaiganes.services.portal;

import java.util.Locale;

import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layoutset.LayoutSet;
import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.portal.user.User;

public interface PortalService {
    GetRequestedPageResponse getRequestedPage(GetRequestedPageRequest request);

    class GetRequestedPageRequest {
        private final String serverName;
        private final long userId;
        private final String friendlyUrl;
        private final Locale locale;

        public GetRequestedPageRequest(String serverName, long userId, String friendlyUrl, Locale locale) {
            this.serverName = serverName;
            this.userId = userId;
            this.friendlyUrl = friendlyUrl;
            this.locale = locale;
        }

        public String getServerName() {
            return serverName;
        }

        public long getUserId() {
            return userId;
        }

        public String getFriendlyUrl() {
            return friendlyUrl;
        }

        public Locale getLocale() {
            return locale;
        }
    }

    class GetRequestedPageResponse {
        private LayoutSet layoutSet;
        private User user;
        private Layout layout;
        private Theme theme;
        private long languageId;

        public LayoutSet getLayoutSet() {
            return layoutSet;
        }

        protected void setLayoutSet(LayoutSet layoutSet) {
            this.layoutSet = layoutSet;
        }

        public User getUser() {
            return user;
        }

        protected void setUser(User user) {
            this.user = user;
        }

        public Layout getLayout() {
            return layout;
        }

        protected void setLayout(Layout layout) {
            this.layout = layout;
        }

        public Theme getTheme() {
            return theme;
        }

        protected void setTheme(Theme theme) {
            this.theme = theme;
        }

        public long getLanguageId() {
            return languageId;
        }

        public void setLanguageId(long languageId) {
            this.languageId = languageId;
        }
    }
}
