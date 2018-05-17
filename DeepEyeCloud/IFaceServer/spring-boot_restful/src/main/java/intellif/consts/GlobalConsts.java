
package intellif.consts;

import intellif.dto.ProcessInfo;
import intellif.dto.SearchReasonDto;
import intellif.enums.SolrCloudUntils;
import intellif.dto.ZipPathInfo;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.E_FLUSH_PROGRESS_TYPE;
import intellif.database.entity.ExcelProcessInfo;
import intellif.database.entity.IFaceConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

/**
 * The Class GlobalConsts.
 */
public final class GlobalConsts {
    //Table name list
	public static final String T_NAME_USER_BUSINESS = "t_user_business_api";
    public static final String T_NAME_CAMERA_INFO = "t_camera_info";
    public static final String T_NAME_OTHER_CAMERA_INFO = "t_other_camera";
    public static final String T_NAME_VIDEO_INFO = "t_video_info";
    public static final String T_NAME_IMAGE_INFO = "t_image";
    public static final String T_NAME_POLICE_CASE = "t_police_case_type";
    	public static final String[] TABLE_NAMES_SOURCE = {
			GlobalConsts.INTELLIF_BASE + "." + T_NAME_CAMERA_INFO,
			GlobalConsts.INTELLIF_BASE + "." + T_NAME_VIDEO_INFO,
			GlobalConsts.INTELLIF_BASE + "." + T_NAME_IMAGE_INFO };
    	
    public static final String T_NAME_PERSON_INFO = "t_person_info_c";
    public static final String T_NAME_FK_PERSON_ATTR = "t_fk_person_attr"; 
    public static final String T_NAME_FK_BK_BANK = "t_fk_bank_dictionary"; 
    public static final String T_NAME_FK_INSTITUTION_CODE = "t_fk_institution_code"; 
    public static final String T_NAME_FK_ALARM_PUSH_LOG = "t_fk_alarm_push_log"; 
    public static final String T_NAME_PERSON_DETAIL = "t_person_detail";
    public static final String T_NAME_TASK_INFO = "t_task_info";//任务信息
    public static final String T_NAME_RULE_INFO = "t_rule_info";
    public static final String T_NAME_SEARCH_REASON = "t_search_reason";
    public static final String T_NAME_FACE_INFO = "t_face";
    public static final String T_NAME_FACE_DELETE_INFO = "t_face_delete";
    public static final String T_NAME_ALARM_INFO = "t_alarm_info";
    public static final String T_NAME_URGENT_ALARM_INFO = "t_urgent_alarm_info";
    public static final String T_NAME_BLACK_BANK = "t_black_bank";
    public static final String T_NAME_BLACK_DETAIL = "t_black_detail";
    public static final String T_NAME_POLICE_STATION = "t_police_station";
    public static final String T_NAME_SERVER_INFO = "t_server_info";
    public static final String T_NAME_USER = "t_user";
    public static final String T_NAME_ROLE = "t_role";
    public static final String T_NAME_USER_ROLE = "t_user_role";
    public static final String T_NAME_RESOURCE = "t_resource";
    public static final String T_NAME_CLIENT_DETAILS = "t_oauth_client_details";
    public static final String T_NAME_OAUTH_ACCESS_TOKEN = "oauth_access_token";//token表
    public static final String T_NAME_TASK_DEPLOY_INFO = "t_task_deploy_info";//任务布控信息
    public static final String T_NAME_CAMERA_BLACKDETAIL = "t_camera_blackdetail";//摄像头与黑名单关联关系
    public static final String T_NAME_ALARM_PROCESS = "t_alarm_process";
    public static final String T_NAME_AREA_BLACKDETAIL = "t_area_blackdetail";//区域与黑名单关联关系

