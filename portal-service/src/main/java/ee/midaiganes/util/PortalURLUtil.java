package ee.midaiganes.util;

public class PortalURLUtil {

	/**
	 * @param friendlyURL
	 * @return root context path + friendly URL
	 */
	public static String getFullURLByFriendlyURL(String friendlyURL) {
		return PortalUtil.getPortalContextPath() + friendlyURL;
	}
}
