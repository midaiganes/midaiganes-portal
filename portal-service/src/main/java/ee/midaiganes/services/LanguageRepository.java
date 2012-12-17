package ee.midaiganes.services;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.util.StringUtil;

public class LanguageRepository {
	private static final Logger log = LoggerFactory.getLogger(LanguageRepository.class);
	@Resource
	private JdbcTemplate jdbcTemplate;

	public List<String> getSupportedLanguageIds() {
		return Arrays.asList(getLanguageId(Locale.ENGLISH));
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
