/**
 * @see: http://javarevisited.blogspot.com/2011/08/enum-in-java-example-tutorial.html
 */
package intellif.enums;

/**
 * The Enum AlarmThresholds.
 *
 * @author yangboz
 */
public enum AlarmThresholds {

	// 重要告警阈值
	// 重要告警告警类型
	// 中等告警阈值
	// 中等告警告警类型
	// 轻微告警阈值
	// 轻微告警告警类型
	HIGH(10), MIDDLE(0), LOW(-10);

	private int _value;

	AlarmThresholds(int value) {
		this._value = value;
	}

	public int getValue() {
		return _value;
	}
}
