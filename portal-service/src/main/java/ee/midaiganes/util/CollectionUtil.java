package ee.midaiganes.util;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

public class CollectionUtil {
    @Nullable
    public static <A> A getFirstElement(@Nullable List<A> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    @Nullable
    public static <A> A getFirstElement(@Nullable Collection<A> list) {
        return list != null && !list.isEmpty() ? list.iterator().next() : null;
    }
}
