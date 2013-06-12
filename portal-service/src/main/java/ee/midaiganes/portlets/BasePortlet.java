package ee.midaiganes.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class BasePortlet implements Portlet {
	private PortletConfig config;

	@Override
	public void init(PortletConfig config) throws PortletException {
		this.config = config;
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
	}

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
	}

	@Override
	public void destroy() {
		this.config = null;
	}

	protected PortletRequestDispatcher getPortletRequestDispatcher(String path) {
		return config.getPortletContext().getRequestDispatcher("/WEB-INF/portlet-jsp/" + path + ".jsp");
	}

	protected void include(String path, RenderRequest request, RenderResponse response) throws PortletException, IOException {
		getPortletRequestDispatcher(path).include(request, response);
	}

	protected PortletConfig getPortletConfig() {
		return config;
	}
}
