package ee.midaiganes.util;

import com.google.common.primitives.Longs;

public class PortalUtil {

    public static boolean isSuperAdminUser(long userId) {
        return Longs.contains(PropsValues.SUPERADMIN_USER_IDS, userId);
    }
}
