package intellif.dto;

import intellif.database.entity.CidInfo;
import intellif.database.entity.JuZhuInfo;
import intellif.database.entity.OtherInfo;

public class CidInfoDto extends CidInfo
{
	private static final long serialVersionUID = -362828047921620657L;
	
	private int datatype;
	private String datatypename;
	private String policeStationName;
	private String extendField;
	private String extendField1;
	private String extendField2;
	private String extendField3;
	private String extendField4;
	private String extendField5;

	public CidInfoDto(CidInfo cid)
	{
		setId(cid.getId());
		setXs(cid.getXs());
		setSfzqfjg(cid.getSfzqfjg());
		setMz(cid.getMz());
		setSg(cid.getSg());
		setXjzdz(cid.getXjzdz());
		setRid(cid.getRid());
		setJggj(cid.getJggj());
		setCsrq(cid.getCsrq());
		setXm(cid.getXm());
		setPhoto(cid.getPhoto());
		setGxsj(cid.getGxsj());
		setXb(cid.getXb());
		setDsmc(cid.getDsmc());
		setJgssx(cid.getJgssx());
		setRowkey(cid.getRowkey());
		setGmsfhm(cid.getGmsfhm());
		setSjhm(cid.getSjhm());
		setCjsj(cid.getCjsj());
		setMzmc(cid.getMzmc());
		setCreated(cid.getCreated());
		setUpdated(cid.getUpdated());
	}

	public CidInfoDto(JuZhuInfo cid)
	{
		setId(cid.getId());
		setXs(cid.getXs());
		setSfzqfjg(cid.getSfzqfjg());
		setMz(cid.getMz());
		setSg(cid.getSg());
		setXjzdz(cid.getXjzdz());
		setRid(cid.getRid());
		setJggj(cid.getJggj());
		setCsrq(cid.getCsrq());
		setXm(cid.getXm());
		setPhoto(cid.getPhoto());
		setGxsj(cid.getGxsj());
		setXb(cid.getXb());
		setDsmc(cid.getDsmc());
		setJgssx(cid.getJgssx());
		setRowkey(cid.getRowkey());
		setGmsfhm(cid.getGmsfhm());
		setSjhm(cid.getSjhm());
		setCjsj(cid.getCjsj());
		setMzmc(cid.getMzmc());
		setCreated(cid.getCreated());
		setUpdated(cid.getUpdated());
	}

	public CidInfoDto(OtherInfo cid)
	{
		setId(cid.getId());
		setXs(cid.getXs());
		setSfzqfjg(cid.getSfzqfjg());
		setMz(cid.getMz());
		setSg(cid.getSg());
		setXjzdz(cid.getXjzdz());
		setRid(cid.getRid());
		setJggj(cid.getJggj());
		setCsrq(cid.getCsrq());
		setXm(cid.getXm());
		setPhoto(cid.getPhoto());
		setGxsj(cid.getGxsj());
		setXb(cid.getXb());
		setDsmc(cid.getDsmc());
		setJgssx(cid.getJgssx());
		setRowkey(cid.getRowkey());
		setGmsfhm(cid.getGmsfhm());
		setSjhm(cid.getSjhm());
		setCjsj(cid.getCjsj());
		setMzmc(cid.getMzmc());
		setCreated(cid.getCreated());
		setUpdated(cid.getUpdated());
		this.datatype = cid.getDatatype();
		this.datatypename = cid.getDatatypename();
		this.extendField = cid.getExtendField();
		this.extendField1 = cid.getExtendField1();
		this.extendField2 = cid.getExtendField2();
		this.extendField3 = cid.getExtendField3();
		this.extendField4 = cid.getExtendField4();
		this.extendField5 = cid.getExtendField5();
	}

	// 人脸对象Id
    private String detailId;

	// 图片地址_人脸
    private String file;
    
    // 比对分值
    private float score;
    
    // 人脸图片Base64
    private String imageBase64;
    
    // 是否需要photo转Base64，该字段不序列化，内部使用，不返回前端
    private transient boolean needPhotoBase64 = false;

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		this.file = file;
	}

	public float getScore()
	{
		return score;
	}

	public void setScore(float score)
	{
		this.score = score;
	}

	public String getImageBase64() {
		return imageBase64;
	}

	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public boolean isNeedPhotoBase64() {
		return needPhotoBase64;
	}

	public void setNeedPhotoBase64(boolean needPhotoBase64) {
		this.needPhotoBase64 = needPhotoBase64;
	}

    public int getDatatype() {
        return datatype;
    }

    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }

    public String getDatatypename() {
        return datatypename;
    }

    public void setDatatypename(String datatypename) {
        this.datatypename = datatypename;
    }

    public String getExtendField() {
        return extendField;
    }

    public void setExtendField(String extendField) {
        this.extendField = extendField;
    }

    public String getExtendField1() {
        return extendField1;
    }

    public void setExtendField1(String extendField1) {
        this.extendField1 = extendField1;
    }

    public String getExtendField2() {
        return extendField2;
    }

    public void setExtendField2(String extendField2) {
        this.extendField2 = extendField2;
    }

    public String getExtendField3() {
        return extendField3;
    }

    public void setExtendField3(String extendField3) {
        this.extendField3 = extendField3;
    }

    public String getExtendField4() {
        return extendField4;
    }

    public void setExtendField4(String extendField4) {
        this.extendField4 = extendField4;
    }

    public String getExtendField5() {
        return extendField5;
    }

    public void setExtendField5(String extendField5) {
        this.extendField5 = extendField5;
    }

    public String getPoliceStationName() {
        return policeStationName;
    }

    public void setPoliceStationName(String policeStationName) {
        this.policeStationName = policeStationName;
    }
    
}