    public static final String T_NAME_FK_PLACE_CAMERA = "t_fk_place_camera";//反恐场所与摄像头关联关系
    public static final String T_NAME_AUDIT_LOG = "t_audit_log";
    public static final String T_NAME_SEARCH_LOG = "t_search_log";//搜索日志记录
    public static final String T_NAME_SEARCH_RESULT_CODE = "t_search_result_code";//搜索结果码
	public static final String T_NAME_POLICE_CLOUD_AUDIT_LOG = "t_police_cloud_audit_log";
    public static final String T_NAME_AUDIT_LOG_TYPE = "t_audit_log_type";
    public static final String T_NAME_CRIME_SEC_TYPE = "t_crime_sec_type";
    public static final String T_NAME_CRIME_FRI_TYPE = "t_crime_fri_type";
    public static final String T_NAME_SOLR_CONFIG_INFO = "t_solr_config_info";
    public static final String T_NAME_USER_ATTENTION = "t_user_attention";
    public static final String T_NAME_POLICE_STATION_AUTHORITY = "t_police_station_authority";
    public static final String T_NAME_RED_DETAIL = "t_red_detail";
    public static final String T_NAME_RED_PERSON = "t_person_red";
    public static final String T_NAME_RED_FORCE = "t_red_force_log";
    public static final String T_NAME_RED_CHECK = "t_red_check_result";
    public static final String T_NAME_IFACE_CONFIG = "t_iface_com_conf";
    public static final String T_SEARCH_RECORD = "t_search_record";
    public static final String T_NAME_USER_API_LIMIT = "t_user_api_limit";
    public static final String T_NAME_API_RESOURCE = "t_api_resource";
    public static final String T_NAME_RESIDENT_PERSON = "t_resident_person";
    public static final String T_NAME_RESIDENT_TOTAL = "t_resident_total";
    public static final String T_NAME_RESIDENT_FACE = "t_resident_face";
    public static final String T_NAME_RESIDENT_AREA = "t_analysis_area";
    public static final String T_NAME_PERSON_INZONES = "t_person_inzones";
    public static final String T_NAME_MALL = "t_mall_detail";
    public static final String T_NAME_CID_INFO = "t_cid_info";
    public static final String T_NAME_CID_DETAIL = "t_cid_detail";
    public static final String T_NAME_JUZHU_INFO = "t_juzhu_info";
    public static final String T_NAME_JUZHU_DETAIL = "t_juzhu_detail";
    public static final String T_NAME_OTHER_INFO =  "t_other_info";
    public static final String T_NAME_OTHER_DETAIL = "t_other_detail";
    public static final String T_NAME_ALLOW_IPS = "t_allow_ips";
	public static final String T_NAME_AREA = "t_area";
	public static final String T_NAME_FK_PLACE = "t_fk_place";
	public static final String T_NAME_DISTRICT = "t_district";
	public static final String T_NAME_OTHER_AREA = "t_other_area";
    public static final String T_NAME_MAINTENANCE = "t_maintenance_info";
    public static final String T_NAME_LOSS_PRE = "t_lose_pre_c";
    public static final String T_NAME_WEIXIN_USER = "t_weixin_user";
    public static final String T_NAME_POLICEMAN_INFO = "t_policeman_info";
    public static final String T_NAME_POLICEMAN_INFO_AUTHORITY = "t_police_man_authority";
    public static final String T_NAME_POLICEMAN_INFO_AUTHORITY_TYPE = "t_police_man_authority_type";
    public static final String T_NAME_PUSH_ALARM_INFO = "t_push_alarm_info"; 
    public static final String T_NAME_CRIME_ALARM_INFO = "t_crime_alarm_info";
    public static final String T_NAME_FACE_CAMERA_COUNT = "t_face_camera_count";
    public static final String T_NAME_FACE_QUALITY_CAMERA_COUNT = "t_face_quality_camera_count";
    public static final String T_NAME_SOLR_INDEX_SETINAL = "face_index_records";
    public static final String T_NAME_EXCEL_RECORD = "t_excel_record";

