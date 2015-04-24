package ee.midaiganes.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;

import ee.midaiganes.portal.theme.Theme;
import ee.midaiganes.portal.theme.ThemeRepository;
import ee.midaiganes.services.ServletContextResourceRepository;
import ee.midaiganes.util.StringPool;

public class StaticContentServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(StaticContentServlet.class);

    @Inject
    private ThemeRepository themeRepository;

    @Inject
    private ServletContextResourceRepository servletContextResourceRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            autowire();
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            String requestURI = request.getRequestURI();
            Map<String, Theme> staticFiles = getThemesStaticContents();
            log.debug("requestURI = {}; staticFiles = {}", requestURI, staticFiles);
            if (staticFiles.containsKey(requestURI)) {
                try (InputStream is = getResourceAsStream(staticFiles.get(requestURI), requestURI)) {
                    if (is != null) {
                        copyToOutputStream(response, is);
                    }
                }
            } else {
                log.error("file not found: {}", requestURI);
            }

        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void copyToOutputStream(HttpServletResponse response, @Nonnull InputStream is) throws IOException {
        try (ServletOutputStream os = response.getOutputStream()) {
            if (os != null) {
                ByteStreams.copy(is, os);
                os.flush();
            }
        }
    }

    @Nullable
    private InputStream getResourceAsStream(Theme theme, String requestURI) {
        return getContext(theme.getThemeName().getContextWithSlash()).getResourceAsStream(requestURI.substring(theme.getThemeName().getContextWithSlash().length()));
    }

    private ServletContext getContext(String contextPath) {
        return getServletContext().getContext(contextPath);
    }

    private Map<String, Theme> getThemesStaticContents() {
        Map<String, Theme> files = new HashMap<>();
        ServletContext context = getServletContext();
        for (Theme theme : themeRepository.getThemes()) {
            ServletContext ctx = context.getContext(theme.getThemeName().getContextWithSlash());
            files.putAll(getStaticContents(ctx, theme, theme.getThemePath() + StringPool.SLASH + theme.getJavascriptPath()));
            files.putAll(getStaticContents(ctx, theme, theme.getThemePath() + StringPool.SLASH + theme.getCssPath()));
        }
        return files;
    }

    private Map<String, Theme> getStaticContents(ServletContext ctx, Theme theme, String resourcePath) {
        ImmutableList<String> list = servletContextResourceRepository.getContextResourcePaths(ctx, resourcePath);
        // ctx.getResourcePaths(theme.getThemePath() + StringPool.SLASH +
        // theme.getJavascriptPath());
        Map<String, Theme> files = new HashMap<>();
        if (list != null) {
            for (String path : list) {
                files.put(theme.getThemeName().getContextWithSlash() + path, theme);
            }
        }
        return files;
    }
}
