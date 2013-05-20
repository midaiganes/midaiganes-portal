package ee.midaiganes.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.generated.xml.theme.MidaiganesTheme;
import ee.midaiganes.model.Theme;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.util.CollectionUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.XmlUtil;

@Component(value = PortalConfig.THEME_REPOSITORY)
public class ThemeRepository {
	private static final Logger log = LoggerFactory.getLogger(ThemeRepository.class);
	private final Map<ThemeName, Theme> themes = new ConcurrentHashMap<>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Resource(name = PortalConfig.PORTAL_JDBC_TEMPLATE)
	private JdbcTemplate jdbcTemplate;

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

	public void registerThemes(String contextPath, InputStream themeXmlInputStream) {
		try {
			MidaiganesTheme theme = XmlUtil.unmarshalWithoutJAXBElement(MidaiganesTheme.class, themeXmlInputStream);
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
			ThemeName themeName = theme.getThemeName();
			jdbcTemplate.update("INSERT IGNORE INTO Theme(context, name) VALUES(?, ?)", themeName.getContext(), themeName.getName());
		} finally {
			lock.writeLock().unlock();
		}
	}
}
