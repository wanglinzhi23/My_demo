package intellif.chd.vo;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import intellif.chd.bean.FeatureItf;
import intellif.chd.consts.Constant;
import intellif.chd.util.FaceUtil;
import intellif.consts.GlobalConsts;

/**
 * 过滤人脸表
 * 
 * @author wyy
 */
@Entity
@Table(name = Constant.T_NAME_FILTER_FACE, schema = GlobalConsts.INTELLIF_BASE)
public class FilterFace implements Serializable, FeatureItf {

	/**
	 * 序列化版本
	 */
	private static final long serialVersionUID = 1L;

	// ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	// 人脸特征值
	@JsonIgnore
	protected byte[] feature;

	// 类型
	protected String kind;

	// 过滤阈值
	protected float threshold = GlobalConsts.DEFAULT_SCORE_THRESHOLD;

	// 数量
	protected int num;

	@Transient
	@JsonIgnore
	protected float[] featureFloat;

	/**
	 * 获取浮点数组的特征值
	 * 
	 * @return
	 */
	public float[] takeFeatureFloat() {
		if (null != featureFloat) {
			return featureFloat;
		}
		if (null == feature) {
			return null;
		}
		try {
			featureFloat = FaceUtil.byte2float(feature, 0, Constant.REAL_LENGTH_181 * 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		feature = null;
		return featureFloat;
	}

	/**
	 * 获取浮点数组的特征值
	 *
	 * @return
	 */
	public void evaluationFeature(float[] featureFloat) {
		if (null == featureFloat) {
			return ;
		}
		if (featureFloat.length < Constant.REAL_LENGTH_181) {
			return ;
		}
		feature = FaceUtil.float2byte(featureFloat);
	}


	/**
	 * 获取ID
	 * 
	 * @return ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取人脸特征值
	 * 
	 * @return 人脸特征值
	 */
	public byte[] getFeature() {
		return feature;
	}

	/**
	 * 设置人脸特征值
	 */
	public void setFeature(byte[] feature) {
		this.feature = feature;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * 设置类型
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * 获取过滤阈值
	 * 
	 * @return 过滤阈值
	 */
	public float getThreshold() {
		return threshold;
	}

	/**
	 * 设置过滤阈值
	 */
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("FilterFace{");
		sb.append("id=").append(id);
		sb.append(", kind='").append(kind).append('\'');
		sb.append(", threshold=").append(threshold);
		sb.append(", num='").append(num).append('\'');
		sb.append(", feature='").append(Arrays.toString(takeFeatureFloat())).append('\'');
		sb.append('}');
		return sb.toString();
	}
}