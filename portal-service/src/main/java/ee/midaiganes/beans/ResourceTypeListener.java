package ee.midaiganes.beans;

import java.lang.reflect.Field;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class ResourceTypeListener implements TypeListener {
    private static final Logger log = LoggerFactory.getLogger(ResourceTypeListener.class);

    @Override
    public <I> void hear(TypeLiteral<I> type, final TypeEncounter<I> encounter) {
        for (final Field field : type.getRawType().getDeclaredFields()) {
            if (field.isAnnotationPresent(Resource.class)) {
                Provider<?> provider = encounter.getProvider(field.getType());
                encounter.register(new FieldMembersInjector<I>(field, provider));
            }
        }
    }

    private static class FieldMembersInjector<I> implements MembersInjector<I> {
        private final Field field;
        private final Provider<?> provider;

        public FieldMembersInjector(Field field, Provider<?> provider) {
            this.field = field;
            this.provider = provider;
        }

        @Override
        public void injectMembers(I instance) {
            field.setAccessible(true);
            try {
                field.set(instance, provider.get());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}