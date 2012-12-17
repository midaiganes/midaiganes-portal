package ee.midaiganes.model;

import ee.midaiganes.util.PortalUtil;

public class DefaultLayout extends Layout {
	private static final long serialVersionUID = 1L;
	public static final long DEFAULT_LAYOUT_ID = 0;

	public DefaultLayout(long layoutSetId, String friendlyUrl) {
		super.setLayoutSetId(layoutSetId);
		super.setFriendlyUrl(friendlyUrl);
	}

	@Override
	public long getId() {
		return DEFAULT_LAYOUT_ID;
	}

	@Override
	public void setLayoutSetId(long layoutSetId) {
	}

	@Override
	public void setFriendlyUrl(String friendlyUrl) {
	}

	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public ThemeName getThemeName() {
		return new ThemeName(PortalUtil.getPortalContextPath(), "default");
	}
}
