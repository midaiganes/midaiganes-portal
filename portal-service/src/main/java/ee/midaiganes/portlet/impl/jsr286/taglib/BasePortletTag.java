package ee.midaiganes.portlet.impl.jsr286.taglib;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import ee.midaiganes.util.PortletConstant;

public class BasePortletTag implements Tag {
	private Tag parent;
	private PageContext pageContext;

	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		return Tag.SKIP_BODY;
	}

	@Override
	public Tag getParent() {
		return parent;
	}

	@Override
	public void release() {
		this.parent = null;
		this.pageContext = null;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	@Override
	public void setParent(Tag parent) {
		this.parent = parent;
	}

	protected PageContext getPageContext() {
		return pageContext;
	}

	protected HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) pageContext.getRequest();
	}

	protected PortletRequest getPortletRequest() {
		return (PortletRequest) getHttpServletRequest().getAttribute(PortletConstant.JAVAX_PORTLET_REQUEST);
	}

	protected PortletResponse getPortletResponse() {
		return (PortletResponse) getHttpServletRequest().getAttribute(PortletConstant.JAVAX_PORTLET_RESPONSE);
	}

	protected PortletConfig getPortletConfig() {
		return (PortletConfig) getHttpServletRequest().getAttribute(PortletConstant.JAVAX_PORTLET_CONFIG);
	}

	protected JspWriter getOut() {
		return getPageContext().getOut();
	}
}
