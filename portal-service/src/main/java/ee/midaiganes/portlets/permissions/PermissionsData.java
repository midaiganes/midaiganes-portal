package ee.midaiganes.portlets.permissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PermissionsData implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<PermissionsDataRow> rows;

	public List<PermissionsDataRow> getRows() {
		if (rows == null) {
			rows = new ArrayList<>();
		}
		return rows;
	}

	public void setRows(List<PermissionsDataRow> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "PermissionsData [rows=" + rows + "]";
	}
}
