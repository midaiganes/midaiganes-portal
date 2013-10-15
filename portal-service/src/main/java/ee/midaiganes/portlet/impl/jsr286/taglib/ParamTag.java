package ee.midaiganes.portlet.impl.jsr286.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public class ParamTag extends BasePortletTag {
	private String name;
	private String value;

	@Override
	public int doEndTag() throws JspException {
		try {
			PortletUrlParamTarget parent = getPortletURLTag();
			if (parent != null) {
				parent.addParam(name, value);
			} else {
				throw new JspException("param tag must be in actionURL/renderURL/resourceURL");
			}
			return Tag.EVAL_PAGE;
		} finally {
			release();
		}
	}

	private PortletUrlParamTarget getPortletURLTag() {
		Tag tag = getParent();
		while (tag != null) {
			if (tag instanceof PortletUrlParamTarget) {
				return (PortletUrlParamTarget) tag;
			}
			tag = tag.getParent();
		}
		return null;
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
