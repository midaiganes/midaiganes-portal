package ee.midaiganes.services.util;

import java.util.Locale;

import javax.servlet.ServletRequest;

import ee.midaiganes.beans.Utils;
import ee.midaiganes.services.LanguageRepository;

public class LanguageUtil {
    private static LanguageRepository getRepository() {
        return Utils.getInstance().getInstance(LanguageRepository.class);
    }

    public static Long getId(Locale locale) {
        LanguageRepository languageRepository = getRepository();
        return languageRepository.getId(languageRepository.getLanguageId(locale));
    }

    public static Long getId(ServletRequest request) {
        return getId(request.getLocale());
    }
}
