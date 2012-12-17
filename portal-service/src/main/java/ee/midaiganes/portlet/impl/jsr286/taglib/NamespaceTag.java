package ee.midaiganes.portlet.impl.jsr286.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public class NamespaceTag extends BasePortletTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			getOut().print(getPortletResponse().getNamespace());
		} catch (IOException e) {
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
}
