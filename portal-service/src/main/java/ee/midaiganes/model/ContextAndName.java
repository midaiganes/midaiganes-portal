package ee.midaiganes.model;

import java.io.Serializable;

import ee.midaiganes.util.StringPool;

public class ContextAndName implements Serializable {
	private static final long serialVersionUID = 1L;
	protected static final String SEPARATOR = "_w_";
	private final String context;
	private final String contextWithSlash;
	private final String name;
	private final String fullName;

	public ContextAndName(String context, String name) {
		if (context == null || name == null) {
			throw new IllegalArgumentException("contex='" + context + "'; name='" + name + "'");
		}
		if (context.startsWith(StringPool.SLASH)) {
			throw new IllegalArgumentException("context must not start with '/'; context = '" + context + "'");
		}
		this.context = context;
		this.name = name;
		this.contextWithSlash = StringPool.SLASH + context;
		this.fullName = this.context + SEPARATOR + this.name;
	}

	public ContextAndName(String fullName) {
		this(fullName.split(SEPARATOR, 2));
	}

	public ContextAndName(String[] fullName) {
		this(fullName[0], fullName[1]);
	}

	public String getContext() {
		return context;
	}

	public String getContextWithSlash() {
		return contextWithSlash;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}

	public static boolean isValidFullName(String fullName) {
		return fullName != null && fullName.split(SEPARATOR, 2).length == 2 && !fullName.startsWith(StringPool.SLASH);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ContextAndName && equals((ContextAndName) o, this);
	}

	private static boolean equals(ContextAndName n, ContextAndName m) {
		return n != null && m.context.equals(n.context) && m.name.equals(n.name);
	}

	@Override
	public int hashCode() {
		return context.hashCode() + name.hashCode();
	}

	@Override
	public String toString() {
		return "ContextAndName [context=" + context + ", name=" + name + "]";
	}
}
