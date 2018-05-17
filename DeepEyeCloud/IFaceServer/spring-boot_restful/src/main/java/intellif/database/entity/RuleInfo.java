package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_RULE_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class RuleInfo extends InfoBase implements Serializable,Cloneable{
	
	private static final long serialVersionUID = -2363436111224977748L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 规则名称
	private String ruleName;
	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	// 规则描述
	private String ruleDescription;
	
	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}

//	// 重要告警阈值
//	private double importantAlarmThreshold;
//	
//	// 重要告警告警类型
//	private int importantAlarmType;
//	
//	// 中等告警阈值
//	private double mediumAlarmThreshold;
//	
//	// 中等告警告警类型
//	private int mediumAlarmType;
//	
//	// 轻微告警阈值
//	private double lightAlarmThreshold;
//	
//	// 轻微告警告警类型
//	private int lightAlarmType;
	private String types;//0,1,2

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	private String thresholds;//-10,0,10

	public String getThresholds() {
		return thresholds;
	}

	public void setThresholds(String thresholds) {
		this.thresholds = thresholds;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public RuleInfo clone() {   
        try {   
            return (RuleInfo) super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
    } 

	
}
