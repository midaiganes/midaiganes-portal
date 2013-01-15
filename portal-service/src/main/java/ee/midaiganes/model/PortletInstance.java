package ee.midaiganes.model;

import java.io.Serializable;

public class PortletInstance implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private PortletName portletName;
	private String windowID;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PortletName getPortletName() {
		return portletName;
	}

	public void setPortletName(PortletName portletName) {
		this.portletName = portletName;
	}

	public String getWindowID() {
		return windowID;
	}

	public void setWindowID(String windowID) {
		this.windowID = windowID;
	}

	@Override
	public String toString() {
		return "PortletInstance [id=" + id + ", portletName=" + portletName + ", windowID=" + windowID + "]";
	}
}
