package ee.midaiganes.services;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.services.rowmapper.LongResultSetExtractor;
import ee.midaiganes.services.rowmapper.StringRowMapper;
import ee.midaiganes.util.StringUtil;

// TODO caching
@Resource(name = PortalConfig.LANGUAGE_REPOSITORY)
public class LanguageRepository {
	private static final Logger log = LoggerFactory.getLogger(LanguageRepository.class);

	private final JdbcTemplate jdbcTemplate;
	private final Cache cache;

	public LanguageRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.cache = SingleVmPool.getCache(LanguageRepository.class.getName());
	}

	public List<String> getSupportedLanguageIds() {
		String cacheKey = "getSupportedLanguageIds";
		Element el = cache.getElement(cacheKey);
		if (el != null) {
			return el.get();
		}
		List<String> list = null;
		try {
			list = Collections.unmodifiableList(jdbcTemplate.query("SELECT languageId FROM Language", new StringRowMapper()));// TODO
		} finally {
			cache.put(cacheKey, list);
		}
		return list;
	}

	public Long getId(String languageId) {
		String cacheKey = "getId#" + languageId;
		Element el = cache.getElement(cacheKey);
		if (el != null) {
			return el.get();
		}
		Long id = null;
		try {
			id = jdbcTemplate.query("SELECT id FROM Language WHERE languageId = ?", new LongResultSetExtractor(), languageId);
		} finally {
			cache.put(cacheKey, id);
		}
		return id;
	}

	public String getLanguageId(long id) {
		String cacheKey = "getLanguageId#" + id;
		Element el = cache.getElement(cacheKey);
		if (el != null) {
			return el.get();
		}
		String languageId = null;
		try {
			List<String> l = jdbcTemplate.query("SELECT languageId FROM Language WHERE id = ?", new StringRowMapper(), Long.valueOf(id));
			languageId = l.isEmpty() ? null : l.get(0);
		} finally {
			cache.put(cacheKey, languageId);
		}
		return languageId;
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
			log.warn("locale.getCountry is empty");
			return language;
		}
		return language + "_" + country;
	}

	public boolean isLanguageIdSupported(String languageId) {
		return getSupportedLanguageIds().contains(languageId);
	}

	public Locale getLocale(String languageId) {
		// TODO
		log.warn("todo");
		return null;
	}
}
