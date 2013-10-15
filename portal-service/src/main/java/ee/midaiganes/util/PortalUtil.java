package ee.midaiganes.util;

public class PortalUtil {

    public static boolean isSuperAdminUser(long userId) {
        return ArrayUtil.contains(PropsValues.SUPERADMIN_USER_IDS, userId);
    }
}
