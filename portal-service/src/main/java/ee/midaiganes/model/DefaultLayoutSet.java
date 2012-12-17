package ee.midaiganes.model;

public class DefaultLayoutSet extends LayoutSet {

	private static final long serialVersionUID = 1L;
	public static final long DEFAULT_LAYOUT_SET_ID = 0;

	public DefaultLayoutSet(String virtualHost) {
		super.setVirtualHost(virtualHost);
	}

	@Override
	public long getId() {
		return DEFAULT_LAYOUT_SET_ID;
	}

	@Override
	public void setVirtualHost(String virtualHost) {
	}

	@Override
	public boolean isDefault() {
		return true;
	}
}
