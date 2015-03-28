package ee.midaiganes.portal.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Longs;

import ee.midaiganes.portal.pagelayout.PageLayoutName;
import ee.midaiganes.portal.pagelayout.PageLayoutRepository;
import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.portal.theme.ThemeName;
import ee.midaiganes.portal.theme.ThemeRepository;
import ee.midaiganes.services.exceptions.IllegalFriendlyUrlException;
import ee.midaiganes.services.exceptions.IllegalPageLayoutException;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;
import gnu.trove.map.hash.TLongObjectHashMap;

public class LayoutRepository {
    private static final Logger log = LoggerFactory.getLogger(LayoutRepository.class);
    private static final Pattern FRIENDLY_URL_PATTERN;
    static {
        FRIENDLY_URL_PATTERN = Pattern.compile("^\\/[a-zA-Z0-9_\\-]*$");
    }
    private final LayoutDao layoutDao;
    private final ThemeRepository themeRepository;
    private final PageLayoutRepository pageLayoutRepository;

    private final LoadingCache<Long, ImmutableList<LayoutTitle>> layoutTitleCache;
    private final LoadingCache<Long, Layout> layoutCache;
    private final LoadingCache<Long, ImmutableList<Layout>> layoutSetLayouts;

    @Inject
    public LayoutRepository(LayoutDao layoutDao, ThemeRepository themeRepository, PageLayoutRepository pageLayoutRepository) {
        this.layoutDao = layoutDao;
        this.themeRepository = themeRepository;
        this.pageLayoutRepository = pageLayoutRepository;

        this.layoutTitleCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, ImmutableList<LayoutTitle>>() {
            @Override
            public ImmutableList<LayoutTitle> load(Long layoutId) {
                return ImmutableList.copyOf(layoutDao.loadLayoutTitles(layoutId.longValue()));
            }

            @Override
            public Map<Long, ImmutableList<LayoutTitle>> loadAll(Iterable<? extends Long> layoutIds) throws Exception {
                TLongObjectHashMap<List<LayoutTitle>> map = layoutDao.loadLayoutTitles(ImmutableList.copyOf(layoutIds));
                Map<Long, ImmutableList<LayoutTitle>> result = new HashMap<>();
                for (Long layoutId : layoutIds) {
                    List<LayoutTitle> titles = map.get(layoutId.longValue());
                    result.put(layoutId, titles == null ? ImmutableList.of() : ImmutableList.copyOf(titles));
                }
                return result;
            }
        });
        this.layoutCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, Layout>() {
            @Override
            public Layout load(Long layoutId) throws Exception {
                Layout layout = layoutDao.loadLayout(layoutId.longValue());
                return layout.withLayoutTitles(layoutTitleCache.getUnchecked(layoutId));
            }

            @Override
            public Map<Long, Layout> loadAll(Iterable<? extends Long> layoutIds) throws Exception {
                List<Layout> layouts = layoutDao.loadLayouts(ImmutableList.copyOf(layoutIds));
                ImmutableMap<Long, ImmutableList<LayoutTitle>> titlesMap = layoutTitleCache.getAll(layoutIds);
                Map<Long, Layout> result = new HashMap<>(layouts.size());
                for (Layout l : layouts) {
                    result.put(Long.valueOf(l.getId()), l.withLayoutTitles(titlesMap.get(Long.valueOf(l.getId()))));
                }
                return result;
            }
        });

        this.layoutSetLayouts = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, ImmutableList<Layout>>() {
            @Override
            public ImmutableList<Layout> load(Long key) throws Exception {
                long[] layoutIds = layoutDao.loadLayoutIds(key.longValue()).toArray();
                return ImmutableList.copyOf(layoutCache.getAll(Longs.asList(layoutIds)).values());
            }
        });
    }

    @Nonnull
    public ImmutableList<LayoutTitle> getLayoutTitles(long layoutId) {
        return this.layoutTitleCache.getUnchecked(Long.valueOf(layoutId));
    }

    @Nonnull
    public Layout getLayout(long layoutId) {
        return this.layoutCache.getUnchecked(Long.valueOf(layoutId));
    }

    public ImmutableList<Layout> getLayouts(long layoutSetId) {
        return this.layoutSetLayouts.getUnchecked(Long.valueOf(layoutSetId));
    }

    public ImmutableList<Layout> getChildLayouts(long layoutSetId, Long parentId) {
        List<Layout> layouts = new ArrayList<>();
        for (Layout layout : this.layoutSetLayouts.getUnchecked(Long.valueOf(layoutSetId))) {
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
        return ImmutableList.copyOf(layouts);
    }

    @Nullable
    public Layout getLayout(long layoutSetId, String friendlyUrl) {
        for (Layout layout : this.layoutSetLayouts.getUnchecked(Long.valueOf(layoutSetId))) {
            if (layout.getFriendlyUrl().equals(friendlyUrl)) {
                return layout;
            }
        }
        return null;
    }

    public long addLayout(long layoutSetId, String friendlyUrl, ThemeName themeName, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId)
            throws IllegalFriendlyUrlException, IllegalPageLayoutException {
        validateLayoutData(friendlyUrl, pageLayoutName);
        long layoutId = layoutDao.addLayout(layoutSetId, friendlyUrl, themeName, pageLayoutName, parentId, defaultLayoutTitleLanguageId);
        this.layoutTitleCache.invalidate(Long.valueOf(layoutId));
        this.layoutSetLayouts.invalidate(Long.valueOf(layoutSetId));
        return layoutId;
    }

    public void updateLayout(String friendlyUrl, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId, long id) throws IllegalFriendlyUrlException,
            IllegalPageLayoutException {
        validateLayoutData(friendlyUrl, pageLayoutName);
        layoutDao.updateLayout(friendlyUrl, pageLayoutName, parentId, defaultLayoutTitleLanguageId, id);
        this.layoutSetLayouts.invalidateAll();
        this.layoutCache.invalidate(Long.valueOf(id));
        this.layoutTitleCache.invalidate(Long.valueOf(id));
    }

    public void addLayoutTitle(long layoutId, long languageId, String title) {
        layoutDao.addLayoutTitle(layoutId, languageId, title);

        this.layoutSetLayouts.invalidateAll();
        this.layoutCache.invalidate(Long.valueOf(layoutId));
        this.layoutTitleCache.invalidate(Long.valueOf(layoutId));
    }

    public void updateLayoutTitle(long layoutId, long languageId, String title) {
        layoutDao.updateLayoutTitle(layoutId, languageId, title);

        this.layoutSetLayouts.invalidateAll();
        this.layoutCache.invalidate(Long.valueOf(layoutId));
        this.layoutTitleCache.invalidate(Long.valueOf(layoutId));
    }

    public void deleteLayoutTitle(long layoutId, long languageId) {
        layoutDao.deleteLayoutTitle(layoutId, languageId);

        this.layoutSetLayouts.invalidateAll();
        this.layoutCache.invalidate(Long.valueOf(layoutId));
        this.layoutTitleCache.invalidate(Long.valueOf(layoutId));
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
        layoutDao.updatePageLayout(layoutId, pageLayoutName);

        this.layoutSetLayouts.invalidateAll();
        this.layoutCache.invalidate(Long.valueOf(layoutId));
        this.layoutTitleCache.invalidate(Long.valueOf(layoutId));
    }

    @Transactional
    public void deleteLayout(long layoutId) {
        Layout layout = getLayout(layoutId);
        if (layout != null) {
            int deleted = layoutDao.deleteLayout(layoutId);
            int updated = layoutDao.moveLayoutsUp(layout.getLayoutSetId(), layout.getParentId(), layout.getNr());
            log.debug("Deleted {} and updated {} layout(s)", Integer.valueOf(deleted), Integer.valueOf(updated));

            this.layoutSetLayouts.invalidateAll();
            this.layoutCache.invalidateAll();
            this.layoutTitleCache.invalidate(Long.valueOf(layoutId));
        } else {
            throw new IllegalArgumentException("Layout not found with id " + layoutId);
        }
    }

    @Transactional
    public boolean moveLayoutUp(long layoutId) {
        try {
            return layoutDao.moveLayoutUp(layoutId) == 2;
        } finally {
            this.layoutSetLayouts.invalidateAll();
            this.layoutCache.invalidateAll();
        }
    }

    public boolean moveLayoutDown(long layoutId) {
        try {
            return layoutDao.moveLayoutDown(layoutId) == 2;
        } finally {
            this.layoutSetLayouts.invalidateAll();
            this.layoutCache.invalidateAll();
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
