package ee.midaiganes.services.util;

import javax.portlet.PortletResponse;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletNamespace;
import ee.midaiganes.services.PortletInstanceRepository;

public class PortletInstanceUtil {

	public static PortletInstance getPortletInstance(PortletResponse response) {
		return getPortletInstance(new PortletNamespace(response.getNamespace()));
	}

	public static PortletInstance getPortletInstance(PortletNamespace namespace) {
		return BeanRepositoryUtil.getBean(PortletInstanceRepository.class).getPortletInstance(namespace.getPortletName(), namespace.getWindowID());
	}
}
