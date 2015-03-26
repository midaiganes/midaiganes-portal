package ee.midaiganes.fn;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public class Optionals {

    public static <T> Function<Optional<T>, T> get() {
        return new Function<Optional<T>, T>() {
            @Override
            @Nullable
            public T apply(@Nullable Optional<T> input) {
                return input.get();
            }
        };
    }

    public static <T> Function<T, Optional<T>> of() {
        return new Function<T, Optional<T>>() {
            @Override
            @Nullable
            public Optional<T> apply(@Nullable T input) {
                return Optional.of(input);
            }
        };
    }
}
