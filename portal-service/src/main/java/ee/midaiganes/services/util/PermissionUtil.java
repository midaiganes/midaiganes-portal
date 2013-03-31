package ee.midaiganes.services.util;

import ee.midaiganes.model.PortalResource;
import ee.midaiganes.services.PermissionRepository;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

public class PermissionUtil {
	private static PermissionRepository permissionRepository;

	public static void setPermissionRepository(PermissionRepository permissionRepository) {
		if (permissionRepository == null) {
			throw new IllegalArgumentException("PermissionRepository is null");
		}
		PermissionUtil.permissionRepository = permissionRepository;
	}

	public static boolean hasUserPermission(long userId, String resource, long resourcePrimKey, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return permissionRepository.hasUserPermission(userId, resource, resourcePrimKey, action);
	}

	public static boolean hasUserResourcePermission(long userId, PortalResource resource, String action) throws ResourceNotFoundException,
			ResourceActionNotFoundException {
		return permissionRepository.hasUserPermission(userId, resource.getResource(), resource.getId(), action);
	}
}