    public static final String T_NAME_TIME_CONFIGURE = "t_chd_configure_time";
    public static final String T_NAME_CONTRAST_FACE_INFO = "t_contrast_face_info";
    public static final String T_NAME_SYSTEM_SWITCH = "t_system_switch";
    public static final String T_NAME_USER_DISTRICT = "t_user_district";
    public static final String T_NAME_USER_AREA = "t_user_area";
    public static final String T_NAME_USER_CAMERA = "t_user_camera";
    public static final String T_NAME_USER_SWITCH = "t_user_switch";
    public static final String T_NAME_TABLE_VERSION = "t_table_version";

    public static final String T_NAME_ROLE_RESOURCE = "t_role_resource";


    public static final String T_NAME_BLACK_FEATURE = "t_black_feature";
    public static final String T_NAME_RED_FEATURE = "t_red_feature";

	public static final String T_NAME_WIFI_ACCESS_INFO = "t_wifi_access_info";
    public static final String T_NAME_LAST_CAPTURE_TIME = "t_last_capture_time";
    public static final String T_NAME_ALG_PARAM = "t_alg_param";
    public static final String T_NAME_MOBILE_COLLECT_SYNC_LOG = "t_mobile_collect_sync_log";
    public static final String T_NAME_MOBILE_COLLECT_STATION_MAP = "t_mobile_collect_station_map";
    public static final String T_NAME_FACE_FILTER_TYPE = "t_face_filter_type";
    public static final String T_NAME_FACE_FILTERED = "t_face_filtered";

    public static final String T_NAME_FACE_EXTRACT_TASK = "t_face_extract_task";
    public static final String T_NAME_FACE_COLLISION_TASK = "t_face_collision_task";
	public static final String T_NAME_FACE_PERSON_STATISTIC="t_person_statistic";

    //上传状态表
    public static final String T_UPLOADED_STATUS="t_uploaded_status";
    //已上传文件表
    public static final String T_UPLOADED_FILE="t_uploaded_file";

    public static final String T_NAME_QIANGDAN_RECORD = "t_qiangdan_record";
    public static final String T_NAME_QIANGDAN_CAMERA_POLICE = "t_qiangdan_camera_police";
    
    public static final String T_NAME_FACE_STREAM = "t_face_stream";
    public static final String T_NAME_VENUE = "t_venue";

    //lire stop
    public static boolean run=false;
    
    //facecompare
    public static final int REAL_LENGTH_181 =140;  
    
