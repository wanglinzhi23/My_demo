package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;
import intellif.utils.FileUtil;

/**
 * The Class TaskInfo.
 */
@Entity
@Table(name = GlobalConsts.T_NAME_JUZHU_INFO,schema=GlobalConsts.INTELLIF_STATIC)
//Notice: RMI object should be Serializable.
public class JuZhuInfo extends InfoBase implements Serializable {

	private static final long serialVersionUID = -2935079401470769534L;

	private static Logger LOG = LogManager.getLogger(JuZhuInfo.class);

    public JuZhuInfo() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using=ToStringSerializer.class)
    private long id;

    // 姓氏
    private String xs;

    // 身份证签发机关
    private String sfzqfjg;

    // 民族编号
    private String mz;

    // 身高
    private String sg;

    // 现居住地址
    private String xjzdz;

    // 编号
    private String rid;

    // 
    private String jggj;

    // 出生日期
    private Date csrq;

    // 姓名
    private String xm;

    // 头像图片地址
    private String photo;

    // 更新时间
    private Date gxsj;

    // 性别
    private String xb;

    // 地市名称
    private String dsmc;

    // 
    private String jgssx;

    // 公安唯一识别号
    private String rowkey;

    // 公民身份号码
    private String gmsfhm;

    // 手机号码
    private String sjhm;

    // 创建时间
    private Date cjsj;

    // 民族名称
    private String mzmc;
    
    // 图片Base64
	@Transient
    private String photoBase64;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getXs()
	{
		return xs;
	}

	public void setXs(String xs)
	{
		this.xs = xs;
	}

	public String getSfzqfjg()
	{
		return sfzqfjg;
	}

	public void setSfzqfjg(String sfzqfjg)
	{
		this.sfzqfjg = sfzqfjg;
	}

	public String getMz()
	{
		return mz;
	}

	public void setMz(String mz)
	{
		this.mz = mz;
	}

	public String getSg()
	{
		return sg;
	}

	public void setSg(String sg)
	{
		this.sg = sg;
	}

	public String getXjzdz()
	{
		return xjzdz;
	}

	public void setXjzdz(String xjzdz)
	{
		this.xjzdz = xjzdz;
	}

	public String getRid()
	{
		return rid;
	}

	public void setRid(String rid)
	{
		this.rid = rid;
	}

	public String getJggj()
	{
		return jggj;
	}

	public void setJggj(String jggj)
	{
		this.jggj = jggj;
	}

	

	public String getXm()
	{
		return xm;
	}

	public void setXm(String xm)
	{
		this.xm = xm;
	}

	public String getPhoto()
	{
		return photo;
	}

	public void setPhoto(String photo)
	{
		this.photo = photo;
	}



	public String getXb()
	{
		return xb;
	}

	public void setXb(String xb)
	{
		this.xb = xb;
	}

	public String getDsmc()
	{
		return dsmc;
	}

	public void setDsmc(String dsmc)
	{
		this.dsmc = dsmc;
	}

	public String getJgssx()
	{
		return jgssx;
	}

	public void setJgssx(String jgssx)
	{
		this.jgssx = jgssx;
	}

	public String getRowkey()
	{
		return rowkey;
	}

	public void setRowkey(String rowkey)
	{
		this.rowkey = rowkey;
	}

	public String getGmsfhm()
	{
		return gmsfhm;
	}

	public void setGmsfhm(String gmsfhm)
	{
		this.gmsfhm = gmsfhm;
	}

	public String getSjhm()
	{
		return sjhm;
	}

	public void setSjhm(String sjhm)
	{
		this.sjhm = sjhm;
	}



	public Date getCsrq() {
		return csrq;
	}

	public void setCsrq(Date csrq) {
		this.csrq = csrq;
	}

	public Date getGxsj() {
		return gxsj;
	}

	public void setGxsj(Date gxsj) {
		this.gxsj = gxsj;
	}

	public Date getCjsj() {
		return cjsj;
	}

	public void setCjsj(Date cjsj) {
		this.cjsj = cjsj;
	}

	public String getMzmc()
	{
		return mzmc;
	}

	public void setMzmc(String mzmc)
	{
		this.mzmc = mzmc;
	}

	public String getPhotoBase64() {
//		try {
//			photoBase64 = FileUtil.GetImageStr(getPhoto());
//		} catch (Exception e) {
//			LOG.error(e.toString());
//		}
		return photoBase64;
	}

}
