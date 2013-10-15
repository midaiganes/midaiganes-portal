package ee.midaiganes.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public class SimpleTag implements Tag {
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
		return (HttpServletRequest) getPageContext().getRequest();
	}

	protected HttpServletResponse getHttpServletResponse() {
		return (HttpServletResponse) getPageContext().getResponse();
	}
}
