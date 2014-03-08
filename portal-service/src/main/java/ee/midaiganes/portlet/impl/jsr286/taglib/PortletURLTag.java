package ee.midaiganes.portlet.impl.jsr286.taglib;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import ee.midaiganes.util.StringUtil;
import ee.midaiganes.util.WindowStateUtil;

public abstract class PortletURLTag extends BasePortletTag implements PortletUrlParamTarget, PortletUrlPropertyTarget {
    private String portletMode;
    private String secure;
    private String windowState;
    private String escapeXml;
    private String var;
    private Map<String, String> parameters;
    private Map<String, List<String>> properties;

    @Override
    public void release() {
        this.portletMode = null;
        this.secure = null;
        this.windowState = null;
        this.escapeXml = null;
        this.var = null;
        this.parameters = null;
        this.properties = null;
        super.release();
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
            portletUrl.setSecure(isSecure());
            portletUrl.setWindowState(getWindowState());
            setParameters(portletUrl);
            addProperties(portletUrl);
            if (var == null) {
                portletUrl.write(getOut(), isEscapeXml());
            } else {
                CharArrayWriter caw = new CharArrayWriter(128);
                portletUrl.write(caw, isEscapeXml());
                getPageContext().setAttribute(var, caw.toString());
            }
        } catch (PortletModeException e) {
            throw new JspException(e);
        } catch (PortletSecurityException e) {
            throw new JspException(e);
        } catch (WindowStateException e) {
            throw new JspException(e);
        } catch (IOException e) {
            throw new JspException(e);
        } finally {
            release();
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

    private void addProperties(PortletURL portletUrl) {
        if (this.properties != null) {
            for (Map.Entry<String, List<String>> property : this.properties.entrySet()) {
                String key = property.getKey();
                for (String value : property.getValue()) {
                    portletUrl.addProperty(key, value);
                }
            }
        }
    }

    protected boolean isEscapeXml() {
        return StringUtil.isEmpty(escapeXml) || Boolean.parseBoolean(escapeXml);
    }

    protected PortletMode getPortletMode() {
        if (portletMode == null) {
            return getPortletRequest().getPortletMode();
        }
        return PortletModeUtil.getPortletMode(portletMode);
    }

    protected WindowState getWindowState() {
        if (windowState == null) {
            return getPortletRequest().getWindowState();
        }
        return WindowStateUtil.getWindowState(windowState);
    }

    protected boolean isSecure() {
        return secure == null ? getPortletRequest().isSecure() : Boolean.parseBoolean(secure);
    }

    @Override
    public void addParam(String name, String value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(name, value);
    }

    @Override
    public void addProperty(String name, String value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        List<String> values = this.properties.get(name);
        if (values == null) {
            values = new ArrayList<>();
            this.properties.put(name, values);
        }
        values.add(value);
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
        if (Boolean.parseBoolean(copyCurrentRenderParameters)) {
            // TODO
        }
        throw new RuntimeException("not implemented");
    }

    protected MimeResponse getMimeResponse() {
        return (MimeResponse) getPortletResponse();
    }

    protected abstract PortletURL getPortletURL();
}
