package ee.midaiganes.model;

import java.io.Serializable;

public class LayoutSet implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String virtualHost;
	private ThemeName themeName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVirtualHost() {
		return virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
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

}
