package intellif.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: PersonFlowCollectDto.java
 * @Package intellif.dto
 * @Description
 * @date 2018 05-04 11:55.
 */
public class PersonFlowCollectDto implements Serializable {
	private static final long serialVersionUID = -218931405739190052L;
	//总榜list
	private List<PersonStatisticCount> all;
	private List<PersonStatisticCount> male;
	private List<PersonStatisticCount> female;
	private List<PersonStatisticCount> child;
	private List<PersonStatisticCount> teens;
	private List<PersonStatisticCount> young;
	private List<PersonStatisticCount> middleAge;
	private List<PersonStatisticCount> oldAge;
	private List<PersonStatisticCount> unknownAge;
	private List<PersonStatisticCount> unknownSexual;
	private Long count;

	public PersonFlowCollectDto() {
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public PersonFlowCollectDto(List<PersonStatisticCount> all) {
		this.all = all;
	}

	public PersonFlowCollectDto(List<PersonStatisticCount> male, List<PersonStatisticCount> female) {
		this.male = male;
		this.female = female;
	}

	public PersonFlowCollectDto(List<PersonStatisticCount> child, List<PersonStatisticCount> teens, List<PersonStatisticCount> young, List<PersonStatisticCount> middleAge, List<PersonStatisticCount> oldAge) {
		this.child = child;
		this.teens = teens;
		this.young = young;
		this.middleAge = middleAge;
		this.oldAge = oldAge;
	}

	public List<PersonStatisticCount> getAll() {
		return all;
	}

	public void setAll(List<PersonStatisticCount> all) {
		this.all = all;
	}

	public List<PersonStatisticCount> getMale() {
		return male;
	}

	public void setMale(List<PersonStatisticCount> male) {
		this.male = male;
	}

	public List<PersonStatisticCount> getFemale() {
		return female;
	}

	public void setFemale(List<PersonStatisticCount> female) {
		this.female = female;
	}

	public List<PersonStatisticCount> getChild() {
		return child;
	}

	public void setChild(List<PersonStatisticCount> child) {
		this.child = child;
	}

	public List<PersonStatisticCount> getTeens() {
		return teens;
	}

	public void setTeens(List<PersonStatisticCount> teens) {
		this.teens = teens;
	}

	public List<PersonStatisticCount> getYoung() {
		return young;
	}

	public void setYoung(List<PersonStatisticCount> young) {
		this.young = young;
	}

	public List<PersonStatisticCount> getMiddleAge() {
		return middleAge;
	}

	public void setMiddleAge(List<PersonStatisticCount> middleAge) {
		this.middleAge = middleAge;
	}

	public List<PersonStatisticCount> getOldAge() {
		return oldAge;
	}

	public void setOldAge(List<PersonStatisticCount> oldAge) {
		this.oldAge = oldAge;
	}

	public List<PersonStatisticCount> getUnknownAge() {
		return unknownAge;
	}

	public void setUnknownAge(List<PersonStatisticCount> unknownAge) {
		this.unknownAge = unknownAge;
	}

	public List<PersonStatisticCount> getUnknownSexual() {
		return unknownSexual;
	}

	public void setUnknownSexual(List<PersonStatisticCount> unknownSexual) {
		this.unknownSexual = unknownSexual;
	}

}
