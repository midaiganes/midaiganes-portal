package ee.midaiganes.beans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanRepositoryUtil {
    private static BeanRepository beanRepository;

    static {
        setBeanRepository(new BeanRepositoryImpl());
    }

    public static void setBeanRepository(BeanRepository beanRepository) {
        BeanRepositoryUtil.beanRepository = beanRepository;
    }

    public static <A> A getBean(Class<A> clazz) {
        return beanRepository.getBean(clazz);
    }

    public static <A> void register(Class<A> clazz, A bean) {
        beanRepository.register(clazz, bean);
    }

    public static interface BeanRepository {
        <A> void register(Class<A> clazz, A impl);

        <A> A getBean(Class<A> clazz);
    }

    public static class BeanRepositoryImpl implements BeanRepository {
        private static final Logger log = LoggerFactory.getLogger(BeanRepositoryImpl.class);
        private final Map<Class<?>, Object> beans = new ConcurrentHashMap<>();

        @Override
        public <A> A getBean(Class<A> clazz) {
            @SuppressWarnings("unchecked")
            A bean = (A) beans.get(clazz);
            if (bean == null) {
                throw new BeanNotFoundException(clazz);
            }
            return bean;
        }

        @Override
        public <A> void register(Class<A> clazz, A bean) {
            log.debug("Adding bean with class '{}' and actual class '{}'", clazz, bean.getClass());
            beans.put(clazz, bean);
        }

        private static class BeanNotFoundException extends RuntimeException {
            private static final long serialVersionUID = 1L;

            public BeanNotFoundException(Class<?> clazz) {
                super("Bean '" + clazz + "' not found!");
            }
        }

    }
}