	//分表
    public static final String T_NAME_TABLES = "t_tables";
    public static final String T_FACE_PRE = "t_face";
    public static final String T_IMAGE_PRE = "t_image";
    public static final String INTELLIF_BASE = "intellif_base";
    public static final String INTELLIF_FACE = "intellif_face";
    public static final String INTELLIF_STATIC = "intellif_static";
    public static final String INTELLIF_AREA_AUTHORIZE = INTELLIF_BASE;
    //
    public static final int LEN_FACE_FEATURE = 724;
    //Quartz schedule job/trigger related
    public static final String QUARTZ_JOB_IDENTIFY_PREFIX = "IF_QuartzTaskJob_";
    public static final String QUARTZ_JOB_TRIGGER_PREFIX = "IF_QuartzTaskJob_Trigger_";
    public static final String QUARTZ_JOB_GROUP_PREFIX = "IF_QuartzTaskJob_Group_";
    //OauthResource IDs
    public static final String RESOURCE_ID_BASE = "/intellif/";
    public static final String R_ID_ALARM = RESOURCE_ID_BASE + "alarm";
    public static final String R_ID_URGENT_ALARM = RESOURCE_ID_BASE + "urgent/alarm";
    public static final String R_ID_JPA_ALARM = RESOURCE_ID_BASE + "jpa/alarm";
    public static final String R_ID_BLACK_BANK = RESOURCE_ID_BASE + "black/bank";
    public static final String R_ID_BLACK_DETAIL = RESOURCE_ID_BASE + "black/detail";
    public static final String R_ID_CAMERA = RESOURCE_ID_BASE + "camera";
    public static final String R_ID_FACE = RESOURCE_ID_BASE + "face";
    public static final String R_ID_GREETING = RESOURCE_ID_BASE + "greeting";
    public static final String R_ID_UPLOAD = RESOURCE_ID_BASE + "upload";
    public static final String R_ID_IMAGE = RESOURCE_ID_BASE + "image";
    public static final String R_ID_PERSON_DETAIL = RESOURCE_ID_BASE + "person/detail";
    public static final String R_ID_FK_PERSON_DETAIL = RESOURCE_ID_BASE + "fk/person/detail";
    public static final String R_ID_FK_ALARM_PUSH = RESOURCE_ID_BASE + "fk/alarm/push";
    public static final String R_ID_FK_PLACE = RESOURCE_ID_BASE + "fk/place";
    public static final String R_ID_FK_INSTITUTION_CODE = RESOURCE_ID_BASE + "fk/institutioncode";
    public static final String R_ID_PERSON_INZONES = RESOURCE_ID_BASE + "person/inzones";
    public static final String R_ID_PERSON_INFO = RESOURCE_ID_BASE + "person/info";
    public static final String R_ID_POLICE_STATION = RESOURCE_ID_BASE + "police/station";
    public static final String R_ID_POLICE_STATION_AUTHORITY = RESOURCE_ID_BASE + "police/station/authority";
    public static final String R_ID_POLICE_MAN = RESOURCE_ID_BASE + "police/man";
	public static final String R_ID_POLICE_MAN_AUTHORITY = RESOURCE_ID_BASE
			+ "police/man/authority";
    public static final String R_ID_ROLE = RESOURCE_ID_BASE + "role";
    public static final String R_ID_RULE = RESOURCE_ID_BASE + "rule";
    public static final String R_ID_SEARCH = RESOURCE_ID_BASE + "search";
    public static final String R_ID_G20 = RESOURCE_ID_BASE + "g20meeting";
    public static final String R_ID_SERVER = RESOURCE_ID_BASE + "server";
    public static final String R_ID_SUSPECT = RESOURCE_ID_BASE + "suspect";
    public static final String R_ID_TASK = RESOURCE_ID_BASE + "task";
    public static final String R_ID_USER = RESOURCE_ID_BASE + "user";
    public static final String R_ID_USER_ATTENTION = RESOURCE_ID_BASE + "user/attention";
    public static final String R_ID_AUDIT_LOG = RESOURCE_ID_BASE + "audit/log";
    public static final String R_ID_SEARCH_REASON = RESOURCE_ID_BASE + "search/reason";
    public static final String R_ID_CRIME_FRI_TYPE = RESOURCE_ID_BASE + "crime/fritype";
    public static final String R_ID_CRIME_SEC_TYPE = RESOURCE_ID_BASE + "crime/sectype";
    public static final String R_ID_RED = RESOURCE_ID_BASE + "red/detail";
    public static final String R_ID_RESIDENT = RESOURCE_ID_BASE + "resident";
    public static final String R_ID_MALL = RESOURCE_ID_BASE + "mall";
    public static final String R_ID_POLICE_CLOUD = RESOURCE_ID_BASE + "police/cloud";
	public static final String R_ID_AREA = RESOURCE_ID_BASE + "area";
    public static final String R_ID_MAINTENANCE = RESOURCE_ID_BASE + "maintenance";
    public static final String R_ID_LOSS_PRE = RESOURCE_ID_BASE + "loss/prevent";
    public static final String R_ID_ALLOW_IPS = RESOURCE_ID_BASE + "allowips";    
    public static final String R_ID_WEIXIN_USER = RESOURCE_ID_BASE + "weixinuser";
    public static final String R_ID_CRIME_ALARM = RESOURCE_ID_BASE + "crime/alarm";
    public static final String R_ID_TABLE = RESOURCE_ID_BASE + "table";
    public static final String R_ID_ALARM_PROCESS = RESOURCE_ID_BASE + "alarm/process";
    public static final String R_ID_STATISTIC = RESOURCE_ID_BASE + "statistic";
    public static final String R_ID_BANK_COLLISION = RESOURCE_ID_BASE + "bankcollision";
    public static final String R_ID_BANKIMPORT = RESOURCE_ID_BASE + "bank/import";
    
