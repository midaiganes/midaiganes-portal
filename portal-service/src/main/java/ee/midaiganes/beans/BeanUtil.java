package ee.midaiganes.beans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtil {
	private static Bean beanImpl;

	static {
		setBeanImpl(new BeanImpl());
	}

	public static void setBeanImpl(Bean beanImpl) {
		BeanUtil.beanImpl = beanImpl;
	}

	public static <A> A getBean(Class<A> clazz) {
		return beanImpl.getBean(clazz);
	}

	public static <A> void addBean(Class<A> clazz, A bean) {
		beanImpl.addBean(clazz, bean);
	}

	public interface Bean {
		<A> A getBean(Class<A> clazz);

		<A> void addBean(Class<A> clazz, A bean);
	}

	public static class BeanImpl implements Bean {
		private static final Logger log = LoggerFactory.getLogger(BeanImpl.class);
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
		public <A> void addBean(Class<A> clazz, A bean) {
			log.debug("Adding bean with class '{}' and actual class '{}'", clazz, bean.getClass());
			beans.put(clazz, bean);
		}

		public static class BeanNotFoundException extends RuntimeException {
			private static final long serialVersionUID = 1L;

			public BeanNotFoundException(Class<?> clazz) {
				super("Bean '" + clazz + "' not found!");
			}
		}

	}
}
