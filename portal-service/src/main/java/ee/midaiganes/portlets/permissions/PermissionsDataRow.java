package ee.midaiganes.portlets.permissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PermissionsDataRow implements Serializable {
	private static final long serialVersionUID = 1L;
	private long resourcePrimKey;
	private String resourceText;
	private List<Boolean> permissions;

	public long getResourcePrimKey() {
		return resourcePrimKey;
	}

	public void setResourcePrimKey(long resourcePrimKey) {
		this.resourcePrimKey = resourcePrimKey;
	}

	public String getResourceText() {
		return resourceText;
	}

	public void setResourceText(String resourceText) {
		this.resourceText = resourceText;
	}

	public List<Boolean> getPermissions() {
		if (permissions == null) {
			permissions = new ArrayList<>();
		}
		return permissions;
	}

	public void setPermissions(List<Boolean> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return "PermissionsDataRow [resourcePrimKey=" + resourcePrimKey + ", resourceText=" + resourceText + ", permissions=" + permissions + "]";
	}
}