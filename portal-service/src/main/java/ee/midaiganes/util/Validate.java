package ee.midaiganes.util;

public class Validate {
	public static void notNull(Object obj, String msg) {
		if (obj == null) {
			throw new IllegalArgumentException(msg);
		}
	}
}
