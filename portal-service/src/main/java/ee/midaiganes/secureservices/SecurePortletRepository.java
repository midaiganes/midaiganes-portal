package ee.midaiganes.secureservices;

import java.util.Locale;

import javax.annotation.Resource;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.services.PermissionRepository;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

@Component(value = PortalConfig.SECURE_PORTLET_REPOSITORY)
public class SecurePortletRepository {

	@Resource(name = PortalConfig.PORTLET_REPOSITORY)
	private PortletRepository portletRepository;

	@Resource(name = PortalConfig.PERMISSION_REPOSITORY)
	private PermissionRepository permissionRepository;

	public PortletApp getPortletApp(long userId, PortletInstance portletInstance, PortletMode portletMode, WindowState windowState) throws PrincipalException {
		try {
			final String action = portletMode.toString().toUpperCase(Locale.US);
			if (permissionRepository.hasUserPermission(userId, portletInstance, action)) {
				return portletRepository.getPortletApp(portletInstance, portletMode, windowState);
			}
			throw new PrincipalException(userId, portletInstance, action);
		} catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public PortletApp getPortletApp(long userId, LayoutPortlet layoutPortlet, PortletMode portletMode, WindowState windowState) throws PrincipalException {
		return getPortletApp(userId, layoutPortlet.getPortletInstance(), portletMode, windowState);
	}
}
