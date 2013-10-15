package ee.midaiganes.util;

import java.util.List;

public class ArrayUtil {
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

	public static long[] toPrimitivLongArray(List<Long> list) {
		long[] array = new long[list.size()];
		int i = 0;
		for (Long l : list) {
			array[i++] = l.longValue();
		}
		return array;
	}

	public static boolean contains(final long[] longs, final long l) {
		for (long ll : longs) {
			if (ll == l) {
				return true;
			}
		}
		return false;
	}
}
