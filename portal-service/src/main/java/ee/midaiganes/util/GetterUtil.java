package ee.midaiganes.util;

public class GetterUtil {
	public static long get(String str, long defaultValue) {
		if (!StringUtil.isEmpty(str)) {
			try {
				return Long.parseLong(str);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}
}
