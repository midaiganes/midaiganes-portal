package ee.midaiganes.model;

import java.io.Serializable;

public final class LayoutSet implements Serializable, PortalResource {
	private static final long serialVersionUID = 1L;
	public static final long DEFAULT_LAYOUT_SET_ID = 0;

	private final long id;
	private final String virtualHost;
	private final ThemeName themeName;

	private LayoutSet(String virtualHost) {
		if (virtualHost == null) {
			throw new IllegalArgumentException("Virtualhost is null");
		}
		this.id = DEFAULT_LAYOUT_SET_ID;
		this.virtualHost = virtualHost;
		this.themeName = null;
	}

	public LayoutSet(long id, String virtualHost, ThemeName themeName) {
		if (id == DEFAULT_LAYOUT_SET_ID) {
			throw new IllegalArgumentException("Default layout set id not allowed");
		}
		if (virtualHost == null) {
			throw new IllegalArgumentException("Virtualhost is null");
		}
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
