package ee.midaiganes.model;

import java.io.Serializable;

public class PortletInstance implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private PortletNamespace portletNamespace;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PortletNamespace getPortletNamespace() {
		return portletNamespace;
	}

	public void setPortletNamespace(PortletNamespace portletNamespace) {
		this.portletNamespace = portletNamespace;
	}

	@Override
	public String toString() {
		return "PortletInstance [id=" + id + ", portletNamespace=" + portletNamespace + "]";
	}
}
