package intellif.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: PersonStatisticCount.java
 * @Package intellif.vo
 * @Description
 * @date 2018 05-04 9:08.
 */
@Entity
public class PersonStatisticCount implements  Serializable {
	@Id
	private Long id;
	/**
	 * 年龄<br/>
	 * <p>
	 *    儿童：1,2，少年 3，青年 4,5,6 ，中年 7,8，老年9
	 *
	 * </p>
	 */
	private Integer age;
	/**
	 * 0未知,1 男性, 2 女性
	 */
	private Integer gender;
	//总数
	private Long count;
	//时间
	private String time;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
