package ee.midaiganes.secureservices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.Layout;
import ee.midaiganes.services.LayoutRepository;
import ee.midaiganes.services.PermissionRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

@Component(value = PortalConfig.SECURE_LAYOUT_REPOSITORY)
public class SecureLayoutRepository {
	private static final String VIEW = "VIEW";
	private static SecureLayoutRepository instance;
	private final LayoutRepository layoutRepository;
	private final PermissionRepository permissionRepository;

	public SecureLayoutRepository(LayoutRepository layoutRepository, PermissionRepository permissionRepository) {
		this.layoutRepository = layoutRepository;
		this.permissionRepository = permissionRepository;
	}

	public static SecureLayoutRepository getInstance() {
		return instance;
	}

	public static void setInstance(SecureLayoutRepository instance) {
		SecureLayoutRepository.instance = instance;
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
			if (permissionRepository.hasUserPermission(userId, layout, VIEW)) {
				return layout;
			}
		} catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
			throw new RuntimeException(e);
		}
		throw new PrincipalException(userId, layout, VIEW);
	}
}
