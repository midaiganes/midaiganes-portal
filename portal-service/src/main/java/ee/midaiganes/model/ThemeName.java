package ee.midaiganes.model;

public class ThemeName extends ContextAndName {
	private static final long serialVersionUID = 1L;

	public ThemeName(ThemeName themeName) {
		super(themeName);
	}

	public ThemeName(String context, String name) {
		super(context, name);
	}

	public ThemeName(String fullName) {
		super(fullName);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ThemeName && super.equals(o);
	}
}
