package ee.midaiganes.secureservices;

import java.util.Locale;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import ee.midaiganes.beans.PortalBeans;
import ee.midaiganes.portal.layoutportlet.LayoutPortlet;
import ee.midaiganes.portal.permission.PermissionRepository;
import ee.midaiganes.portal.portletinstance.PortletInstance;
import ee.midaiganes.portlet.app.PortletApp;
import ee.midaiganes.services.PortletRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

@Resource(name = PortalBeans.SECURE_PORTLET_REPOSITORY)
public class SecurePortletRepository {

    private final PortletRepository portletRepository;

    private final PermissionRepository permissionRepository;

    @Inject
    public SecurePortletRepository(PortletRepository portletRepository, PermissionRepository permissionRepository) {
        this.portletRepository = portletRepository;
        this.permissionRepository = permissionRepository;
    }

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
