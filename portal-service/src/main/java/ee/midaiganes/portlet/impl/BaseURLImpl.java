package ee.midaiganes.portlet.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.BaseURL;
import javax.portlet.PortletSecurityException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;

import ee.midaiganes.util.StringEscapeUtil;

public class BaseURLImpl implements BaseURL {
    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";

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
    private final HttpServletResponse response;
    private boolean secure;

    public BaseURLImpl(String host, int port, String path, boolean secure, HttpServletResponse response) {
        this.host = Preconditions.checkNotNull(host);
        this.port = port;
        this.path = Preconditions.checkNotNull(path);
        this.secure = secure;
        this.response = Preconditions.checkNotNull(response);
    }

    public BaseURLImpl(HttpServletRequest request, HttpServletResponse response) {
        this(request.getServerName(), request.getServerPort(), request.getRequestURI(), request.isSecure(), response);
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
        out.append(createURL(escapeXML));
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
            return createURL(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createURL(boolean escapeXml) throws IOException {
        StringBuilder w = new StringBuilder(256);
        appendHostAndPath(w);
        w.append('?');
        boolean addAnd = false;
        for (Map.Entry<String, String[]> entry : this.parameters.entrySet()) {
            for (String val : entry.getValue()) {
                if (addAnd) {
                    w.append('&');
                } else {
                    addAnd = true;
                }
                w.append(entry.getKey()).append('=').append(val);
            }
        }
        String url = w.toString();
        if (escapeXml) {
            url = StringEscapeUtil.escapeXml(new StringBuilder(url.length() + (this.parameters.size() * 5)), url).toString();
        }
        return response.encodeURL(url);
    }

    private void appendHostAndPath(Appendable sb) throws IOException {
        sb.append(secure ? HTTPS : HTTP).append(host);
        if ((secure && port != 443) || (!secure && port != 80)) {
            sb.append(':');
            sb.append(Integer.toString(port));
        }
        sb.append(path);
    }

    protected String[] getParameter(String name) {
        return this.parameters.get(name);
    }
}
