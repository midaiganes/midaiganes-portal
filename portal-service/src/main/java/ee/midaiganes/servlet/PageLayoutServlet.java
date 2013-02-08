package ee.midaiganes.servlet;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.PageLayout;
import ee.midaiganes.services.PageLayoutRepository;
import ee.midaiganes.util.ContextUtil;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;

public class PageLayoutServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(PageLayoutServlet.class);

	@Resource(name = PortalConfig.PAGE_LAYOUT_REPOSITORY)
	private PageLayoutRepository pageLayoutRepository;

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
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PageLayout pageLayout = getPageLayout(request);
		ServletContext servletContext = getServletContext(request, pageLayout);
		RequestDispatcher requestDispatcher = getRequestDispatcher(servletContext, pageLayout);
		requestDispatcher.include(request, response);
	}

	private PageLayout getPageLayout(HttpServletRequest request) {
		String pageLayoutId = RequestUtil.getPageDisplay(request).getLayout().getPageLayoutId();
		PageLayout pageLayout = pageLayoutRepository.getPageLayout(pageLayoutId);
		return pageLayout != null ? pageLayout : pageLayoutRepository.getDefaultPageLayout();
	}

	private RequestDispatcher getRequestDispatcher(ServletContext servletContext, PageLayout pageLayout) {
		return servletContext.getRequestDispatcher(getPath(pageLayout));
	}

	private String getPath(PageLayout pageLayout) {
		return pageLayout.getLayoutPath() + StringPool.SLASH + pageLayout.getPageLayoutName().getName() + ".jsp";
	}

	private ServletContext getServletContext(HttpServletRequest request, PageLayout pageLayout) {
		return ContextUtil.getServletContext(request, pageLayout.getPageLayoutName().getContextWithSlash());
	}
}
