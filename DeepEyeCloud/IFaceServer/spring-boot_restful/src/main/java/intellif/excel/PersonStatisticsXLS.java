package intellif.excel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: PersonStatistics.java
 * @Package intellif.excel
 * @Description
 * @date 2018 05-12 8:54.
 */
public class PersonStatisticsXLS {
	private static Logger log = LogManager.getLogger(PersonStatisticsXLS.class);
	/**
	 * 时间
	 */
	private String time;
	/**
	 *男性人数
	 */
	private Long male;
	/**
	 *女性人数
	 */
	private Long female;
	/**
	 * 儿童人数
	 */
	private Long child;
	/**
	 *少年人数
	 */
	private Long teens;
	/**
	 *青年人数
	 */
	private Long young;
	/**
	 *中年人数
	 */
	private Long middleAge;
	/**
	 *老年人数
	 */
	private Long oldAge;
	/**
	 *总人数
	 */
	private Long total;
	private Long unknownSexual;

	private Long unknownAge;
	public PersonStatisticsXLS(){

	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		PersonStatisticsXLS.log = log;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Long getMale() {
		return male;
	}

	public void setMale(Long male) {
		this.male = male;
	}

	public Long getFemale() {
		return female;
	}

	public void setFemale(Long female) {
		this.female = female;
	}

	public Long getChild() {
		return child;
	}

	public void setChild(Long child) {
		this.child = child;
	}

	public Long getTeens() {
		return teens;
	}

	public void setTeens(Long teens) {
		this.teens = teens;
	}

	public Long getYoung() {
		return young;
	}

	public void setYoung(Long young) {
		this.young = young;
	}

	public Long getMiddleAge() {
		return middleAge;
	}

	public void setMiddleAge(Long middleAge) {
		this.middleAge = middleAge;
	}

	public Long getOldAge() {
		return oldAge;
	}

	public void setOldAge(Long oldAge) {
		this.oldAge = oldAge;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getUnknownSexual() {
		return unknownSexual;
	}

	public void setUnknownSexual(Long unknownSexual) {
		this.unknownSexual = unknownSexual;
	}

	public Long getUnknownAge() {
		return unknownAge;
	}

	public void setUnknownAge(Long unknownAge) {
		this.unknownAge = unknownAge;
	}

}
