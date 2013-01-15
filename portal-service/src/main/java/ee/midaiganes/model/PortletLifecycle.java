package ee.midaiganes.model;

import ee.midaiganes.util.GetterUtil;

public enum PortletLifecycle {
	RENDER(0), ACTION(1), RESOURCE(2);

	private final int i;
	private final String string;

	private PortletLifecycle(int i) {
		this.i = i;
		this.string = Integer.toString(i);
	}

	private static PortletLifecycle getLifecycle(int i) {
		for (PortletLifecycle plc : PortletLifecycle.values()) {
			if (plc.i == i) {
				return plc;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return string;
	}

	public static PortletLifecycle getLifecycle(String i, PortletLifecycle def) {
		return GetterUtil.get(getLifecycle(Integer.parseInt(i)), def);
	}
}
