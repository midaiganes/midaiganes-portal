package ee.midaiganes.tag;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PortletName;
import ee.midaiganes.servlet.RuntimePortletServlet;
import ee.midaiganes.servlet.http.WrappedOutputHttpServletResponse;
import ee.midaiganes.util.ContextUtil;
import ee.midaiganes.util.StringUtil;

public class RuntimePortletTag extends SimpleTag {
	private static final Logger log = LoggerFactory.getLogger(RuntimePortletTag.class);
	private String name;

	@Override
	public int doEndTag() {
		if (!StringUtil.isEmpty(name)) {
			try {
				includeRuntimePortletServlet();
			} catch (ServletException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			} catch (RuntimeException e) {
				log.error(e.getMessage(), e);
			}
		}
		return Tag.EVAL_PAGE;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void includeRuntimePortletServlet() throws ServletException, IOException {
		ServletContext servletContext = ContextUtil.getPortalServletContext(getHttpServletRequest());
		RequestDispatcher requestDispatcher = servletContext.getNamedDispatcher(RuntimePortletServlet.class.getName());
		HttpServletResponse response = new WrappedOutputHttpServletResponse(getHttpServletResponse(), getPageContext().getOut());
		requestDispatcher.include(new RuntimePortletServletRequest(getHttpServletRequest(), new PortletName(name)), response);
	}

	private static final class RuntimePortletServletRequest extends HttpServletRequestWrapper {
		private final PortletName name;

		public RuntimePortletServletRequest(HttpServletRequest request, PortletName name) {
			super(request);
			this.name = name;
		}

		@Override
		public Object getAttribute(String name) {
			if (RuntimePortletServlet.PORTLET_NAME.equals(name)) {
				return this.name;
			}
			return super.getAttribute(name);
		}
	}
}
