package ee.midaiganes.util;

import javax.annotation.Nullable;

import com.google.common.primitives.Longs;

public class GetterUtil {
    public static long get(@Nullable String str, long defaultValue) {
        Long val = str == null ? null : Longs.tryParse(str);
        return val == null ? defaultValue : val.longValue();
    }

    public static <A> A get(A a, A def) {
        return a != null ? a : def;
    }
}
