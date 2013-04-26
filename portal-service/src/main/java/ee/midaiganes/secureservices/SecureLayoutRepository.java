package ee.midaiganes.secureservices;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.model.Layout;
import ee.midaiganes.services.LayoutRepository;
import ee.midaiganes.services.PermissionRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

public class SecureLayoutRepository {
	private static final String VIEW = "VIEW";

	@Resource(name = RootApplicationContext.LAYOUT_REPOSITORY)
	private LayoutRepository layoutRepository;

	@Resource(name = PortalConfig.PERMISSION_REPOSITORY)
	private PermissionRepository permissionRepository;

	public List<Layout> getLayouts(long userId, long layoutSetId) {
		List<Layout> layouts = layoutRepository.getLayouts(layoutSetId);
		List<Layout> allowedLayouts = new ArrayList<>(layouts.size());
		try {
			for (Layout layout : layouts) {
				if (permissionRepository.hasUserPermission(userId, layout, VIEW)) {
					allowedLayouts.add(layout);
				}
			}
		} catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
			throw new RuntimeException(e);
		}

		return allowedLayouts;
	}

	public Layout getLayout(long userId, long layoutSetId, String friendlyUrl) throws PrincipalException {
		Layout layout = layoutRepository.getLayout(layoutSetId, friendlyUrl);
		try {
			if (permissionRepository.hasUserPermission(userId, layout, VIEW)) {
				return layout;
			}
		} catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
			throw new RuntimeException(e);
		}
		throw new PrincipalException(userId, layout, VIEW);
	}
}
