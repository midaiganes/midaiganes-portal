package ee.midaiganes.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import ee.midaiganes.servlet.LayoutPortletServlet;

public class LayoutPortletRequest extends HttpServletRequestWrapper {
	private final long id;

	public LayoutPortletRequest(HttpServletRequest request, long id) {
		super(request);
		this.id = id;
	}

	@Override
	public Object getAttribute(String name) {
		if (LayoutPortletServlet.ID.equals(name)) {
			return id;
		}
		return super.getAttribute(name);
	}
}
