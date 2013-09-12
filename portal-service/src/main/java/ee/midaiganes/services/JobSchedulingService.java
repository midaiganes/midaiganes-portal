package ee.midaiganes.services;

import javax.annotation.Resource;

@Resource(name = "JobSchedulingService")
public class JobSchedulingService {
	private static final JobSchedulingService jobSchedulingService = new JobSchedulingService();

	private JobSchedulingService() {
	}

	public static JobSchedulingService getInstance() {
		return JobSchedulingService.jobSchedulingService;
	}

	public void runAtInterval(IntervalJob job) {
		runAtInterval(job, true);
	}

	public void runAtInterval(IntervalJob job, boolean daemon) {
		createJobThread(new JobRunner(job), daemon).start();
	}

	private Thread createJobThread(JobRunner job, boolean daemon) {
		Thread t = new Thread(job, "job-runner");
		if (daemon != t.isDaemon()) {
			t.setDaemon(daemon);
		}
		return t;
	}

	private static class JobRunner implements Runnable {
		private final IntervalJob job;

		public JobRunner(IntervalJob job) {
			this.job = job;
		}

		@Override
		public void run() {
			while (job.runAgain()) {
				try {
					Thread.sleep(job.getIntervalInMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				job.run();
			}
		}

	}

	public static interface Job extends Runnable {
		boolean runAgain();
	}

	public static interface IntervalJob extends Job {
		long getIntervalInMillis();
	}

	public static abstract class AbstractIntervalJob implements IntervalJob {
		@Override
		public boolean runAgain() {
			return true;
		}
	}
}
