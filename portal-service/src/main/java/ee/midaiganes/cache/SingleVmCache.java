package ee.midaiganes.cache;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ee.midaiganes.util.PropsValues;
import ee.midaiganes.util.TimeProviderUtil;

public class SingleVmCache implements SingleVmCacheMBean {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock read = lock.readLock();
    private final Lock write = lock.writeLock();
    private final SingleVmCache.CacheMap cache = new CacheMap();
    private final AtomicLong numberOfGets = new AtomicLong();
    private final AtomicLong numberOfPuts = new AtomicLong();
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();

    private static final class CacheMap extends HashMap<String, Element> {
        private static final long serialVersionUID = 1L;
    }

    public SingleVmCache() {
    }

    public void destroy() {
        clear();
    }

    public void put(String key, Object value) {
        if (!PropsValues.CACHE_DISABLED) {
            write.lock();
            try {
                numberOfPuts.incrementAndGet();
                cache.put(key, new Element(value));
            } finally {
                write.unlock();
            }
        }
    }

    public <T> T get(String key) {
        Element el = getElement(key);
        return el != null ? el.<T> get() : null;
    }

    public Element getElement(String key) {
        Element el = readElement(key);
        if (isElementInvalid(el)) {
            remove(key);
            return null;
        }
        return el;
    }

    private Element readElement(String key) {
        read.lock();
        try {
            numberOfGets.incrementAndGet();
            Element el = cache.get(key);
            if (el != null) {
                cacheHits.incrementAndGet();
            } else {
                cacheMisses.incrementAndGet();
            }
            return el;
        } finally {
            read.unlock();
        }
    }

    private static boolean isElementInvalid(Element el) {
        return el != null && el.getDestroyTime() != 0 && el.getDestroyTime() < TimeProviderUtil.currentTimeMillis();
    }

    public Element remove(String key) {
        write.lock();
        try {
            return cache.remove(key);
        } finally {
            write.unlock();
        }
    }

    public void clear() {
        write.lock();
        try {
            cache.clear();
        } finally {
            write.unlock();
        }
    }

    @Override
    public long getNumberOfGets() {
        return numberOfGets.get();
    }

    @Override
    public long getCacheSize() {
        read.lock();
        try {
            return cache.size();
        } finally {
            read.unlock();
        }
    }

    @Override
    public long getNumberOfPuts() {
        return numberOfPuts.get();
    }

    @Override
    public long getCacheHits() {
        return cacheHits.get();
    }

    @Override
    public long getCacheMisses() {
        return cacheMisses.get();
    }
}