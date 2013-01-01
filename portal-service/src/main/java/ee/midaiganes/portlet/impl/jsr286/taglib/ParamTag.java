package ee.midaiganes.portlet.impl.jsr286.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public class ParamTag extends BasePortletTag {
	private String name;
	private String value;

	@Override
	public int doEndTag() throws JspException {
		try {
			Tag parent = getParent();
			if (parent instanceof PortletURLTag) {
				((PortletURLTag) parent).addParam(name, value);
			} else {
				throw new JspException("param tag must be in actionURL/renderURL");
			}
			return Tag.EVAL_PAGE;
		} finally {
			release();
		}
	}

	@Override
	public void release() {
		this.name = null;
		this.value = null;
		super.release();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
