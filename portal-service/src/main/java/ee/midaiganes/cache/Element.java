package ee.midaiganes.cache;

import ee.midaiganes.util.TimeProviderUtil;

public class Element {
    private final Object value;
    private final long destroyTime;

    Element(Object value) {
        this(value, value == null ? TimeProviderUtil.currentTimeMillis() + 60000 : 0);
    }

    Element(Object value, long destroyTime) {
        this.value = value;
        this.destroyTime = destroyTime;
    }

    public <T> T get() {
        @SuppressWarnings("unchecked")
        T val = (T) value;
        return val;
    }

    public long getDestroyTime() {
        return destroyTime;
    }
}