package ee.midaiganes.portlets.layoutset;

import java.io.Serializable;

public class LayoutSetModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}