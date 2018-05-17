package intellif.dto;

import intellif.database.entity.PersonDetail;
import intellif.database.entity.EventInfo;
import intellif.database.entity.OtherInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventDto extends PersonDetail{
	private static final long serialVersionUID = 544784406183226182L;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	// 犯罪一级分类简称
	private String crimeName;
	
	// 排序用
	private Integer index;

	// 嫌疑人事件集
	private List<EventInfo> events;
	
	public EventDto(PersonDetail alarmPerson) {
		this.setId(alarmPerson.getId());
		this.setRealName(alarmPerson.getRealName());
		this.setBirthday(alarmPerson.getBirthday());
		this.setNation(alarmPerson.getNation());
		this.setRealGender(alarmPerson.getRealGender());
		this.setCid(alarmPerson.getCid());
		this.setAddress(alarmPerson.getAddress());
		this.setPhotoData(alarmPerson.getPhotoData());
		this.setCrimeType(alarmPerson.getCrimeType());
		this.setCrimeAddress(alarmPerson.getCrimeAddress());
		this.setDescription(alarmPerson.getDescription());
		this.setRuleId(alarmPerson.getRuleId());
		this.setIdentity(alarmPerson.getIdentity());
		this.setBankId(alarmPerson.getBankId());
        this.setStarttime(alarmPerson.getStarttime());
        this.setEndtime(alarmPerson.getEndtime());
        this.setStatus(alarmPerson.getStatus());
        this.setOwner(alarmPerson.getOwner());
        this.setOwnerStation(alarmPerson.getOwnerStation());
        this.setImportant(alarmPerson.getImportant());
        this.setArrest(alarmPerson.getArrest());
        this.setSimilarSuspect(alarmPerson.getSimilarSuspect());
        this.setInStation(alarmPerson.getInStation());
        this.setHistory(alarmPerson.getHistory());
        this.setDeleted(alarmPerson.getDeleted());
        this.setCreated(alarmPerson.getCreated());
        this.setType(alarmPerson.getType());
        this.setIsUrgent(alarmPerson.getIsUrgent());
        this.setFkType(alarmPerson.getFkType());                  //////  1.2.9反恐新增
	}

	public EventDto(OtherInfo person) {
		this.setId(person.getId());
		this.setRealName(person.getXm());
		this.setRealGender(Integer.valueOf(person.getXb()));
		this.setCid(person.getGmsfhm());
		try {
			this.setBirthday(person.getCsrq());
		} catch (Exception e) {
		}
		this.setPhotoData(person.getPhoto());
	}

	public String getCrimeName() {
		return crimeName;
	}

	public void setCrimeName(String crimeName) {
		this.crimeName = crimeName;
	}

	public List<EventInfo> getEvents() {
		return events;
	}

	public void setEvents(List<EventInfo> events) {
		this.events = events;
	}

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

	
}