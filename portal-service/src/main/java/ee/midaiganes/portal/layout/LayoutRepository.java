package ee.midaiganes.portal.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;

import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.portal.pagelayout.PageLayoutName;
import ee.midaiganes.portal.pagelayout.PageLayoutRepository;
import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.portal.theme.ThemeName;
import ee.midaiganes.portal.theme.ThemeRepository;
import ee.midaiganes.services.exceptions.IllegalFriendlyUrlException;
import ee.midaiganes.services.exceptions.IllegalPageLayoutException;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

public class LayoutRepository {
    private static final Logger log = LoggerFactory.getLogger(LayoutRepository.class);
    private static final Pattern FRIENDLY_URL_PATTERN;
    static {
        FRIENDLY_URL_PATTERN = Pattern.compile("^\\/[a-zA-Z0-9_\\-]*$");
    }
    private final LayoutDao layoutDao;
    private final ThemeRepository themeRepository;
    private final PageLayoutRepository pageLayoutRepository;

    private final SingleVmCache cache;
    private final SingleVmCache layoutTitleCache;
    private final SingleVmCache layoutCache;

    @Inject
    public LayoutRepository(LayoutDao layoutDao, ThemeRepository themeRepository, PageLayoutRepository pageLayoutRepository) {
        this.layoutDao = layoutDao;
        this.themeRepository = themeRepository;
        this.pageLayoutRepository = pageLayoutRepository;
        this.cache = SingleVmPoolUtil.getCache(LayoutRepository.class.getName());
        this.layoutTitleCache = SingleVmPoolUtil.getCache(LayoutRepository.class.getName() + ".LayoutTitle");
        this.layoutCache = SingleVmPoolUtil.getCache(LayoutRepository.class.getName() + ".Layout");
    }

    public List<LayoutTitle> getLayoutTitles(long layoutId) {
        List<LayoutTitle> list = getLayoutTitlesFromCache(layoutId);
        if (list == null) {
            return loadAndCacheLayoutTitles(layoutId);
        }
        return list;
    }

    @Nullable
    private List<LayoutTitle> getLayoutTitlesFromCache(long layoutId) {
        Element el = layoutTitleCache.getElement(Long.toString(layoutId));
        return el != null ? el.<List<LayoutTitle>> get() : null;
    }

    private List<LayoutTitle> loadAndCacheLayoutTitles(long layoutId) {
        List<LayoutTitle> list = null;
        try {
            list = ImmutableList.copyOf(layoutDao.loadLayoutTitles(layoutId));
        } finally {
            list = list == null ? Collections.<LayoutTitle> emptyList() : list;
            layoutTitleCache.put(Long.toString(layoutId), list);
        }
        return list;
    }

    @Nullable
    public Layout getLayout(long layoutId) {
        Element el = layoutCache.getElement(Long.toString(layoutId));
        if (el != null) {
            return el.get();
        }
        Layout layout = null;
        try {
            layout = layoutDao.loadLayout(layoutId);
            if (layout != null) {
                layout = layout.withLayoutTitles(getLayoutTitles(layoutId));
            }
        } finally {
            layoutCache.put(Long.toString(layoutId), layout);
        }
        return layout;
    }

    public List<Layout> getLayouts(long[] layoutIds) {
        if (layoutIds != null) {
            List<Layout> list = new ArrayList<>(layoutIds.length);
            for (long id : layoutIds) {
                list.add(getLayout(id));
            }
            return list;
        }
        return Collections.emptyList();
    }

    public List<Layout> getLayouts(long layoutSetId) {
        String cacheKey = Long.toString(layoutSetId);
        Element el = cache.getElement(cacheKey);
        if (el != null) {
            return getLayouts(el.<long[]> get());
        }
        List<Layout> result = null;
        try {
            List<Layout> layouts = layoutDao.loadLayouts(layoutSetId);
            long[] layoutIds = new long[layouts.size()];
            int i = 0;
            result = new ArrayList<>(layouts.size());
            for (Layout layout : layouts) {
                layout = layout.withLayoutTitles(getLayoutTitles(layout.getId()));
                result.add(layout);
                layoutCache.put(Long.toString(layout.getId()), layout);
                layoutIds[i++] = layout.getId();
            }
            cache.put(cacheKey, layoutIds);
        } catch (RuntimeException e) {
            cache.put(cacheKey, null);
        } finally {
            result = result == null ? Collections.<Layout> emptyList() : result;
        }

        return result;
    }

    public List<Layout> getChildLayouts(long layoutSetId, Long parentId) {
        List<Layout> layouts = new ArrayList<>();
        for (Layout layout : getLayouts(layoutSetId)) {
            Long layoutParentId = layout.getParentId();
            if (parentId == null) {
                if (layoutParentId == null) {
                    layouts.add(layout);
                }
            } else if (layoutParentId != null && parentId.longValue() == layoutParentId.longValue()) {
                layouts.add(layout);
            }
        }
        Collections.sort(layouts, new Comparator<Layout>() {
            @Override
            public int compare(Layout o1, Layout o2) {
                return Long.compare(o1.getNr(), o2.getNr());
            }
        });
        return layouts;
    }

