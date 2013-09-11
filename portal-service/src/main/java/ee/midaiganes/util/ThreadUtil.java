package ee.midaiganes.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
	private static final ThreadPoolExecutor executor;
	static {
		executor = new ThreadPoolExecutor(0, 1000, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100000));
	}

	public static void execute(Runnable r) {
		executor.execute(r);
	}
}
