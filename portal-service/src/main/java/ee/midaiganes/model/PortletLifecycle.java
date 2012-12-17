package ee.midaiganes.model;

public enum PortletLifecycle {
	RENDER(0), ACTION(1), RESOURCE(2);

	private final int i;

	private PortletLifecycle(int i) {
		this.i = i;
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
		return Integer.toString(i);
	}

	public static PortletLifecycle getLifecycle(String i, PortletLifecycle def) {
		PortletLifecycle plc = getLifecycle(Integer.parseInt(i));
		return plc != null ? plc : def;
	}
}
