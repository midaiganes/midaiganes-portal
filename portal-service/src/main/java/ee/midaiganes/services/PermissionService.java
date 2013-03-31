package ee.midaiganes.services;

import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;

public interface PermissionService {
	boolean hasPermission(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String action) throws ResourceActionNotFoundException;

	void setPermissions(long resource1, long resource1PrimKey, long resource2, long resource2PrimKey, String action[], boolean hasPermission[])
			throws ResourceActionNotFoundException;
}
