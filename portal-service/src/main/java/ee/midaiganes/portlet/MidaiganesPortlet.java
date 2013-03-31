package ee.midaiganes.portlet;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PortletName;

public class MidaiganesPortlet implements MidaiganesPortletMBean, Portlet {
	private static final Logger log = LoggerFactory.getLogger(MidaiganesPortlet.class);
	private final Portlet portlet;
	private final AtomicLong successRenderCount = new AtomicLong();
	private final AtomicLong failedRenderCount = new AtomicLong();
	private final AtomicLong successActionCount = new AtomicLong();
	private final AtomicLong failedActionCount = new AtomicLong();
	private int activeRequestCount = 0;
	private boolean destroy = false;
	private boolean destroyed = false;
	private final ObjectName objectName;

	public MidaiganesPortlet(Portlet portlet, PortletName portletName) {
		this.portlet = portlet;
		try {
			objectName = new ObjectName("ee.midaiganes:type=Portlet,name=" + portletName.getFullName());
			getPlatformMBeanServer().registerMBean(this, objectName);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init(PortletConfig config) throws PortletException {
		portlet.init(config);
	}

	@Override
	public synchronized void destroy() {
		destroy = true;
		checkDestroy();
	}

	private synchronized void checkDestroy() {
		if (destroy && !destroyed && activeRequestCount == 0) {
			try {
				destroyed = true;
				portlet.destroy();
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			} finally {
				unregisterMBean();
			}
		}
	}

	private void unregisterMBean() {
		try {
			getPlatformMBeanServer().unregisterMBean(objectName);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	private synchronized void incrementActiveRequestCount() {
		if (destroyed) {
			throw new RuntimeException("portlet is destroyed");
		}
		activeRequestCount++;
	}

	private synchronized void decrementActiveRequestCountAndCheckDestroy() {
		activeRequestCount--;
		checkDestroy();
	}

	public boolean isResourceServingPortlet() {
		return portlet instanceof ResourceServingPortlet;
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		// TODO
		((ResourceServingPortlet) portlet).serveResource(request, response);
	}

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		try {
			incrementActiveRequestCount();
			portlet.render(request, response);
			successRenderCount.incrementAndGet();
		} catch (PortletException | IOException | RuntimeException e) {
			failedRenderCount.incrementAndGet();
			throw e;
		} finally {
			decrementActiveRequestCountAndCheckDestroy();
		}
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		try {
			incrementActiveRequestCount();
			portlet.processAction(request, response);
			successActionCount.incrementAndGet();
		} catch (PortletException | IOException | RuntimeException e) {
			failedActionCount.incrementAndGet();
			throw e;
		} finally {
			decrementActiveRequestCountAndCheckDestroy();
		}
	}

	@Override
	public long getSuccessRenderCount() {
		return successRenderCount.get();
	}

	@Override
	public long getFailedRenderCount() {
		return failedRenderCount.get();
	}

	@Override
	public long getSuccessActionCount() {
		return successActionCount.get();
	}

	@Override
	public long getFailedActionCount() {
		return failedActionCount.get();
	}

	@Override
	public synchronized int getActiveRequestCount() {
		return activeRequestCount;
	}
}