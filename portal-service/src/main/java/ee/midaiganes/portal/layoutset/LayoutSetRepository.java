package ee.midaiganes.portal.layoutset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;

import ee.midaiganes.portal.theme.ThemeName;

public class LayoutSetRepository {
    private static final Logger log = LoggerFactory.getLogger(LayoutSetRepository.class);

    private final LayoutSetDao layoutSetDao;

    private final LoadingCache<String, Optional<LayoutSet>> virtualHostLayoutSetCache;
    private final LoadingCache<Long, Optional<LayoutSet>> idLayoutSetCache;
    private final LoadingCache<Boolean, ImmutableList<Long>> allLayoutSetsCache;

    @Inject
    public LayoutSetRepository(LayoutSetDao layoutSetDao) {
        this.layoutSetDao = layoutSetDao;
        this.virtualHostLayoutSetCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<String, Optional<LayoutSet>>() {
            @Override
            public Optional<LayoutSet> load(String virtualHost) throws Exception {
                LayoutSet layoutSet = layoutSetDao.getLayoutSet(virtualHost);
                Optional<LayoutSet> ols = Optional.fromNullable(layoutSet);
                if (layoutSet != null) {
                    idLayoutSetCache.put(Long.valueOf(layoutSet.getId()), ols);
                }
                return ols;
            }
        });

        this.idLayoutSetCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, Optional<LayoutSet>>() {
            @Override
            public Optional<LayoutSet> load(Long id) {
                LayoutSet layoutSet = layoutSetDao.getLayoutSet(id.longValue());
                Optional<LayoutSet> ols = Optional.fromNullable(layoutSet);
                if (layoutSet != null) {
                    virtualHostLayoutSetCache.put(layoutSet.getVirtualHost(), ols);
                }
                return ols;
            }

            @Override
            public Map<Long, Optional<LayoutSet>> loadAll(Iterable<? extends Long> keys) {
                List<LayoutSet> layoutSets = layoutSetDao.getLayoutSets(ImmutableList.<Long> copyOf(keys));
                Map<Long, Optional<LayoutSet>> result = new HashMap<>(layoutSets.size());
                for (LayoutSet ls : layoutSets) {
                    Optional<LayoutSet> ols = Optional.of(ls);
                    virtualHostLayoutSetCache.put(ls.getVirtualHost(), ols);
                    result.put(Long.valueOf(ls.getId()), ols);
                }
                return result;
            }
        });
        this.allLayoutSetsCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).initialCapacity(1).maximumSize(1)
                .build(new CacheLoader<Boolean, ImmutableList<Long>>() {
                    @Override
                    public ImmutableList<Long> load(Boolean key) throws Exception {
                        return ImmutableList.copyOf(Longs.asList(layoutSetDao.getLayoutSetIds().toArray()));
                    }

                });
    }

    public List<LayoutSet> getLayoutSets() {
        ImmutableList<Long> ids = allLayoutSetsCache.getUnchecked(Boolean.TRUE);
        try {
            return ImmutableList.copyOf(Collections2.transform(idLayoutSetCache.getAll(ids).values(), new Function<Optional<LayoutSet>, LayoutSet>() {
                @Override
                @Nullable
                public LayoutSet apply(@Nullable Optional<LayoutSet> input) {
                    return input.get();
                }
            }));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public LayoutSet getLayoutSet(String virtualHost) {
        return virtualHostLayoutSetCache.getUnchecked(virtualHost).orNull();
    }

    @Nullable
    public LayoutSet getLayoutSet(long id) {
        return idLayoutSetCache.getUnchecked(Long.valueOf(id)).orNull();
    }

    public long addLayoutSet(String virtualHost, ThemeName themeName) {
        long id = layoutSetDao.addLayoutSet(virtualHost, themeName);
        idLayoutSetCache.invalidate(Long.valueOf(id));
        virtualHostLayoutSetCache.invalidate(virtualHost);

        ImmutableList<Long> idsList = allLayoutSetsCache.getIfPresent(Boolean.TRUE);
        if (idsList != null) {
            allLayoutSetsCache.put(Boolean.TRUE, ImmutableList.<Long> builder().addAll(idsList).add(Long.valueOf(id)).build());
        }
        return id;
    }

    public void updateLayoutSet(long id, String virtualHost, ThemeName themeName) {
        layoutSetDao.updateLayoutSet(id, virtualHost, themeName);
        idLayoutSetCache.invalidate(Long.valueOf(id));
        virtualHostLayoutSetCache.invalidateAll();
    }

    public LayoutSet getDefaultLayoutSet(String virtualHost) {
        log.warn("get default layout set: {}", virtualHost);
        return LayoutSet.getDefault(virtualHost);
    }
}
