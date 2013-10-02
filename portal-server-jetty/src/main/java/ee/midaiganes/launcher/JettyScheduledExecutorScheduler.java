package ee.midaiganes.launcher;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.thread.Scheduler;

/**
 * @see org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
 * **/
public class JettyScheduledExecutorScheduler extends AbstractLifeCycle implements Scheduler {
	private final String name;
	private final boolean daemon = false;
	private volatile ScheduledThreadPoolExecutor scheduler;

	public JettyScheduledExecutorScheduler() {
		name = JettyScheduledExecutorScheduler.class.getName();
	}

	@Override
	protected void doStart() throws Exception {
		scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, name + (r == null ? "-null" : r.getClass().getName()));
				thread.setDaemon(daemon);
				return thread;
			}
		});
		scheduler.setRemoveOnCancelPolicy(true);
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		scheduler.shutdownNow();
		super.doStop();
		scheduler = null;
	}

	@Override
	public Task schedule(Runnable task, long delay, TimeUnit unit) {
		ScheduledFuture<?> result = scheduler.schedule(task, delay, unit);
		return new ScheduledFutureTask(result);
	}

	private class ScheduledFutureTask implements Task {
		private final ScheduledFuture<?> scheduledFuture;

		public ScheduledFutureTask(ScheduledFuture<?> scheduledFuture) {
			this.scheduledFuture = scheduledFuture;
		}

		@Override
		public boolean cancel() {
			return scheduledFuture.cancel(false);
		}
	}
}
