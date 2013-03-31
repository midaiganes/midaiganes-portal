package ee.midaiganes.services.util;

import javax.portlet.PortletResponse;

import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletNamespace;
import ee.midaiganes.services.PortletInstanceRepository;

public class PortletInstanceUtil {
	private static PortletInstanceRepository portletInstanceRepository;

	public static void setPortletInstanceRepository(PortletInstanceRepository portletInstanceRepository) {
		PortletInstanceUtil.portletInstanceRepository = portletInstanceRepository;
	}

	public static PortletInstance getPortletInstance(PortletResponse response) {
		return getPortletInstance(new PortletNamespace(response.getNamespace()));
	}

	public static PortletInstance getPortletInstance(PortletNamespace namespace) {
		return portletInstanceRepository.getPortletInstance(namespace.getPortletName(), namespace.getWindowID());
	}
}
