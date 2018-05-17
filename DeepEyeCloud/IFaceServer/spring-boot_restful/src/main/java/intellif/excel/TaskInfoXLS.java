package intellif.excel;

import org.apache.commons.lang.ArrayUtils;

import com.blogspot.na5cent.exom.annotation.Column;

public class TaskInfoXLS {
	
	@Column(name = "任务名称")
	private String name;// 任务名称

	@Column(name = "应用服务器名称")
	private String serverName;// 应用服务器名称
	
	private static final String SOURCE_TYPE_STRS[] = {"视频摄像头","视频文件","图片文件"};
	@Column(name = "数据源类型")
	private String sourceType;// 数据源类型,[视频摄像头,视频文件,图片文件]

	@Column(name = "数据源")
	private String sourceName;// 数据源ID

	@Column(name = "重点人员库名称")
	private String bankName;// 重点人员库名称

	@Column(name = "任务规则名称")
	private String ruleName;// 任务规则名称
	
	private static final String DECODE_MODE_STRS[] = {"抓怕模式","解码模式"};
	@Column(name = "工作模式")
	private String decodeModeName;// 解码模式

	@Override
	public String toString() {
		return "name: " + name + ",serverName: " + serverName + ", sourceType: " + sourceType + ",sourceName: " + sourceName + ",bankName: "
				+ bankName + ",ruleName: " + ruleName+",decodeModeName:"+decodeModeName;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getServerName() {
		return serverName;
	}


	public void setServerName(String serverName) {
		this.serverName = serverName;
	}


	public String getSourceType() {
		return sourceType;
	}
	
	public int getSourceTypeInt() {
		return ArrayUtils.indexOf(SOURCE_TYPE_STRS,sourceType);
	}


	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getRuleName() {
		return ruleName;
	}


	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}


	public String getSourceName() {
		return sourceName;
	}


	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}


	public String getDecodeModeName() {
		return decodeModeName;
	}
	
	public int getDecodeModeNameInt() {
		return ArrayUtils.indexOf(DECODE_MODE_STRS,decodeModeName);
	}

	public void setDecodeModeName(String decodeModeName) {
		this.decodeModeName = decodeModeName;
	}
}
