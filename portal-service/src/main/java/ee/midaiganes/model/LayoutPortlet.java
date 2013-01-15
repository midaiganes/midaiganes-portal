package ee.midaiganes.model;

import java.io.Serializable;

public class LayoutPortlet implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long portletInstanceId;
	private long layoutId;
	private long rowId;
	private PortletInstance portletInstance;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPortletInstanceId() {
		return portletInstanceId;
	}

	public void setPortletInstanceId(long portletInstanceId) {
		this.portletInstanceId = portletInstanceId;
	}

	public long getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(long layoutId) {
		this.layoutId = layoutId;
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public PortletInstance getPortletInstance() {
		return portletInstance;
	}

	public void setPortletInstance(PortletInstance portletInstance) {
		this.portletInstance = portletInstance;
	}

	@Override
	public String toString() {
		return "LayoutPortlet [id=" + id + ", portletInstanceId=" + portletInstanceId + ", layoutId=" + layoutId + ", rowId=" + rowId + ", portletInstance="
				+ portletInstance + "]";
	}
}
