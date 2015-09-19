package ee.midaiganes.services.portal;

import java.util.Locale;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.aspect.Service;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layout.LayoutRepository;
import ee.midaiganes.portal.layoutset.LayoutSet;
import ee.midaiganes.portal.layoutset.LayoutSetRepository;
import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.portal.theme.ThemeName;
import ee.midaiganes.portal.theme.ThemeRepository;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.secureservices.SecureLayoutRepository;
import ee.midaiganes.services.LanguageRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.util.StringPool;

@Service(service = PortalService.class)
public class PortalServiceImpl implements PortalService {
    private static final Logger log = LoggerFactory.getLogger(PortalServiceImpl.class);

    private final LayoutSetRepository layoutSetRepository;
    private final UserRepository userRepository;
    private final SecureLayoutRepository secureLayoutRepository;
    private final LayoutRepository layoutRepository;
    private final ThemeRepository themeRepository;
    private final LanguageRepository languageRepository;

    @Inject
    public PortalServiceImpl(LayoutSetRepository layoutSetRepository, UserRepository userRepository, SecureLayoutRepository secureLayoutRepository,
            LayoutRepository layoutRepository, ThemeRepository themeRepository, LanguageRepository languageRepository) {
        this.layoutSetRepository = layoutSetRepository;
        this.userRepository = userRepository;
        this.secureLayoutRepository = secureLayoutRepository;
        this.layoutRepository = layoutRepository;
        this.themeRepository = themeRepository;
        this.languageRepository = languageRepository;
    }

    @Override
    @Transactional
    public GetRequestedPageResponse getRequestedPage(GetRequestedPageRequest request) {
        GetRequestedPageResponse response = new GetRequestedPageResponse();
        response.setLayoutSet(getLayoutSet(request.getServerName()));
        response.setUser(getUser(request.getUserId()));
        response.setLayout(getLayout(request.getUserId(), response.getLayoutSet().getId(), request.getFriendlyUrl()));
        response.setTheme(getTheme(response.getLayout(), response.getLayoutSet()));
        response.setLanguageId(getLanguageId(request.getLocale()));
        return response;
    }

    private long getLanguageId(Locale locale) {// TODO
        String languageId = languageRepository.getLanguageId(locale);
        Long language_id = languageRepository.getId(languageId);
        if (language_id == null) {
            language_id = languageRepository.getId(languageRepository.getSupportedLanguageIds().get(0));
        }
        return language_id.longValue();
    }

    private LayoutSet getLayoutSet(String virtualHost) {
        LayoutSet layoutSet = layoutSetRepository.getLayoutSet(virtualHost);
        if (layoutSet != null) {
            return layoutSet;
        }
        return layoutSetRepository.getDefaultLayoutSet(virtualHost);
    }

    private User getUser(long userid) {
        User user = null;
        if (!User.isDefaultUserId(userid)) {
            user = userRepository.getUser(userid);
        }
        return user != null ? user : User.getDefault();
    }

    private Layout getLayout(long userId, long layoutSetId, String friendlyUrl) {
        try {
            final Layout layout;
            if (StringPool.SLASH.equals(friendlyUrl)) {
                layout = secureLayoutRepository.getHomeLayout(userId, layoutSetId);
            } else {
                layout = secureLayoutRepository.getLayout(userId, layoutSetId, friendlyUrl);
            }
            if (layout != null) {
                return layout;
            }
        } catch (PrincipalException e) {
            // TODO handle this..
            log.debug("User '{}' is not allowd to access '{}'", Long.valueOf(userId), friendlyUrl);
        }
        return layoutRepository.get404Layout(layoutSetId, friendlyUrl);
    }

    private Theme getTheme(Layout layout, LayoutSet layoutSet) {
        ThemeName themeName = layout.getThemeName();
        if (themeName != null) {
            Theme theme = themeRepository.getTheme(themeName);
            if (theme != null) {
                return theme;
            }
        }
        themeName = layoutSet.getThemeName();
        if (themeName != null) {
            Theme theme = themeRepository.getTheme(themeName);
            if (theme != null) {
                return theme;
            }
        }
        return themeRepository.getDefaultTheme();
    }
}
