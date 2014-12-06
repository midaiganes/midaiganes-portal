package ee.midaiganes.model;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import ee.midaiganes.util.StringPool;

public class ContextAndName implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = "_w_";
    @Nonnull
    private final String context;
    private final String contextWithSlash;
    @Nonnull
    private final String name;
    private final String fullName;

    public ContextAndName(ContextAndName contextAndName) {
        this(contextAndName.context, contextAndName.name);
    }

    public ContextAndName(String context, String name) {
        Preconditions.checkNotNull(context, "Context is null");
        Preconditions.checkNotNull(name, "Name is null");
        Preconditions.checkArgument(context.length() == 0 || context.charAt(0) != '/', "context must not start with '/'; context = '%s'", context);
        this.context = context;
        this.name = name;
        this.contextWithSlash = StringPool.SLASH + context;
        this.fullName = this.context + SEPARATOR + this.name;
    }

    public ContextAndName(String fullName) {
        this(validateAndGet(split(fullName)));
    }

    protected static String[] split(String fullName) {
        int length = fullName.length();
        StringBuilder ctx = new StringBuilder(length);
        StringBuilder name = new StringBuilder(length);
        boolean contextFound = false;
        final char underscore = SEPARATOR.charAt(0);
        final char w = SEPARATOR.charAt(1);
        for (int i = 0; i < length;) {
            char c = fullName.charAt(i);
            if (!contextFound) {
                if (c != underscore) {
                    ctx.append(c);
                } else if (i + 2 < length && fullName.charAt(i + 1) == w && fullName.charAt(i + 2) == underscore) {
                    contextFound = true;
                    i += 2;
                }
            } else {
                name.append(c);
            }
            i++;
        }
        return contextFound ? new String[] { ctx.toString(), name.toString() } : new String[] { fullName };
    }

    private ContextAndName(String[] fullName) {
        this(fullName[0], fullName[1]);
    }

    private static String[] validateAndGet(String[] fullName) {
        Preconditions.checkNotNull(fullName, "Full name is null");
        if (fullName.length == 0) {
            throw new IllegalArgumentException("Full name lenght is 0");
        } else if (fullName.length == 1) {
            throw new IllegalArgumentException("Full name lenght is 1. Full name is '" + fullName[0] + "'");
        }
        return fullName;
    }

    @Nonnull
    public String getContext() {
        return context;
    }

    public String getContextWithSlash() {
        return contextWithSlash;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public static boolean isValidFullName(String fullName) {
        return fullName != null && split(fullName).length == 2 && fullName.charAt(0) != '/';
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ContextAndName && ContextAndName.equals((ContextAndName) o, this);
    }

    private static boolean equals(ContextAndName n, ContextAndName _this) {
        return n == _this || (n != null && _this.context.equals(n.context) && _this.name.equals(n.name));
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
