package ee.midaiganes.portlet;

public interface MidaiganesPortletMBean {
	long getSuccessRenderCount();

	long getFailedRenderCount();

	long getSuccessActionCount();

	long getFailedActionCount();

	int getActiveRequestCount();
}
