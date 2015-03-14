package ee.midaiganes.services;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import ee.midaiganes.services.rowmapper.LongResultSetExtractor;
import ee.midaiganes.services.rowmapper.StringRowMapper;
import ee.midaiganes.util.StringUtil;

public class LanguageRepository {
    private static final Logger log = LoggerFactory.getLogger(LanguageRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final LoadingCache<String, Optional<Long>> getIdCache;
    private final LoadingCache<Long, Optional<String>> getLanguageIdCache;
    private final LoadingCache<Boolean, ImmutableList<String>> getSupportedLanguageIdsCache;

    @Inject
    public LanguageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.getIdCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<String, Optional<Long>>() {
            @Override
            public Optional<Long> load(String languageId) throws Exception {
                return Optional.fromNullable(loadId(languageId));
            }
        });
        this.getLanguageIdCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, Optional<String>>() {
            @Override
            public Optional<String> load(Long id) throws Exception {
                return Optional.fromNullable(loadLanguageId(id.longValue()));
            }
        });
        this.getSupportedLanguageIdsCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Boolean, ImmutableList<String>>() {
            @Override
            public ImmutableList<String> load(Boolean key) throws Exception {
                return ImmutableList.copyOf(loadSupportedLanguageIds());
            }
        });
    }

    public ImmutableList<String> getSupportedLanguageIds() {
        return getSupportedLanguageIdsCache.getUnchecked(Boolean.TRUE);
    }

    public Long getId(String languageId) {
        return getIdCache.getUnchecked(languageId).orNull();
    }

    public String getLanguageId(long id) {
        return getLanguageIdCache.getUnchecked(Long.valueOf(id)).orNull();
    }

    public String getLanguageId(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if (StringUtil.isEmpty(language)) {
            log.warn("locale.getLanguage is empty");
            if (StringUtil.isEmpty(country)) {
                throw new IllegalStateException("language & country empty");
            }
            return country;
        }
        if (StringUtil.isEmpty(country)) {
            log.debug("locale.getCountry is empty");
            return language;
        }
        return language + "_" + country;
    }

    public boolean isLanguageIdSupported(String languageId) {
        return getSupportedLanguageIds().contains(languageId);
    }

    private List<String> loadSupportedLanguageIds() {
        return jdbcTemplate.query("SELECT languageId FROM Language", new StringRowMapper());
    }

    private String loadLanguageId(long id) {
        List<String> l = jdbcTemplate.query("SELECT languageId FROM Language WHERE id = ?", new StringRowMapper(), Long.valueOf(id));
        return l.isEmpty() ? null : l.get(0);
    }

    private Long loadId(String languageId) {
        return jdbcTemplate.query("SELECT id FROM Language WHERE languageId = ?", new LongResultSetExtractor(), languageId);
    }
}
