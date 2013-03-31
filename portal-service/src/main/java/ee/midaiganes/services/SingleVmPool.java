package ee.midaiganes.services;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ee.midaiganes.util.PropsValues;

public class SingleVmPool {
	public static Cache getCache(String name) {
		return new Cache();
	}

	public static class Cache {
		private final ReadWriteLock lock = new ReentrantReadWriteLock();

		public static class Element {
			private final Object value;
			private final long destroyTime;

			private Element(Object value) {
				this.value = value;
				destroyTime = value == null ? System.currentTimeMillis() + 60000 : 0;
			}

			public <T> T get() {
				@SuppressWarnings("unchecked")
				T val = (T) value;
				return val;
			}
		}

		private static final class CacheMap extends HashMap<String, Element> {
			private static final long serialVersionUID = 1L;
		};

		private Cache() {
		}

		private final CacheMap cache = new CacheMap();

		public void put(String key, Object value) {
			if (!PropsValues.CACHE_DISABLED) {
				lock.writeLock().lock();
				try {
					cache.put(key, new Element(value));
				} finally {
					lock.writeLock().unlock();
				}
			}
		}

		public <T> T get(String key) {
			Element el = getElement(key);
			return el != null ? el.<T> get() : null;
		}

		public Element getElement(String key) {
			Element el = null;
			try {
				lock.readLock().lock();
				el = cache.get(key);
			} finally {
				lock.readLock().unlock();
			}
			if (el != null && el.destroyTime != 0 && el.destroyTime < System.currentTimeMillis()) {
				remove(key);
				return null;
			}
			return el;
		}

		public Element remove(String key) {
			try {
				lock.writeLock().lock();
				return cache.remove(key);
			} finally {
				lock.writeLock().unlock();
			}
		}

		public void clear() {
			try {
				lock.writeLock().lock();
				cache.clear();
			} finally {
				lock.writeLock().unlock();
			}
		}
	}
}
