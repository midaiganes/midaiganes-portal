package ee.midaiganes.util;

import java.util.List;

public class CollectionUtil {
	public static <A> A getFirstElement(List<A> list) {
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}
}
