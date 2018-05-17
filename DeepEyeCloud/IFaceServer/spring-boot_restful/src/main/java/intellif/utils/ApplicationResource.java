package intellif.utils;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationResource implements ApplicationContextAware {

	// Spring应用上下文环境
	private static ApplicationContext applicationContext;

	public static final ForkJoinPool THREAD_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 4);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		ApplicationResource.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}


	public static <T> T getBean(Class<T> tClass) {
		return applicationContext.getBean(tClass);
	}
}