    public static final String R_ID_XINYI = RESOURCE_ID_BASE + "xinyi/api";
    public static final String R_ID_PERSON_RED_LIGHT = RESOURCE_ID_BASE + "red/light/person";


    public static final String R_ID_SHARED_FACE = RESOURCE_ID_BASE + "share/face";
    public static final String R_ID_SHARED_CAMERA = RESOURCE_ID_BASE + "share/camera";
    public static final String R_ID_SHARED_AREA = RESOURCE_ID_BASE + "share/area";
    public static final String R_ID_DISTRICT = RESOURCE_ID_BASE + "share/district";
    public static final String R_ID_STATIC_OTHER = RESOURCE_ID_BASE + "static/other";

    public static final String R_ID_SYSTEM_SWITCH = RESOURCE_ID_BASE + "system/switch";
    public static final String R_ID_ZONE = RESOURCE_ID_BASE + "zone";

    public static final String R_ID_RESOURCE = RESOURCE_ID_BASE + "resource";

    public static final String R_ID_FACE_FILTER = RESOURCE_ID_BASE + "face/filter";

    
    public static final String R_ID_RESUMABLE_UPLOAD = RESOURCE_ID_BASE+"resumable/upload";
    
    public static final String R_ID_FILE_MANAGEMENT=RESOURCE_ID_BASE+"file/management";


    public static final String T_NAME_ZIP_PATH = "t_zip_path";
	
	public static final String R_ID_VEHICLE = RESOURCE_ID_BASE + "vehicle";
    public static final String R_ID_WIFI = RESOURCE_ID_BASE + "wifi";


    public static final String R_ID_FACE_EXTRACT = RESOURCE_ID_BASE + "faceExtract";
    public static final String R_ID_FACE_COLLISION_TASK = RESOURCE_ID_BASE + "faceCollision";

    public static final String R_ID_FACE_STREAM = RESOURCE_ID_BASE + "face/stream";
    
    public static final String R_ID_XINGHUO = RESOURCE_ID_BASE + "xinghuo";
	public static final String R_ID_PERSION_STATISTICS=RESOURCE_ID_BASE + "person";
	public static final String R_ID_DataExport=RESOURCE_ID_BASE + "dataExport";

    //OAuth2.0 additional information keys
    public static final String OAUTH_A_I_K_USER_INFO = "oauth_AIK_user_info";
    public static final String OAUTH_A_I_K_ROLE_INFO_S = "oauth_AIK_role_info_s";
    public static final String OAUTH_A_I_K_OAUTH_RES_S = "oauth_AIK_oauth_res_s";//API resources
    public static final String OAUTH_A_I_K_USER_DETAIL = "oauth_AIK_user_detail";
    //重点人员库查询接口
    public static final Integer TASK_IDS_TYPE = 0;
    public static final Integer CAMERA_IDS_TYPE = 1;
    //MQTT消息分类
    public static final Integer MAKE_A_ARREST = 1;
    public static final Integer CANCEL_A_ARREST = 2;
    public static final Integer MAKE_A_IMPORTANT = 3;
    public static final Integer CANCEL_A_IMPORTANT = 4;
    public static final Integer CREATE_NAW_PERSON = 5;
    //Solr人脸检索默认参数
    public static final float DEFAULT_SCORE_THRESHOLD = 0.92F;
    public static final int BLACK_BANK_TYPE = 0;
    public static final int WHITE_BANK_TYPE = 1;//白名单类型
    public static final int CID_BANK_TYPE = 1;
    public static final int IN_CAMERA_TYPE = 1;
    public static final int IN_STATION_TYPE = 2;
    //OAuth2.0 client has roles, differ with user related roles
    public static final String OAUTH_C_H_R_SUPER_ADMIN = "#oauth2.clientHasRole('SUPER_ADMIN')";
    public static final String OAUTH_C_H_R_ADMIN = "#oauth2.clientHasRole('ADMIN')";
    public static final String OAUTH_C_H_R_USER = "#oauth2.clientHasRole('USER')";
    public static final String OAUTH_C_H_R_GUEST = "#oauth2.clientHasRole('GUEST')";
    public static final String OAUTH_C_H_R_ANONYMOUS = "#oauth2.clientHasRole('ANONYMOUS')";
    //目标库管理权限
	public static final int READ_AUTORITY_TYPE = 0;
	public static final int UPDATE_AUTORITY_TYPE = 1;
	public static final int CONTROL_AUTORITY_TYPE = 2;
	public static  IFaceConfig redConfig = null;
	public static List<String> picFormatList = new ArrayList<String>();
	
