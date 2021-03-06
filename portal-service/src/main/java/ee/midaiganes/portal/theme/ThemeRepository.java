package ee.midaiganes.portal.theme;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import ee.midaiganes.generated.xml.theme.MidaiganesTheme;
import ee.midaiganes.services.ThemeRegistryRepository;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.XmlUtil;

public class ThemeRepository implements ThemeRegistryRepository {
    private static final Logger log = LoggerFactory.getLogger(ThemeRepository.class);
    private final ConcurrentHashMap<ThemeName, Theme> themes = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final JdbcTemplate jdbcTemplate;

    @Inject
    public ThemeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Theme getTheme(ThemeName themeName) {
        lock.readLock().lock();
        try {
            return themes.get(themeName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public ImmutableList<Theme> getThemes() {
        lock.readLock().lock();
        try {
            return ImmutableList.copyOf(themes.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nullable
    public Theme getDefaultTheme() {
        lock.readLock().lock();
        try {
            return Iterables.getFirst(Preconditions.checkNotNull(themes.values()), null);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nonnull
    private final Theme getThemeFromMidaiganesTheme(String contextPath, MidaiganesTheme.Theme theme) {
        return new Theme(new ThemeName(contextPath, theme.getId()), theme.getPath(), theme.getJavascriptPath(), theme.getCssPath());
    }

    @Override
    @Transactional
    public void registerThemes(String contextPath, InputStream themeXmlInputStream) {
        try {
            MidaiganesTheme theme = XmlUtil.unmarshalWithoutJAXBElement(MidaiganesTheme.class, themeXmlInputStream);
            log.info("contextPath = {}, theme = {}", contextPath, theme);
            if (theme != null) {
                String contextPathWithoutSlash = contextPath.startsWith(StringPool.SLASH) ? contextPath.substring(1) : contextPath;
                for (MidaiganesTheme.Theme t : theme.getTheme()) {
                    registerTheme(getThemeFromMidaiganesTheme(contextPathWithoutSlash, t));
                }
            }
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
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

    private void registerTheme(@Nonnull Theme theme) {
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
