package ee.midaiganes.model;

import java.io.Serializable;

public class PortletInstance implements Serializable, PortalResource {
	private static final long serialVersionUID = 1L;

	private long id;
	private PortletNamespace portletNamespace;

	@Override
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
	public String getResource() {
		return getResourceName();
	}

	public static String getResourceName() {
		return PortletInstance.class.getName();
	}

	@Override
	public String toString() {
		return "PortletInstance [id=" + id + ", portletNamespace=" + portletNamespace + "]";
	}
}
