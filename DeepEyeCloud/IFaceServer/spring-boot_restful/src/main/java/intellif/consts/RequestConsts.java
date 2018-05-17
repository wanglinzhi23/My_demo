package intellif.consts;
/**
 * 存储swagger接口请求参数常量
 * @author shixiaohua
 *
 */
public final class RequestConsts {
public final static int blackdetail_area_allselected = 1; //布控区域全选中状态
public final static int blackdetail_area_notallselected = 0;//布控区域非全选中状态

public final static int response_system_error = 1001;//系统错误 运行异常返回前端
public final static int response_right_error = 1002;//权限错误，库权限或区域权限
public final static int response_dataresult_error = 1003;//结果集错误，为空..,已存在
public final static int response_red_error = 1004;//红名单比中
public final static int response_dataformat_error = 1005;//参数格式错误等
}
