package com.intellif.bankmatch.executor;

import java.util.concurrent.ForkJoinPool;

public class ApplicationResource {
	private static ForkJoinPool THREAD_POOL;
	public static ForkJoinPool getThreadPool() {
		if (THREAD_POOL == null || THREAD_POOL.isShutdown() || THREAD_POOL.isTerminated() || THREAD_POOL.isTerminating()) {
			THREAD_POOL = null;
			THREAD_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 2);
		}
		return THREAD_POOL;
	}
}
