package com.intellif.bankmatch.executor;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FunctionUtils {
	public static Object waitTillThreadFinish(Future<?> thread) {
		try {
			return thread.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return Optional.ofNullable(null);
	}
}
