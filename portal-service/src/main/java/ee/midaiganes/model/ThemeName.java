package ee.midaiganes.model;


public class ThemeName extends ContextAndName {
	private static final long serialVersionUID = 1L;

	public ThemeName(String context, String name) {
		super(context, name);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ThemeName && super.equals(o);
	}
}