	//Solr库类型
	public static final int BLACK_INFO_TYPE = 0; // 入库嫌弃人
	public static final int FACE_INFO_TYPE = 1; // 抓拍
	public static final int INSTATION_INFO_TYPE = 2; // 侯问室
	public static final int CID_INFO_TYPE = 3; // 户籍常口
	public static final int JUZHU_INFO_TYPE = 4; // 居住证
	public static final int CRIME_INFO_TYPE = 5; // 在逃人员
	public static final int POLICE_INFO_TYPE = 6; // 警综人员
	public static final int THREE_INFO_TYPE0 = 7; // 三类人员
	public static final int THREE_INFO_TYPE1 = 8; // 三类人员
	public static final int THREE_INFO_TYPE2 = 9; // 三类人员
	public static final int MOBILE_INFO_TYPE = 11; // 移动采取人员库
	public static final int SEARCH_INFO_TYPE = 10; // 搜索记录库人员
	public static final int face_collusion_mannual = 2;
	public static final int face_collusion_auto = 1;
	public static Map<Integer, SolrCloudUntils> cloudCoreMap = new HashMap<Integer, SolrCloudUntils>();
	public static Map<Integer, String> coreMap = new HashMap<Integer, String>();
	public static Map<Integer, String> kuMap = new HashMap<Integer, String>();
	public static Map<Integer, Integer> bankThriftMap = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> bankScheduleMap = new HashMap<Integer, Integer>();
	
	//存储上传文件的进度
	public static ConcurrentHashMap<Integer, ExcelProcessInfo> fileUploadMap = new ConcurrentHashMap<Integer,ExcelProcessInfo>();
	public static ConcurrentHashMap<Integer, ProcessInfo> downloadMap = new ConcurrentHashMap<Integer,ProcessInfo>();

	public static ConcurrentHashMap<Long,Map<String, List<Long>>> userBukongMap = new ConcurrentHashMap<Long,Map<String, List<Long>>>(); 

	// 双库碰撞的进度
	public static ConcurrentHashMap<Long, ProcessInfo> bankMatchMap = new ConcurrentHashMap<Long, ProcessInfo>();
	public static ConcurrentHashMap<Integer, ProcessInfo> downloadPkFaceMap = new ConcurrentHashMap<Integer, ProcessInfo>(); // 双库碰撞pk结果进度
	public static ConcurrentHashMap<Integer, ZipPathInfo> downloadPkResultMap = new ConcurrentHashMap<Integer, ZipPathInfo>(); // 双库碰撞pk结果地址链接
	public static ConcurrentHashMap<Integer, String> musicNameMap = new ConcurrentHashMap<Integer,String>();
	public static ConcurrentHashMap<Integer, String> musicUrlMap = new ConcurrentHashMap<Integer,String>();
	//存储打包链接
	public static ConcurrentHashMap<Integer, ZipPathInfo> zipMap = new ConcurrentHashMap<Integer, ZipPathInfo>();
	//存储导出文件的状态
	public static ConcurrentHashMap<Integer, Boolean> stateMap = new ConcurrentHashMap<Integer, Boolean>();
	
