package ee.midaiganes.services.util;

import ee.midaiganes.beans.BeanUtil;
import ee.midaiganes.model.PortalResource;
import ee.midaiganes.services.PermissionRepository;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

public class PermissionUtil {
	private static PermissionRepository getRepository() {
		return BeanUtil.getBean(PermissionRepository.class);
	}

	public static boolean hasUserPermission(long userId, String resource, long resourcePrimKey, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return getRepository().hasUserPermission(userId, resource, resourcePrimKey, action);
	}

	public static boolean hasUserResourcePermission(long userId, PortalResource resource, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return getRepository().hasUserPermission(userId, resource.getResource(), resource.getId(), action);
	}
}
