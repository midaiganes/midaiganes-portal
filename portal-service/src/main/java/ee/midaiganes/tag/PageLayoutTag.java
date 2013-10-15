package ee.midaiganes.tag;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.servlet.PageLayoutServlet;
import ee.midaiganes.servlet.http.WrappedOutputHttpServletResponse;
import ee.midaiganes.util.ContextUtil;
import ee.midaiganes.util.PropsValues;

public class PageLayoutTag extends SimpleTag {
	private static final Logger log = LoggerFactory.getLogger(PageLayoutTag.class);

	@Override
	public int doEndTag() throws JspException {
		try {
			ServletContext servletContext = ContextUtil.getServletContext(getHttpServletRequest(), PropsValues.PORTAL_CONTEXT);
			RequestDispatcher requestDispatcher = servletContext.getNamedDispatcher(PageLayoutServlet.class.getName());
			requestDispatcher.include(getHttpServletRequest(), new WrappedOutputHttpServletResponse(getHttpServletResponse(), getPageContext().getOut()));
		} catch (ServletException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return Tag.EVAL_PAGE;
	}

}
