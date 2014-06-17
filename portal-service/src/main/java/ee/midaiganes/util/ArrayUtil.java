package ee.midaiganes.util;

import javax.annotation.Nonnull;

public class ArrayUtil {
    @Nonnull
    public static long[] toPrimitiveLongArray(final String str) {
        if (!StringUtil.isEmpty(str)) {
            final String[] strs = str.split(",");
            final long[] longs = new long[strs.length];
            for (int i = 0; i < strs.length; i++) {
                longs[i] = Long.parseLong(strs[i]);
            }
            return longs;
        }
        return new long[0];
    }
}
