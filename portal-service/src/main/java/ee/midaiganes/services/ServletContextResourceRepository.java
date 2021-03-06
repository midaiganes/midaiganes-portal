package ee.midaiganes.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.servlet.ServletContext;

import com.google.common.collect.ImmutableList;

/**
 * TODO deprecated?
 * 
 * @see javax.servlet.ServletContext.getResourcePaths(String)
 * 
 */
public class ServletContextResourceRepository {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, CopyOnWriteArrayList<String>>> resourcePaths;
    private final ReentrantReadWriteLock lock;
    private final ReadLock readLock;
    private final WriteLock writeLock;

    public ServletContextResourceRepository() {
        resourcePaths = new ConcurrentHashMap<>();
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public void contextDestroyed(ServletContext ctx) {
        writeLock.lock();
        try {
            resourcePaths.remove(ctx.getContextPath());
        } finally {
            writeLock.unlock();
        }
    }

    public ImmutableList<String> getContextResourcePaths(ServletContext ctx, String path) {
        readLock.lock();
        try {
            resourcePaths.putIfAbsent(ctx.getContextPath(), new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>());
            ConcurrentHashMap<String, CopyOnWriteArrayList<String>> resources = resourcePaths.get(ctx.getContextPath());
            CopyOnWriteArrayList<String> list = resources.get(path);
            if (list != null) {
                return ImmutableList.copyOf(list);
            }
        } finally {
            readLock.unlock();
        }
        return getOrCreateContextResourcePaths(ctx, path);
    }

    private ImmutableList<String> getOrCreateContextResourcePaths(ServletContext ctx, String path) {
        writeLock.lock();
        try {
            resourcePaths.putIfAbsent(ctx.getContextPath(), new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>());
            ConcurrentHashMap<String, CopyOnWriteArrayList<String>> resources = resourcePaths.get(ctx.getContextPath());
            CopyOnWriteArrayList<String> list = resources.get(path);
            if (list == null) {
                list = new CopyOnWriteArrayList<>(ctx.getResourcePaths(path));
                CopyOnWriteArrayList<String> previous = resources.putIfAbsent(path, list);
                if (previous != null) {
                    previous.addAll(list);
                }
            }
            return ImmutableList.copyOf(list);
        } finally {
            writeLock.unlock();
        }
    }
}