	//用户搜索原因缓存
	public static ConcurrentHashMap<Long, SearchReasonDto> searchReasonMap = new ConcurrentHashMap<Long, SearchReasonDto>();
		
	//红名单相关
	public final static String RED_CHECK_RESULT_WAIT = "未审核";
	public final static String RED_CHECK_RESULT_PASS = "通过";
	public final static String RED_CHECK_RESULT_NOPASS = "未通过";
	//大小图区分
	public final static String IMAGE = "image";
	public final static String FACE = "face";
	//当前系统采集face和image表最新sequence
	public static volatile long faceSequence = 0;
	public static volatile long imageSequence = 0;
	//缓存系统抓拍图片数目和原图文件大小
	public static volatile long faceBaseCount = 0;//总抓拍图片数 ，每天0点从数据库更新
	public static volatile float faceDayCount = 0;//当前一天抓拍数，实时更新
	public static volatile long faceMinCount = 0;//每分钟抓拍数，1分钟更新一次
	public static volatile float faceSecCount = 0;//每秒抓拍数，1s更新一次
	public static volatile long imageBaseCount = 0;//总抓拍原图数 ，每天0点从数据库更新
	public static volatile float imageDayCount = 0;//当前一天抓拍原图数，实时更新
	public static volatile long imageMinCount = 0;//每分钟抓拍原图数，1分钟更新一次
	
	//进度类型
	public static final int process_Black = 1;
	public static final int process_red = 2;
	//时间格式化
	public static final String YMD="yyyy-MM-dd";
	public static final String YMDHMS="yyyy-MM-dd HH:mm:ss";
	
	//搜索类型
	public static final int search_type_upload = 0;//上传搜索
	public static final int search_type_fetch = 1;//拖动搜索
	
	//全区域搜索账号标记
	public static final int normal_user_sign = 0; //非全区域搜索
	public static final int special_user_sign = 1; //特殊用户，全区域搜索
	public static final int special_contact_sign = 2; //联络员
	
	public static final String chn_camera_1 = "一类高清点";
	public static final String chn_camera_123 = "一二三类高清点";
	public static final String chn_camera_special = "一二三类及特殊高清点";
	
	public static final String ADMIN = "ADMIN";
	public static final String SUPER_ADMIN = "SUPER_ADMIN";
	//导出进度条
	public static ConcurrentHashMap<Integer, ProcessInfo> downloadAuditLogMap = new ConcurrentHashMap<Integer, ProcessInfo>(); // 日志导出进度
	public static ConcurrentHashMap<Integer, Boolean> downloadAuditLogMapStop = new ConcurrentHashMap<Integer, Boolean>(); // 日志导出停止标志位
	public static final String faceSearch = "api/intellif/face/search/face/";
	public static final String faceSearch_c = "api/intellif/face/search/";
	public static final String cloudSearch = "api/intellif/face/search/face/cloud/";
	public static final String personDetail = "api/intellif/person/detail";
	
	public static final String nodeType_district = "district";
	public static final String nodeType_area = "area";
	public static final String IFACE_CONFIG_RED = "red_switch";

//	public static HashMap<Long, HashMap<Date, Long>> lastFaceStatisticMap = new HashMap<Long, HashMap<Date, Long>>();
//	public static HashMap<Long, Long> allFaceStatisticMap = new HashMap<Long, Long>();
	
