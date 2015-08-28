package ee.midaiganes.util;

import java.util.List;

import javax.annotation.Nullable;

public class CollectionUtil {
    @Nullable
    public static <A> A getFirstElementOrNull(@Nullable List<A> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }
}
