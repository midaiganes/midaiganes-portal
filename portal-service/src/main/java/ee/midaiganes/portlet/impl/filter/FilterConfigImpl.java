package ee.midaiganes.portlet.impl.filter;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletContext;
import javax.portlet.filter.FilterConfig;

public class FilterConfigImpl implements FilterConfig {
	private final String filterName;
	private final PortletContext portletContext;
	private final ConcurrentHashMap<String, String> initParameters;

	public FilterConfigImpl(String filterName, PortletContext portletContext, ConcurrentHashMap<String, String> initParameters) {
		this.filterName = filterName;
		this.portletContext = portletContext;
		this.initParameters = initParameters;
	}

	@Override
	public String getFilterName() {
		return filterName;
	}

	@Override
	public PortletContext getPortletContext() {
		return portletContext;
	}

	@Override
	public String getInitParameter(String name) {
		return initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return initParameters.keys();
	}
}
