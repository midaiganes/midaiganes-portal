package ee.midaiganes.model;

public class DefaultLayout extends Layout {
	private static final long serialVersionUID = 1L;
	public static final long DEFAULT_LAYOUT_ID = 0;

	public DefaultLayout(long layoutSetId, String friendlyUrl, ThemeName themeName, String pageLayoutId) {
		super.setLayoutSetId(layoutSetId);
		super.setFriendlyUrl(friendlyUrl);
		super.setThemeName(themeName);
		super.setPageLayoutId(pageLayoutId);
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
}
