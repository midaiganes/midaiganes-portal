package ee.midaiganes.servlet.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.services.RequestParser;
import ee.midaiganes.services.portal.PortalService;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.SessionUtil;

public class PortalFilter extends HttpFilter {
    private static final Logger log = LoggerFactory.getLogger(PortalFilter.class);
    private final RequestParser requestParser = new RequestParser();

    @Inject
    private PortalService portalService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            PortalService.GetRequestedPageResponse resp = portalService.getRequestedPage(new PortalService.GetRequestedPageRequest(request.getServerName(), SessionUtil
                    .getUserId(request), RequestUtil.getFriendlyURL(request.getRequestURI())));

            PageDisplay pageDisplay = getPageDisplay(request, resp);
            RequestUtil.setPageDisplay(request, pageDisplay);
            if (shouldSet404Header(pageDisplay)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean shouldSet404Header(PageDisplay pageDisplay) {
        return pageDisplay.getLayout().isDefault();
    }

    private PageDisplay getPageDisplay(HttpServletRequest request, PortalService.GetRequestedPageResponse resp) {
        return new PageDisplay(resp.getLayoutSet(), resp.getLayout(), resp.getUser(), requestParser.parserRequest(request), resp.getTheme());
    }
}
