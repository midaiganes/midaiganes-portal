package ee.midaiganes.servlet.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.beans.PortalBeans;
import ee.midaiganes.model.PageDisplay;
import ee.midaiganes.services.RequestParser;
import ee.midaiganes.services.portal.PortalService;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.SessionUtil;

public class PortalFilter extends HttpFilter {
    private static final Logger log = LoggerFactory.getLogger(PortalFilter.class);

    @Resource(name = PortalBeans.PORTALSERVICE)
    private PortalService portalService;

    private RequestParser requestParser;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.requestParser = BeanRepositoryUtil.getBean(RequestParser.class);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            PortalService.GetRequestedPageResponse resp = portalService.getRequestedPage(new PortalService.GetRequestedPageRequest(request.getServerName(), SessionUtil
                    .getUserId(request), RequestUtil.getFriendlyURL(request.getRequestURI())));

            PageDisplay pageDisplay = new PageDisplay();
            pageDisplay.setRequestInfo(requestParser.parserRequest(request));
            pageDisplay.setLayoutSet(resp.getLayoutSet());
            pageDisplay.setUser(resp.getUser());
            pageDisplay.setLayout(resp.getLayout());
            pageDisplay.setTheme(resp.getTheme());
            RequestUtil.setPageDisplay(request, pageDisplay);
            if (pageDisplay.getLayout().isDefault()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
