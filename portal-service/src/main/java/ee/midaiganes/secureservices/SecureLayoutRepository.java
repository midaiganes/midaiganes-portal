package ee.midaiganes.secureservices;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layout.LayoutRepository;
import ee.midaiganes.services.PermissionRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

@Component(value = PortalConfig.SECURE_LAYOUT_REPOSITORY)
public class SecureLayoutRepository {
	private static final Logger log = LoggerFactory.getLogger(SecureLayoutRepository.class);
	private static final String VIEW = "VIEW";
	private final LayoutRepository layoutRepository;
	private final PermissionRepository permissionRepository;

	public SecureLayoutRepository(LayoutRepository layoutRepository, PermissionRepository permissionRepository) {
		this.layoutRepository = layoutRepository;
		this.permissionRepository = permissionRepository;
	}

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
			if (layout == null) {
				return null;
			}
			if (permissionRepository.hasUserPermission(userId, layout, VIEW)) {
				return layout;
			}
		} catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
			throw new RuntimeException(e);
		}
		throw new PrincipalException(userId, layout, VIEW);
	}

	public Layout getHomeLayout(long userId, long layoutSetId) {
		for (Layout layout : layoutRepository.getChildLayouts(layoutSetId, null)) {
			try {
				if (permissionRepository.hasUserPermission(userId, layout, VIEW)) {
					return layout;
				}
			} catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
				log.debug(e.getMessage(), e);
			}
		}
		return null;
	}
}
