/**
 *
 */
package intellif.thrift;

import intellif.ifaas.T_IF_FACERECT;
import intellif.ifaas.T_IF_TASK_INFO;
import intellif.ifaas.T_MulAlgFeatureExtReq;
import intellif.ifaas.T_MulAlgFeatureExtRsp;
import intellif.ifaas.T_ProgressQueryRst;

import org.apache.thrift.TException;

import java.util.List;

/**
 * IFaceSdkTarget with input image URL
 *
 * @author yangboz
 * @see http://en.wikipedia.org/wiki/Adapter_pattern
 */
public interface IFaceSdkTarget {
    //
    List<T_IF_FACERECT> processFaceDetectExtract(String input, long bdId, int extType);

    //	double processFaceFeatureVerify(double feat1, double feat2);
    double processFaceFeatureVerify(String img1, String img2) throws TException;

    //	String processTaskSurveillanceCreate(T_IF_TASK_INFO info);
    int processTaskSurveillanceCreate(T_IF_TASK_INFO info) throws TException;

    //	boolean processTaskSurveillanceTerminate(T_IF_TASK_INFO info);
    int processTaskSurveillanceTerminate(long tId) throws TException;

    //
    int iface_engine_ioctrl(int type, long para0, long para1, long para2, long para3) throws TException;

    //
    List<T_IF_FACERECT> image_detect_extract(String inputImgName, long fromImgId) throws TException;
    
    String face_detect_rect(String inputImgName, long feceId) throws TException;

    int task_snaper_create(int sourceType, long sourceId) throws TException;

    int task_snaper_terminate(int sourceType, long sourceId) throws TException;
    T_ProgressQueryRst getFeatureUpdateState(int type,int userId);

	T_MulAlgFeatureExtRsp queryMultipFeature(T_MulAlgFeatureExtReq tMulFeatureExtPara);

}
