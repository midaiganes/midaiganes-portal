package ee.midaiganes.services;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.rowmapper.LongResultSetExtractor;
import ee.midaiganes.services.rowmapper.StringRowMapper;
import ee.midaiganes.util.StringUtil;

// TODO caching
@Component(value = PortalConfig.LANGUAGE_REPOSITORY)
public class LanguageRepository {
	private static final Logger log = LoggerFactory.getLogger(LanguageRepository.class);

	private final JdbcTemplate jdbcTemplate;

	public LanguageRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<String> getSupportedLanguageIds() {
		return jdbcTemplate.query("SELECT languageId FROM Language", new StringRowMapper());// TODO
		// return Arrays.asList(getLanguageId(Locale.US));
	}

	public Long getId(String languageId) {
		return jdbcTemplate.query("SELECT id FROM Language WHERE languageId = ?", new LongResultSetExtractor(), languageId);
	}

	public String getLanguageId(long id) {
		List<String> l = jdbcTemplate.query("SELECT languageId FROM Language WHERE id = ?", new StringRowMapper(), id);
		return l.isEmpty() ? null : l.get(0);
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
