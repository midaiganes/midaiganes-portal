package ee.midaiganes.portal.layoutset;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.portal.theme.ThemeName;

public class LayoutSetRepository {
    private static final Logger log = LoggerFactory.getLogger(LayoutSetRepository.class);

    private static final String GET_LAYOUT_SETS_CACHE_KEY = "getLayoutSets";
    private static final String GET_LAYOUT_SET_BY_VIRTUAL_HOST_CACHE_KEY_PREFIX = "getLayoutSet#";

    private final SingleVmCache cache;
    private final LayoutSetDao layoutSetDao;

    @Inject
    public LayoutSetRepository(LayoutSetDao layoutSetDao) {
        this.layoutSetDao = layoutSetDao;
        this.cache = SingleVmPoolUtil.getCache(LayoutSetRepository.class.getName());
    }

    public List<LayoutSet> getLayoutSets() {
        Element el = cache.getElement(GET_LAYOUT_SETS_CACHE_KEY);
        List<LayoutSet> layoutSets = el != null ? el.<List<LayoutSet>> get() : null;
        if (layoutSets == null) {
            layoutSets = layoutSetDao.getLayoutSets();
            cache.put(GET_LAYOUT_SETS_CACHE_KEY, layoutSets);
        }
        return layoutSets;
    }

    @Nullable
    public LayoutSet getLayoutSet(String virtualHost) {
        String cacheKey = GET_LAYOUT_SET_BY_VIRTUAL_HOST_CACHE_KEY_PREFIX + virtualHost;
        Element el = cache.getElement(cacheKey);
        LayoutSet layoutSet = el != null ? el.<LayoutSet> get() : null;
        if (el == null) {
            List<LayoutSet> list = layoutSetDao.getLayoutSet(virtualHost);
            layoutSet = list.isEmpty() ? null : list.get(0);
            // TODO mem leak?
            cache.put(cacheKey, layoutSet);
        }
        return layoutSet;
    }

    public LayoutSet getLayoutSet(long id) {
        for (LayoutSet ls : getLayoutSets()) {
            if (ls.getId() == id) {
                return ls;
            }
        }
        return null;
    }

    public long addLayoutSet(String virtualHost, ThemeName themeName) {
        try {
            return layoutSetDao.addLayoutSet(virtualHost, themeName);
        } finally {
            cache.clear();
        }
    }

    public void updateLayoutSet(long id, String virtualHost, ThemeName themeName) {
        try {
            layoutSetDao.updateLayoutSet(id, virtualHost, themeName);
        } finally {
            cache.clear();
        }
    }

    public LayoutSet getDefaultLayoutSet(String virtualHost) {
        log.warn("get default layout set: {}", virtualHost);
        return LayoutSet.getDefault(virtualHost);
    }
}
