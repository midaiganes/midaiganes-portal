package ee.midaiganes.portal.portletinstance;

import java.io.Serializable;

import ee.midaiganes.model.PortalResource;

public class PortletInstance implements Serializable, PortalResource {
	private static final long serialVersionUID = 1L;

	private final long id;
	private final PortletNamespace portletNamespace;

	public PortletInstance(long id, PortletNamespace portletNamespace) {
		this.id = id;
		this.portletNamespace = portletNamespace;
	}

	@Override
	public long getId() {
		return id;
	}

	public PortletNamespace getPortletNamespace() {
		return portletNamespace;
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
