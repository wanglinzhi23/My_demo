package intellif.mining.common;

import java.util.concurrent.ForkJoinPool;

import com.fasterxml.jackson.databind.ObjectMapper;

import intellif.consts.GlobalConsts;

public class Constant {
	
 
    public static final String T_NAME_PLACE = "t_place";
    public static final String T_NAME_MINING_TASK = "t_mining_task";
	// 数据库
    public static final String INTELLIF_MINING = "intellif_mining";

    //Quartz schedule job/trigger related
    public static final String QUARTZ_JOB_IDENTIFY_PREFIX = "IF_QuartzTaskJob_";
    public static final String QUARTZ_JOB_TRIGGER_PREFIX = "IF_QuartzTaskJob_Trigger_";
    public static final String QUARTZ_JOB_GROUP_PREFIX = "IF_QuartzTaskJob_Group_";
    //OauthResource IDs
    
    public static final String R_ID_MINING_BASE = GlobalConsts.RESOURCE_ID_BASE + "mining";
    public static final String R_ID_FACE_MINING = R_ID_MINING_BASE + "/face";
    public static final String R_ID_PLACE = R_ID_MINING_BASE + "/place";
    public static final String R_ID_MINING_TASK = R_ID_MINING_BASE + "/task";

    public static final ForkJoinPool MINING_TASK_POOL = new ForkJoinPool();
    
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
}
