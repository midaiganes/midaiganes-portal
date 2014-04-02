package ee.midaiganes.portal.layoutset;

import java.io.Serializable;

import com.google.common.base.Preconditions;

import ee.midaiganes.model.PortalResource;
import ee.midaiganes.portal.theme.ThemeName;

public final class LayoutSet implements Serializable, PortalResource {
    private static final long serialVersionUID = 1L;
    public static final long DEFAULT_LAYOUT_SET_ID = 0;

    private final long id;
    private final String virtualHost;
    private final ThemeName themeName;

    private LayoutSet(String virtualHost) {
        Preconditions.checkNotNull(virtualHost, "Virtualhost is null");
        this.id = DEFAULT_LAYOUT_SET_ID;
        this.virtualHost = virtualHost;
        this.themeName = null;
    }

    public LayoutSet(long id, String virtualHost, ThemeName themeName) {
        Preconditions.checkArgument(id != DEFAULT_LAYOUT_SET_ID, "Default layout set id not allowed");
        Preconditions.checkNotNull(virtualHost, "Virtualhost is null");
        this.id = id;
        this.virtualHost = virtualHost;
        this.themeName = themeName;
    }

    public static LayoutSet getDefault(String virtualHost) {
        return new LayoutSet(virtualHost);
    }

    @Override
    public long getId() {
        return id;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public boolean isDefault() {
        return DEFAULT_LAYOUT_SET_ID == id;
    }

    public ThemeName getThemeName() {
        return themeName;
    }

    @Override
    public String getResource() {
        return LayoutSet.class.getName();
    }
}
