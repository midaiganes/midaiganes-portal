package ee.midaiganes.model;

public class PageLayoutName extends ContextAndName {
	private static final long serialVersionUID = 1L;

	public PageLayoutName(String context, String name) {
		super(context, name);
	}

	public PageLayoutName(String fullName) {
		super(fullName);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof PageLayoutName) && super.equals(o);
	}
}
