package intellif.utils;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 
 * @author yktang, V1.1.2
 *
 */
public class FunctionUtil {
	
	//等待线程池中单个线程结束
	public static Object waitTillThreadFinish(Future<?> thread) {
		try {
			return thread.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return Optional.ofNullable(null);
	}
}
