package intellif.events;

//@see http://www.javacodegeeks.com/2012/11/google-guava-eventbus-for-event-programming.html
public class MqttMessageEvent {
	private String message;
	
	public String getMessage() {
		return message;
	}

	public MqttMessageEvent(String message) {
		this.message = message;
	}
}
