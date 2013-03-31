package ee.midaiganes.services.util;

import java.util.Locale;

import javax.servlet.ServletRequest;

import ee.midaiganes.services.LanguageRepository;

public class LanguageUtil {
	private static LanguageRepository languageRepository;

	public static void setLanguageRepository(LanguageRepository languageRepository) {
		LanguageUtil.languageRepository = languageRepository;
	}

	public static Long getId(Locale locale) {
		return languageRepository.getId(languageRepository.getLanguageId(locale));
	}

	public static Long getId(ServletRequest request) {
		return getId(request.getLocale());
	}
}
