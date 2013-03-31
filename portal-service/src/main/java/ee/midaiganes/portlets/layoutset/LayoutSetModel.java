package ee.midaiganes.portlets.layoutset;

import java.io.Serializable;

public class LayoutSetModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private String id;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
