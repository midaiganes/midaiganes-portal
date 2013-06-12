package ee.midaiganes.portlet.impl.jsr286.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.MimeResponse;
import javax.portlet.PortletSecurityException;
import javax.portlet.ResourceURL;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public class ResourceURLTag extends BasePortletTag implements PortletUrlParamTarget {
	private String var;
	private Map<String, String> parameters;
	private String escapeXml;
	private String secure;

	@Override
	public void release() {
		this.secure = null;
		this.escapeXml = null;
		this.var = null;
		this.parameters = null;
		super.release();
	}

	@Override
	public int doStartTag() {
		return Tag.EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		ResourceURL portletUrl = ((MimeResponse) getPortletResponse()).createResourceURL(); // getPortletURL();
		try {
			portletUrl.setSecure(isSecure());
			setParameters(portletUrl);
			if (var == null) {
				portletUrl.write(getOut(), isEscapeXml());
			} else {
				getPageContext().setAttribute(var, portletUrl.toString());
			}
		} catch (IOException e) {
			throw new JspException(e);
		} catch (PortletSecurityException e) {
			throw new JspException(e);
		} finally {
			release();
		}

		return Tag.EVAL_PAGE;
	}

	protected boolean isEscapeXml() {
		return Boolean.parseBoolean(escapeXml);
	}

	protected boolean isSecure() {
		return secure == null ? getPortletRequest().isSecure() : Boolean.parseBoolean(secure);
	}

	private void setParameters(ResourceURL portletUrl) {
		if (this.parameters != null) {
			for (Map.Entry<String, String> param : this.parameters.entrySet()) {
				portletUrl.setParameter(param.getKey(), param.getValue());
			}
		}
	}

	@Override
	public void addParam(String name, String value) {
		if (this.parameters == null) {
			this.parameters = new HashMap<String, String>();
		}
		this.parameters.put(name, value);
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setEscapeXml(String escapeXml) {
		this.escapeXml = escapeXml;
	}
}
