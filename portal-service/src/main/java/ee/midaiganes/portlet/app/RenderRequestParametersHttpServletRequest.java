package ee.midaiganes.portlet.app;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import ee.midaiganes.portlet.impl.RenderParameterMap;

class RenderRequestParametersHttpServletRequest extends HttpServletRequestWrapper {
	private final RenderParameterMap map;

	public RenderRequestParametersHttpServletRequest(HttpServletRequest request, RenderParameterMap map) {
		super(request);
		this.map = map;
	}

	@Override
	public String getParameter(String name) {
		String[] value = map.get(name);
		return value != null && value.length > 0 ? value[0] : null;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(map.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] value = map.get(name);
		return value != null ? value.clone() : null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return map.getCopy();
	}
}