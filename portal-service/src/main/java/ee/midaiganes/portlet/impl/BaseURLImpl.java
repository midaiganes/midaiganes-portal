package ee.midaiganes.portlet.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.util.FastStringWriter;

public class BaseURLImpl implements BaseURL {
	private static final class ParametersMap extends HashMap<String, String[]> {
		private static final long serialVersionUID = 1L;

		private Map<String, String[]> cloneParameters() {
			Map<String, String[]> clone = new HashMap<String, String[]>();
			for (Map.Entry<String, String[]> entry : this.entrySet()) {
				clone.put(entry.getKey(), entry.getValue().clone());
			}
			return clone;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(BaseURLImpl.class);
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
		this(request, "/");
	}

	public BaseURLImpl(PortletRequest request, String path) {
		this(request.getServerName(), request.getServerPort(), path, request.isSecure());
	}

	public BaseURLImpl(HttpServletRequest request) {
		this(request.getServerName(), request.getServerPort(), request.getRequestURI(), request.isSecure());
	}

	private String getHostAndPath() {
		return (secure ? "https://" : "http://") + host + (!secure && port == 80 ? "" : ":" + port) + path;
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
		if (!escapeXML) {
			createURL(out);
		} else {
			log.warn("escapeXML");
			// TODO
			createURL(out);
		}
	}

	@Override
	public void addProperty(String key, String value) {
		// TODO Auto-generated method stub
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setProperty(String key, String value) {
		// TODO Auto-generated method stub
		throw new RuntimeException("not implemented");
	}

	@Override
	public String toString() {
		try (FastStringWriter sw = new FastStringWriter()) {
			createURL(sw);
			return sw.getString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void createURL(Writer w) throws IOException {
		w.append(getHostAndPath());
		w.append("?");
		boolean addAnd = false;
		for (Map.Entry<String, String[]> entry : this.parameters.entrySet()) {
			for (String val : entry.getValue()) {
				if (addAnd) {
					w.append("&");
				} else {
					addAnd = true;
				}
				w.append(entry.getKey()).append("=").append(val);
			}
		}
	}

	protected String[] getParameter(String name) {
		return this.parameters.get(name);
	}
}