    @Nullable
    public Layout getLayout(long layoutSetId, String friendlyUrl) {
        for (Layout layout : getLayouts(layoutSetId)) {
            if (layout.getFriendlyUrl().equals(friendlyUrl)) {
                return layout;
            }
        }
        return null;
    }

    public long addLayout(long layoutSetId, String friendlyUrl, ThemeName themeName, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId)
            throws IllegalFriendlyUrlException, IllegalPageLayoutException {
        validateLayoutData(friendlyUrl, pageLayoutName);
        try {
            return layoutDao.addLayout(layoutSetId, friendlyUrl, themeName, pageLayoutName, parentId, defaultLayoutTitleLanguageId);
        } finally {
            cache.clear();
        }
    }

    public void updateLayout(String friendlyUrl, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId, long id) throws IllegalFriendlyUrlException,
            IllegalPageLayoutException {
        validateLayoutData(friendlyUrl, pageLayoutName);
        try {
            layoutDao.updateLayout(friendlyUrl, pageLayoutName, parentId, defaultLayoutTitleLanguageId, id);
        } finally {
            cache.clear();
            layoutCache.remove(Long.toString(id));
        }
    }

    public void addLayoutTitle(long layoutId, long languageId, String title) {
        try {
            layoutDao.addLayoutTitle(layoutId, languageId, title);
        } finally {
            layoutCache.remove(Long.toString(layoutId));
            layoutTitleCache.remove(Long.toString(layoutId));
        }
    }

    public void updateLayoutTitle(long layoutId, long languageId, String title) {
        try {
            layoutDao.updateLayoutTitle(layoutId, languageId, title);
        } finally {
            layoutTitleCache.remove(Long.toString(layoutId));
            layoutCache.remove(Long.toString(layoutId));
        }
    }

    public void deleteLayoutTitle(long layoutId, long languageId) {
        try {
            layoutDao.deleteLayoutTitle(layoutId, languageId);
        } finally {
            layoutTitleCache.remove(Long.toString(layoutId));
            layoutCache.remove(Long.toString(layoutId));
        }
    }

    private void validateLayoutData(final String friendlyUrl, final PageLayoutName pageLayoutName) throws IllegalFriendlyUrlException, IllegalPageLayoutException {
        if (!isFriendlyUrlValid(friendlyUrl)) {
            throw new IllegalFriendlyUrlException(friendlyUrl);
        }
        if (pageLayoutName == null) {
            throw new IllegalPageLayoutException("pageLayoutId is null");
        }
    }

    public void updatePageLayout(long layoutId, PageLayoutName pageLayoutName) {
        try {
            layoutDao.updatePageLayout(layoutId, pageLayoutName);
        } finally {
            layoutCache.remove(Long.toString(layoutId));
        }
    }

    @Transactional
    public void deleteLayout(long layoutId) {
        Layout layout = getLayout(layoutId);
        if (layout != null) {
            try {
                int deleted = layoutDao.deleteLayout(layoutId);
                int updated = layoutDao.moveLayoutsUp(layout.getLayoutSetId(), layout.getParentId(), layout.getNr());
                log.debug("Deleted {} and updated {} layout(s)", Integer.valueOf(deleted), Integer.valueOf(updated));
            } finally {
                cache.clear();
                layoutTitleCache.remove(Long.toString(layoutId));
                layoutCache.remove(Long.toString(layoutId));
            }
        } else {
            throw new IllegalArgumentException("Layout not found with id " + layoutId);
        }
    }

    @Transactional
    public boolean moveLayoutUp(long layoutId) {
        try {
            return layoutDao.moveLayoutUp(layoutId) == 2;
        } finally {
            cache.clear();
            layoutCache.clear();
        }
    }

    public boolean moveLayoutDown(long layoutId) {
        try {
            return layoutDao.moveLayoutDown(layoutId) == 2;
        } finally {
            cache.clear();
            layoutCache.clear();
        }
    }

    @Transactional
    public Layout get404Layout(long layoutSetId, String friendlyUrl) {
        log.info("get 404 layout; layoutsetid = {}; friendlyUrl = {}", Long.valueOf(layoutSetId), friendlyUrl);
        Theme defaultTheme = themeRepository.getDefaultTheme();
        if (defaultTheme == null) {
            throw new IllegalStateException("Default theme not found. No themes deployed?");
        }
        String pageLayoutId = pageLayoutRepository.getDefaultPageLayout().getPageLayoutName().getFullName();
        return Layout.getDefault(layoutSetId, friendlyUrl, defaultTheme.getThemeName(), pageLayoutId);
    }

    public boolean isFriendlyUrlValid(String friendlyUrl) {
        return !StringUtil.isEmpty(friendlyUrl) && friendlyUrl.startsWith(StringPool.SLASH) && FRIENDLY_URL_PATTERN.matcher(friendlyUrl).matches();
    }
}
