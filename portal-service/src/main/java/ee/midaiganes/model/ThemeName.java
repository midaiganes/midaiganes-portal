package ee.midaiganes.model;

import java.io.Serializable;

public class ThemeName extends ContextAndName implements Serializable {
	private static final long serialVersionUID = 1L;

	public ThemeName(String context, String name) {
		super(context, name);
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof ThemeName && super.equals(o);
	}
}
