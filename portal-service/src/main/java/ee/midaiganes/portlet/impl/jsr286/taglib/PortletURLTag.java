package ee.midaiganes.portlet.impl.jsr286.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import ee.midaiganes.util.PortletModeUtil;
import ee.midaiganes.util.WindowStateUtil;

public abstract class PortletURLTag extends BasePortletTag {
	private String portletMode;
	private String secure;
	private String windowState;
	private String escapeXml;
	private String var;
	private Map<String, String> parameters;

	@Override
	public void release() {
		super.release();
		this.portletMode = null;
		this.secure = null;
		this.windowState = null;
		this.escapeXml = null;
		this.var = null;
		this.parameters = null;
	}

	@Override
	public int doStartTag() {
		return Tag.EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		PortletURL portletUrl = getPortletURL();
		try {
			portletUrl.setPortletMode(getPortletMode());
			portletUrl.setSecure(secure == null ? getPortletRequest().isSecure() : Boolean.getBoolean(secure));
			portletUrl.setWindowState(getWindowState());
			setParameters(portletUrl);
			if (var == null) {
				portletUrl.write(getOut(), Boolean.getBoolean(escapeXml));
			} else {
				getPageContext().setAttribute(var, portletUrl.toString());
			}
		} catch (PortletModeException e) {
			throw new JspException(e);
		} catch (PortletSecurityException e) {
			throw new JspException(e);
		} catch (WindowStateException e) {
			throw new JspException(e);
		} catch (IOException e) {
			throw new JspException(e);
		}

		return Tag.EVAL_PAGE;
	}

	private void setParameters(PortletURL portletUrl) {
		if (this.parameters != null) {
			for (Map.Entry<String, String> param : this.parameters.entrySet()) {
				portletUrl.setParameter(param.getKey(), param.getValue());
			}
		}
	}

	private PortletMode getPortletMode() {
		if (portletMode == null) {
			return getPortletRequest().getPortletMode();
		}
		return PortletModeUtil.getPortletMode(portletMode);
	}

	public WindowState getWindowState() {
		if (windowState == null) {
			return getPortletRequest().getWindowState();
		}
		return WindowStateUtil.getWindowState(windowState);
	}

	public void addParam(String name, String value) {
		if (this.parameters == null) {
			this.parameters = new HashMap<String, String>();
		}
		this.parameters.put(name, value);
	}

	public void setPortletMode(String portletMode) {
		this.portletMode = portletMode;
	}

	public void setSecure(String secure) {
		this.secure = secure;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setWindowState(String windowState) {
		this.windowState = windowState;
	}

	public void setEscapeXml(String escapeXml) {
		this.escapeXml = escapeXml;
	}

	public void setCopyCurrentRenderParameters(String copyCurrentRenderParameters) {
		// TODO
		throw new RuntimeException("not implemented");
	}

	protected MimeResponse getMimeResponse() {
		return (MimeResponse) getPortletResponse();
	}

	protected abstract PortletURL getPortletURL();
}
