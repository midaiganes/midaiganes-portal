package ee.midaiganes.tag;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.servlet.LayoutPortletServlet;
import ee.midaiganes.servlet.http.LayoutPortletRequest;
import ee.midaiganes.servlet.http.WrappedOutputHttpServletResponse;
import ee.midaiganes.util.ContextUtil;

public class LayoutPortletTag extends SimpleTag {
	private static final Logger log = LoggerFactory.getLogger(LayoutPortletTag.class);
	private long id;

	@Override
	public int doEndTag() {
		log.debug("id = {}", Long.valueOf(id));
		try {
			HttpServletRequest request = getHttpServletRequest();
			ServletContext servletContext = ContextUtil.getPortalServletContext(request);
			RequestDispatcher requestDispatcher = servletContext.getNamedDispatcher(LayoutPortletServlet.class.getName());
			requestDispatcher.include(new LayoutPortletRequest(request, id), new WrappedOutputHttpServletResponse(getHttpServletResponse(), getPageContext()
					.getOut()));
		} catch (ServletException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
		return Tag.EVAL_PAGE;
	}

	public void setId(long id) {
		this.id = id;
	}
}
