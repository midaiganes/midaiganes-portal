package ee.midaiganes.util;

public class LongUtil {
	public static boolean isNonNegativeLong(String s) {
		if (s == null) {
			return false;
		}
		int radix = 10;
		long result = 0;
		int i = 0, len = s.length();
		long multmin;
		int digit;
		if (len == 0) {
			return false;
		}
		char firstChar = s.charAt(0);
		if (firstChar < '0') {
			return false;
		}
		long limit = -Long.MAX_VALUE;
		multmin = limit / radix;
		while (i < len) {
			// Accumulating negatively avoids surprises near MAX_VALUE
			digit = Character.digit(s.charAt(i++), radix);
			if (digit < 0) {
				return false;
			}
			if (result < multmin) {
				return false;
			}
			result *= radix;
			if (result < limit + digit) {
				return false;
			}
			result -= digit;
		}
		return result <= 0;
	}
}
