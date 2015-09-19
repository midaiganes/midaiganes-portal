package ee.midaiganes.portlet.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.util.StringEscapeUtil;
import ee.midaiganes.util.StringPool;

public class BaseURLImpl implements BaseURL {
    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";
    private static final String PORT_8080 = ":8080";

    private static final class ParametersMap extends TreeMap<String, String[]> {
        private static final long serialVersionUID = 1L;

        private Map<String, String[]> cloneParameters() {
            Map<String, String[]> clone = new TreeMap<>();
            for (Map.Entry<String, String[]> entry : this.entrySet()) {
                clone.put(entry.getKey(), entry.getValue().clone());
            }
            return clone;
        }
    }

    private final ParametersMap parameters = new ParametersMap();
    private final String host;
    private final int port;
    private final String path;
    private boolean secure;

    public BaseURLImpl(String host, int port, String path, boolean secure) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.secure = secure;
    }

    public BaseURLImpl(PortletRequest request) {
        this(request, StringPool.SLASH);
    }

    public BaseURLImpl(PortletRequest request, String path) {
        this(request.getServerName(), request.getServerPort(), path, request.isSecure());
    }

    public BaseURLImpl(HttpServletRequest request) {
        this(request.getServerName(), request.getServerPort(), request.getRequestURI(), request.isSecure());
    }

    private void appendHostAndPath(Appendable sb) throws IOException {
        sb.append(secure ? HTTPS : HTTP).append(host);
        if (secure || port != 80) {
            switch (port) {
                case 8080:
                    sb.append(PORT_8080);
                    break;
                default:
                    sb.append(':');
                    sb.append(Integer.toString(port));
            }
        }
        sb.append(path);
    }

    @Override
    public void setParameter(String name, String value) {
        if (value == null) {
            parameters.remove(name);
        } else {
            parameters.put(name, new String[] { value });
        }
    }

    @Override
    public void setParameter(String name, String[] values) {
        if (values == null) {
            parameters.remove(name);
        } else {
            parameters.put(name, values.clone());
        }
    }

    @Override
    public void setParameters(Map<String, String[]> parameters) {
        this.parameters.clear();
        if (parameters != null) {
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                setParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setSecure(boolean secure) throws PortletSecurityException {
        this.secure = secure;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters.cloneParameters();
    }

    @Override
    public void write(Writer out) throws IOException {
        write(out, true);
    }

    @Override
    public void write(Writer out, boolean escapeXML) throws IOException {
        createURL(out, escapeXML);
    }

    @Override
    public void addProperty(String key, String value) {
        // TODO
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setProperty(String key, String value) {
        // TODO
        throw new RuntimeException("not implemented");
    }

    @Override
    public String toString() {
        try {
            return createURL(new StringBuilder(256), false).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <A extends Appendable> A createURL(A w, boolean escapeXml) throws IOException {
        appendHostAndPath(w);
        w.append('?');
        boolean addAnd = false;
        for (Map.Entry<String, String[]> entry : this.parameters.entrySet()) {
            for (String val : entry.getValue()) {
                if (addAnd) {
                    if (escapeXml) {
                        StringEscapeUtil.escapeXml(w, "&");
                    } else {
                        w.append('&');
                    }
                } else {
                    addAnd = true;
                }
                if (escapeXml) {
                    StringEscapeUtil.escapeXml(w, entry.getKey()).append('=');
                    StringEscapeUtil.escapeXml(w, val);
                } else {
                    w.append(entry.getKey()).append('=').append(val);
                }
            }
        }
        return w;
    }

    protected String[] getParameter(String name) {
        return this.parameters.get(name);
    }
}
