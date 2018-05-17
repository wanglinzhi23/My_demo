package intellif.mqtt;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

/**
 * see http://blog.webagesolutions.com/archives/1027
 */
@Component
@Scope(value = "singleton")
public class EventBusHelper {

//	private static EventBusService instance = new EventBusService();
//
//	public static EventBusService getInstance() {
//		return instance;
//	}

	private EventBus eventBus = null;

	public EventBusHelper() {
		eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
	}

	public void registerSubscriber(Object subscriber) {
		eventBus.register(subscriber);
	}

	public void unRegisterSubscriber(Object subscriber) {
		eventBus.unregister(subscriber);
	}

	public void postEvent(Object e) {
		eventBus.post(e);
	}
}
