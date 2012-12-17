package ee.midaiganes.services;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SingleVmPool {
	public static Cache getCache(String name) {
		return new Cache();
	}

	public static class Cache {
		private final ReadWriteLock lock = new ReentrantReadWriteLock();

		public static class Element {
			private final Object value;

			private Element(Object value) {
				this.value = value;
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
			lock.writeLock().lock();
			try {
				cache.put(key, new Element(value));
			} finally {
				lock.writeLock().unlock();
			}
		}

		public <T> T get(String key) {
			try {
				lock.readLock().lock();
				Element el = cache.get(key);
				@SuppressWarnings("unchecked")
				T value = el != null ? (T) el.get() : null;
				return value;
			} finally {
				lock.readLock().unlock();
			}
		}

		public Element getElement(String key) {
			try {
				lock.readLock().lock();
				return cache.get(key);
			} finally {
				lock.readLock().unlock();
			}
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
