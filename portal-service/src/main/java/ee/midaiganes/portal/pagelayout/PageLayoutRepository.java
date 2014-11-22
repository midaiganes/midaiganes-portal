package ee.midaiganes.portal.pagelayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import ee.midaiganes.generated.xml.pagelayout.MidaiganesLayout;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.XmlUtil;

public class PageLayoutRepository {
    private static final Logger log = LoggerFactory.getLogger(PageLayoutRepository.class);
    private final ConcurrentHashMap<PageLayoutName, PageLayout> pageLayouts = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public List<PageLayout> getPageLayouts() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(pageLayouts.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nullable
    public PageLayout getPageLayout(String pageLayoutId) {
        return getPageLayout(new PageLayoutName(pageLayoutId));
    }

    @Nullable
    public PageLayout getPageLayout(PageLayoutName pageLayoutName) {
        lock.readLock().lock();
        try {
            PageLayout pl = pageLayouts.get(pageLayoutName);
            if (pl != null) {
                return pl;
            }
        } finally {
            lock.readLock().unlock();
        }
        if (log.isWarnEnabled()) {
            log.warn("no pageLayout with name '" + pageLayoutName + "'; All pageLayouts = " + getPageLayouts());
        }
        return null;
    }

    public PageLayout getDefaultPageLayout() {
        lock.readLock().lock();
        try {
            Enumeration<PageLayout> e = this.pageLayouts.elements();
            if (e.hasMoreElements()) {
                return e.nextElement();
            }
            throw new IllegalStateException("no page layouts found");
        } finally {
            lock.readLock().unlock();
        }
    }

    private void registerPageLayout(PageLayout layout) {
        lock.writeLock().lock();
        try {
            this.pageLayouts.put(layout.getPageLayoutName(), layout);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private PageLayout getPageLayoutFromMidaiganesLayout(String context, MidaiganesLayout.Layout l) {
        String path = l.getPath();
        Preconditions.checkNotNull(path);
        return new PageLayout(new PageLayoutName(context, l.getId()), path);
    }

    public void registerPageLayouts(String contextPath, InputStream pageLayoutXmlStream) {
        try {
            MidaiganesLayout pageLayouts = XmlUtil.unmarshalWithoutJAXBElement(MidaiganesLayout.class, pageLayoutXmlStream);
            log.info("contextPath = {}, pageLayouts = {}", contextPath, pageLayouts);
            if (pageLayouts != null) {
                String contextPathWithoutFirstSlash = contextPath.startsWith(StringPool.SLASH) ? contextPath.substring(1) : contextPath;
                for (MidaiganesLayout.Layout layout : pageLayouts.getLayout()) {
                    registerPageLayout(getPageLayoutFromMidaiganesLayout(contextPathWithoutFirstSlash, layout));
                }
            }
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void unregisterPageLayouts(String contextPath) {
        for (PageLayout pageLayout : getPageLayouts()) {
            if (pageLayout.getPageLayoutName().getContextWithSlash().equals(contextPath)) {
                lock.writeLock().lock();
                try {
                    pageLayouts.remove(pageLayout.getPageLayoutName());
                } finally {
                    lock.writeLock().unlock();
                }
                log.info("Page layout removed: {}", pageLayout);
            }
        }
    }
}
