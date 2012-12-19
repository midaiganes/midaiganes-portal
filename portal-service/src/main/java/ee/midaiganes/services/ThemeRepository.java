package ee.midaiganes.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.generated.xml.theme.MidaiganesTheme;
import ee.midaiganes.model.Theme;
import ee.midaiganes.model.ThemeName;

public class ThemeRepository {
	private static final Logger log = LoggerFactory.getLogger(ThemeRepository.class);
	private final Map<String, MidaiganesTheme> themes = new ConcurrentHashMap<String, MidaiganesTheme>();

	public Theme getTheme(ThemeName themeName) {
		if (themeName != null) {
			return getThemeFromMidaiganesTheme(themeName.getContext(), getMidaiganesTheme(themeName));
		}
		return null;
	}

	public List<Theme> getThemes() {
		List<Theme> themes = new ArrayList<Theme>();
		for (Map.Entry<String, MidaiganesTheme> entry : this.themes.entrySet()) {
			for (MidaiganesTheme.Theme theme : entry.getValue().getTheme()) {
				themes.add(getTheme(new ThemeName(entry.getKey(), theme.getId())));
			}
		}
		return themes;
	}

	public Theme getDefaultTheme() {
		for (Map.Entry<String, MidaiganesTheme> st : themes.entrySet()) {
			for (MidaiganesTheme.Theme theme : st.getValue().getTheme()) {
				return getTheme(new ThemeName(st.getKey(), theme.getId()));
			}
		}
		return null;
	}

	private final Theme getThemeFromMidaiganesTheme(String contextPath, MidaiganesTheme.Theme theme) {
		return new Theme(new ThemeName(contextPath, theme.getId()), theme.getPath(), theme.getJavascriptPath(), theme.getCssPath());
	}

	private final MidaiganesTheme.Theme getMidaiganesTheme(ThemeName themeName) {
		MidaiganesTheme contextThemes = themes.get(themeName.getContext());
		if (contextThemes != null) {
			for (MidaiganesTheme.Theme theme : contextThemes.getTheme()) {
				if (theme.getId().equals(themeName.getName())) {
					return theme;
				}
			}
		}
		throw new IllegalStateException("Theme not found: " + themeName);
	}

	public void registerThemes(String contextPath, InputStream themeXmlInputStream) {
		try {
			JAXBContext context = JAXBContext.newInstance(MidaiganesTheme.class.getPackage().getName());
			Unmarshaller unmarshaller = context.createUnmarshaller();
			MidaiganesTheme theme = (MidaiganesTheme) unmarshaller.unmarshal(themeXmlInputStream);
			log.info("contextPath = {}, theme = {}", contextPath, theme);
			if (theme != null) {
				themes.put(contextPath.startsWith("/") ? contextPath.substring(1) : contextPath, theme);
			}

		} catch (JAXBException e) {
			log.error(e.getMessage(), e);
		}
	}
}
