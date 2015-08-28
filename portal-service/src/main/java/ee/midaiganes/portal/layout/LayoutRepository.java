package ee.midaiganes.portal.layout;

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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
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

        this.layoutTitleCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new LayoutTitleCacheLoader(layoutDao));
        this.layoutCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new LayoutCacheLoader(layoutDao, this.layoutTitleCache));
        this.layoutSetLayouts = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new LayoutSetLayoutsCacheLoader(layoutDao, this.layoutCache));
    }

    private static final class LayoutSetLayoutsCacheLoader extends CacheLoader<Long, ImmutableList<Layout>> {
        private final LayoutDao layoutDao;
        private final LoadingCache<Long, Layout> layoutCache;

        private LayoutSetLayoutsCacheLoader(LayoutDao layoutDao, LoadingCache<Long, Layout> layoutCache) {
            this.layoutDao = layoutDao;
            this.layoutCache = layoutCache;
        }

        @Override
        public ImmutableList<Layout> load(@Nonnull Long key) throws Exception {
            long[] layoutIds = layoutDao.loadLayoutIds(key.longValue()).toArray();
            return ImmutableList.copyOf(layoutCache.getAll(Longs.asList(layoutIds)).values());
        }
    }

    private static final class LayoutCacheLoader extends CacheLoader<Long, Layout> {
        private final LayoutDao layoutDao;
        private final LoadingCache<Long, ImmutableList<LayoutTitle>> layoutTitleCache;

        private LayoutCacheLoader(LayoutDao layoutDao, LoadingCache<Long, ImmutableList<LayoutTitle>> layoutTitleCache) {
            this.layoutDao = layoutDao;
            this.layoutTitleCache = layoutTitleCache;
        }

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
    }

    private static final class LayoutTitleCacheLoader extends CacheLoader<Long, ImmutableList<LayoutTitle>> {
        private final LayoutDao layoutDao;

        private LayoutTitleCacheLoader(LayoutDao layoutDao) {
            this.layoutDao = layoutDao;
        }

        @Override
        public ImmutableList<LayoutTitle> load(Long layoutId) {
            return ImmutableList.copyOf(layoutDao.loadLayoutTitles(layoutId.longValue()));
        }

        @Override
        public Map<Long, ImmutableList<LayoutTitle>> loadAll(Iterable<? extends Long> layoutIds) {
            ImmutableSet<Long> lids = ImmutableSet.copyOf(layoutIds);
            return Maps.toMap(lids, new ValueFunction(layoutDao.loadLayoutTitles(lids)));
        }

        private static final class ValueFunction implements Function<Long, ImmutableList<LayoutTitle>> {
            private final TLongObjectHashMap<List<LayoutTitle>> map;

            private ValueFunction(TLongObjectHashMap<List<LayoutTitle>> map) {
                this.map = map;
            }

            @Override
            public ImmutableList<LayoutTitle> apply(Long layoutId) {
                Preconditions.checkNotNull(layoutId);
                return apply(map.get(layoutId.longValue()));
            }

            private ImmutableList<LayoutTitle> apply(List<LayoutTitle> titles) {
                return titles == null ? ImmutableList.of() : ImmutableList.copyOf(titles);
            }
        }
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
        ImmutableList<Layout> layoutSetLayouts = this.layoutSetLayouts.getUnchecked(Long.valueOf(layoutSetId));
        return new LayoutComparator().immutableSortedCopy(Iterables.filter(layoutSetLayouts, new IsChildLayoutPredicate(parentId)));
    }

    private static final class IsChildLayoutPredicate implements Predicate<Layout> {
        private final Long parentId;

        private IsChildLayoutPredicate(Long parentId) {
            this.parentId = parentId;
        }

        @Override
        public boolean apply(Layout layout) {
            Preconditions.checkNotNull(layout);
            Long layoutParentId = layout.getParentId();
            return parentId == null ? layoutParentId == null : parentId.equals(layoutParentId);
        }
    }

    private static final class LayoutComparator extends Ordering<Layout> {
        @Override
        public int compare(Layout o1, Layout o2) {
            Preconditions.checkNotNull(o1);
            Preconditions.checkNotNull(o2);
            return Long.compare(o1.getNr(), o2.getNr());
        }
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

    public void updateLayout(String friendlyUrl, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId, long id)
            throws IllegalFriendlyUrlException, IllegalPageLayoutException {
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
        int deleted = layoutDao.deleteLayout(layoutId);
        int updated = layoutDao.moveLayoutsUp(layout.getLayoutSetId(), layout.getParentId(), layout.getNr());
        log.debug("Deleted {} and updated {} layout(s)", Integer.valueOf(deleted), Integer.valueOf(updated));

        this.layoutSetLayouts.invalidateAll();
        this.layoutCache.invalidateAll();
        this.layoutTitleCache.invalidate(Long.valueOf(layoutId));
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
