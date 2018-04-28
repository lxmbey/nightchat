package com.nightchat.common;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadPool {
	private static final Logger logger = LogManager.getLogger(ThreadPool.class);

	private static final Executor EXE = Executors.newWorkStealingPool(50);
	private static final ScheduledExecutorService scheduleExe = Executors.newScheduledThreadPool(1);

	public static void execute(Runnable command) {
		EXE.execute(() -> {
			try {
				command.run();
			} catch (Throwable e) {
				logger.error("线程未处理异常: {}:{}", Thread.currentThread().getName(), e.getMessage());
				logger.error("线程池未处理异常！！！", e);
			}
		});
	}

	public static void schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
		scheduleExe.schedule(runnable, delay, timeUnit);
	}

	public static void scheduleWithFixedDelay(Runnable runnable, long initDelay, long delay, TimeUnit timeUnit) {
		scheduleExe.scheduleWithFixedDelay(runnable, initDelay, delay, timeUnit);
	}

}
