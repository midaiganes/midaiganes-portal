package ee.midaiganes.portlet.impl.jsr286.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public class PropertyTag extends BasePortletTag {
    private String name;
    private String value;

    @Override
    public int doEndTag() throws JspException {
        try {
            PortletUrlPropertyTarget parent = getPortletURLTag();
            if (parent != null) {
                parent.addProperty(name, value);
            } else {
                throw new JspException("property tag must be in actionURL/renderURL/resourceURL");
            }
            return Tag.EVAL_PAGE;
        } finally {
            release();
        }
    }

    private PortletUrlPropertyTarget getPortletURLTag() {
        Tag tag = getParent();
        while (tag != null) {
            if (tag instanceof PortletUrlPropertyTarget) {
                return (PortletUrlPropertyTarget) tag;
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
