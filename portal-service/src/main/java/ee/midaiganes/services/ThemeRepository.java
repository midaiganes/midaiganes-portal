package ee.midaiganes.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.generated.xml.theme.MidaiganesTheme;
import ee.midaiganes.model.Theme;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.util.CollectionUtil;
import ee.midaiganes.util.StringPool;

public class ThemeRepository {
	private static final Logger log = LoggerFactory.getLogger(ThemeRepository.class);
	private final Map<ThemeName, Theme> themes = new ConcurrentHashMap<>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public Theme getTheme(ThemeName themeName) {
		lock.readLock().lock();
		try {
			return themes.get(themeName);
		} finally {
			lock.readLock().unlock();
		}
	}

	public List<Theme> getThemes() {
		lock.readLock().lock();
		try {
			return new ArrayList<>(themes.values());
		} finally {
			lock.readLock().unlock();
		}
	}

	public Theme getDefaultTheme() {
		lock.readLock().lock();
		try {
			return CollectionUtil.getFirstElement(themes.values());
		} finally {
			lock.readLock().unlock();
		}
	}

	private final Theme getThemeFromMidaiganesTheme(String contextPath, MidaiganesTheme.Theme theme) {
		return new Theme(new ThemeName(contextPath, theme.getId()), theme.getPath(), theme.getJavascriptPath(), theme.getCssPath());
	}

	private Unmarshaller createUnmarshaller() throws JAXBException {
		return JAXBContext.newInstance(MidaiganesTheme.class.getPackage().getName()).createUnmarshaller();
	}

	private MidaiganesTheme unmarshal(InputStream themeXmlInputStream) throws JAXBException {
		return (MidaiganesTheme) createUnmarshaller().unmarshal(themeXmlInputStream);
	}

	public void registerThemes(String contextPath, InputStream themeXmlInputStream) {
		try {
			MidaiganesTheme theme = unmarshal(themeXmlInputStream);
			log.info("contextPath = {}, theme = {}", contextPath, theme);
			if (theme != null) {
				contextPath = contextPath.startsWith(StringPool.SLASH) ? contextPath.substring(1) : contextPath;
				for (MidaiganesTheme.Theme t : theme.getTheme()) {
					registerTheme(getThemeFromMidaiganesTheme(contextPath, t));
				}
			}
		} catch (JAXBException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void unregisterThemes(String contextPath) {
		try {
			lock.writeLock().lock();
			Set<ThemeName> themeNames = themes.keySet();
			for (ThemeName tn : themeNames) {
				if (tn.getContextWithSlash().equals(contextPath)) {
					themeNames.remove(tn);
					log.info("removed theme: {}", tn);
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void registerTheme(Theme theme) {
		lock.writeLock().lock();
		try {
			themes.put(theme.getThemeName(), theme);
		} finally {
			lock.writeLock().unlock();
		}
	}
}
