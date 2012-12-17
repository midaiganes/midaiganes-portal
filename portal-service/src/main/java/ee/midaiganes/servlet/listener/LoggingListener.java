package ee.midaiganes.servlet.listener;

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.MDC;

import ee.midaiganes.util.StringPool;

public class LoggingListener implements HttpSessionListener, ServletRequestListener {

	private static final AtomicLong requestId = new AtomicLong();
	private static final String REQUEST_ID = "requestId";
	private static final String SESSION_ID = "sessionId";
	private static final String ARROW = "->";

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		MDC.put(SESSION_ID, MDC.get(SESSION_ID) + ARROW + se.getSession().getId());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		MDC.put(SESSION_ID, MDC.get(SESSION_ID) + ARROW + StringPool.MINUS);
	}

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		MDC.clear();
	}

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		MDC.put(REQUEST_ID, Long.toString(requestId.incrementAndGet()));
		MDC.put(SESSION_ID, getSessionIdIfExists(sre.getServletRequest()));
	}

	private String getSessionIdIfExists(ServletRequest request) {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession(false);
			if (session != null) {
				return session.getId();
			}
		}
		return StringPool.MINUS;
	}
}
