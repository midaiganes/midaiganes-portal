package ee.midaiganes.services.util;

import javax.annotation.Nonnull;

import ee.midaiganes.beans.Utils;
import ee.midaiganes.portal.permission.PermissionService;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

public class PermissionUtil {
    private static PermissionService getService() {
        return Utils.getInstance().getInstance(PermissionService.class);
    }

    public static boolean hasUserPermission(long userId, @Nonnull String resource, long resourcePrimKey, String action) throws ResourceNotFoundException,
            ResourceActionNotFoundException {
        return getService().hasUserPermission(userId, resource, resourcePrimKey, action);
    }
}
