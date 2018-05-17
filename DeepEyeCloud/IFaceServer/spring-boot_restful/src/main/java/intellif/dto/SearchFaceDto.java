package intellif.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SearchFaceDto implements Serializable {

	private static final long serialVersionUID = -203248779095704774L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	  // 节点ID
    private String nodeIds; //节点串,逗号拼接
    // 节点类型
    private String nodeType = "district";
	// 待检索人脸ID
	private long faceId;
	
	// 检索分值阈值
	private float scoreThreshold  = 0.92F;
	
	// 检索类型（0：重点人员库；1：布控抓拍人脸库；2：警局内非布控抓拍人脸库）
	private int type = 1;
	
	// 一组待检索人脸id，逗号分隔
	private String ids = "";
	
	// 合并方法类型（0：交集平均；1：并集平均）
	private int mergeType = 1;
	
	// 数据类型（0：blackdetail；1：faceinfo；2：faceinfo；3：cididetail；4：juzhudetail；5：otherdetail；6：otherdetail）
	private String dataType = "1";
	
	// 数据开始时间
	private String starttime;
	
	// 数据开始时间
	private String endtime;
	
	// 数据排序方式
	private String sort = "time";
	
	//摄像头ID集合
	private String[] cameraIds;
	
	//静态库ID集合
	private String[] bankIds;
	//周期日开始时间
	private String  dayStartTime;
	
	//周期日结束时间
	private String  dayEndTime;
	//星期几
	private String weekDay;
	//年龄
	private int age;
	//穿戴
	private int accessories;
	//种族
	private String race;
	//性别
	private int gender;
	//图片质量  0高质量图片 -1低质量图片
	private int quality;
	
	private String[] caseNumArray; //当type = 6警综人员时，传该值1－18

	private int forceSearch = 0;//是否为红名单强制搜索 0 非强制 1 强制
	
	private long policeStationId = 0l;
	
	private int returnType = 1; //1返回纯图片列表，2返回按摄像头列表集合
	
	private int page;
	
	private int pageSize;
	public String[] getCameraIds() {
		return cameraIds;
	}

	public void setCameraIds(String[] cameraIds) {
		this.cameraIds = cameraIds;
	}

	public String[] getBankIds() {
		return bankIds;
	}

	public void setBankIds(String[] bankIds) {
		this.bankIds = bankIds;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFaceId() {
		return faceId;
	}

	public void setFaceId(long faceId) {
		this.faceId = faceId;
	}

	public float getScoreThreshold() {
		return scoreThreshold;
	}

	public void setScoreThreshold(float scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public int getMergeType() {
		return mergeType;
	}

	public void setMergeType(int mergeType) {
		this.mergeType = mergeType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getSort()
	{
		return sort;
	}

	public void setSort(String sort)
	{
		this.sort = sort;
	}

	public String getDayStartTime() {
		return dayStartTime;
	}

	public void setDayStartTime(String dayStartTime) {
		this.dayStartTime = dayStartTime;
	}

	public String getDayEndTime() {
		return dayEndTime;
	}

	public void setDayEndTime(String dayEndTime) {
		this.dayEndTime = dayEndTime;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getAccessories() {
		return accessories;
	}

	public void setAccessories(int accessories) {
		this.accessories = accessories;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

    public String[] getCaseNumArray() {
        return caseNumArray;
    }

    public void setCaseNumArray(String[] caseNumArray) {
        this.caseNumArray = caseNumArray;
    }

    public int getForceSearch() {
        return forceSearch;
    }

    public void setForceSearch(int forceSearch) {
        this.forceSearch = forceSearch;
    }

    public long getPoliceStationId() {
        return policeStationId;
    }

    public void setPoliceStationId(long policeStationId) {
        this.policeStationId = policeStationId;
    }

 
    public String getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(String nodeIds) {
        this.nodeIds = nodeIds;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getReturnType() {
        return returnType;
    }

    public void setReturnType(int returnType) {
        this.returnType = returnType;
    }


}
