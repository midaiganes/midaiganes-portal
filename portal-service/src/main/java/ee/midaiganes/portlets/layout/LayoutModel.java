package ee.midaiganes.portlets.layout;

import java.io.Serializable;

public class LayoutModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
