package ee.midaiganes.model;

import java.io.Serializable;

public class LayoutPortlet implements Serializable {
	private static final long serialVersionUID = 1L;

	private final long id;
	private final long portletInstanceId;
	private final long layoutId;
	private final long rowId;
	private final PortletInstance portletInstance;

	public LayoutPortlet(long id, long portletInstanceId, long layoutId, long rowId, PortletInstance portletInstance) {
		this.id = id;
		this.portletInstanceId = portletInstanceId;
		this.layoutId = layoutId;
		this.rowId = rowId;
		this.portletInstance = portletInstance;
	}

	public long getId() {
		return id;
	}

	public long getPortletInstanceId() {
		return portletInstanceId;
	}

	public long getLayoutId() {
		return layoutId;
	}

	public long getRowId() {
		return rowId;
	}

	public PortletInstance getPortletInstance() {
		return portletInstance;
	}

	@Override
	public String toString() {
		return "LayoutPortlet [id=" + id + ", portletInstanceId=" + portletInstanceId + ", layoutId=" + layoutId + ", rowId=" + rowId + ", portletInstance="
				+ portletInstance + "]";
	}
}