	public static long getFaceDayCount(){
		return (long)faceDayCount;
	}
	public static long getImageDayCount(){
		return (long)imageDayCount;
	}
	static {
		picFormatList.add(".jpg");
		picFormatList.add(".jpeg");
		picFormatList.add(".gif");
		picFormatList.add(".BMP");
		picFormatList.add(".pngs");
		
		coreMap.put(0, "intellif");
		coreMap.put(3, "cidinfo");
		coreMap.put(4, "juzhuinfo");
		coreMap.put(5, "otherinfo");
		coreMap.put(6, "otherinfo");
		coreMap.put(7, "otherinfo");
		coreMap.put(10, "searchinfo");
		
//		cloudCoreMap.put(0, SolrCloudUntils.BLACK_CLOUD);
//		cloudCoreMap.put(1, SolrCloudUntils.FACE_CLOUD);
//		cloudCoreMap.put(2, SolrCloudUntils.FACE_CLOUD);
//		cloudCoreMap.put(3, SolrCloudUntils.CID_CLOUD);
//		cloudCoreMap.put(4, SolrCloudUntils.JUZHU_CLOUD);
//		cloudCoreMap.put(5, SolrCloudUntils.OTHER_CLOUD);
//		cloudCoreMap.put(6, SolrCloudUntils.OTHER_CLOUD);
		
		kuMap.put(0, "重点人员");
		kuMap.put(2, "候问人员");
		kuMap.put(3, "户籍人员");
		kuMap.put(4, "居住人员");
		kuMap.put(5, "在逃人员");
		kuMap.put(6, "警综人员");
		
		bankThriftMap.put(1, EParamIoctrlType.PARAM_IOCTRL_CID_IMPORT.getValue());//cid
		bankThriftMap.put(2, EParamIoctrlType.PARAM_IOCTRL_JUZHU_IMPORT.getValue());//juzhu
		bankThriftMap.put(3, EParamIoctrlType.PARAM_IOCTRL_OTHER_IMPORT.getValue());//other
		bankThriftMap.put(4, EParamIoctrlType.PARAM_IOCTRL_OTHER_IMPORT.getValue());//other
		
		bankScheduleMap.put(1, E_FLUSH_PROGRESS_TYPE.FLUSH_PROGRESS_TYPE_CIDDETAIL.getValue());//cid
		bankScheduleMap.put(2, E_FLUSH_PROGRESS_TYPE.FLUSH_PROGRESS_TYPE_JUZHUDETAIL.getValue());//juzhu
		bankScheduleMap.put(3, E_FLUSH_PROGRESS_TYPE.FLUSH_PROGRESS_TYPE_OHTERDETAIL.getValue());//other
		bankScheduleMap.put(4, E_FLUSH_PROGRESS_TYPE.FLUSH_PROGRESS_TYPE_OHTERDETAIL.getValue());//other
		
	}

    public static ForkJoinPool MUL_TABLE_FJPOOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

	public static Map<String, Long> INDEX_TABLE_MAP = new HashMap<String, Long>();

	
    //文件类型(0:视频，1：压缩包）
    public static final int FILE_TYPE_VIDEO=0;
    public static final int FILE_TYPE_ZIP=1;
    public static final int FILE_TYPE_IMG=2;
    //文件位置（0左，1右）
    public static final int POSITION_LEFT=0;
    public static final int POSITION_RIGHT=1;
    //文件上传状态
    public static final int IS_FINISHED_NO=0;
    public static final int IS_FINISHED_YES=1;
    
    //上传阶段 0:写入阶段 1:重命名阶段 2:图片打包阶段 3:打包成功原图删除阶段4上传完成5任务创建
    public static final int FILE_WRITTING=0;
    public static final int FILE_RENAMEING=1;
    public static final int FILE_ZIPING=2;
//    public static final int FILE_DELETING=3;
    public static final int FILE_FINISHED=4;
    public static final int FILE_TASK_CREATED=5;
    public static final int FILE_ZIPING_BY_OTHER_THREAD=21;
    public static final int FILE_ZIP_FAILED=22;
    
    public static final int SAVE_DATA_PER_CHUNKS=20;
    
    public static final int IS_FILE_DELETED_NO=0;
    public static final int IS_FILE_DELETED_YES=1;
    public static final int MAX_BATCH_UPDATE_NUM=4000;

    public static final int ALARM_NOPROCESS = 0;
    public static final int AlARM_PROCESS = 1;
    public static final int AlARM_MISTAKE = 2;
}
