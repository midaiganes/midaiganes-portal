package ee.midaiganes.util;

public class PortalUtil {
	private static String portalContextPath;

	/**
	 * @deprecated use {@link PropsValues}
	 */
	@Deprecated
	public static String getPortalContextPath() {
		return portalContextPath;
	}

	/**
	 * The path starts with a / character but does not end with a / character.
	 * For servlets in the default (root) context, this method returns "".
	 */
	@Deprecated
	public synchronized static void setPortalContextPath(String portalContextPath) {
		if (PortalUtil.portalContextPath != null) {
			throw new IllegalStateException("Portal context path(" + PortalUtil.portalContextPath + ") is already set.");
		}
		PortalUtil.portalContextPath = portalContextPath;
	}
}
