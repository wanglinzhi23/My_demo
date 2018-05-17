package intellif.utils;

import intellif.common.Constants;
import intellif.consts.GlobalConsts;
import intellif.enums.IFaceSdkTypes;
import intellif.exception.MsgException;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ERedListIoctrlType;
import intellif.ifaas.E_FACE_EXTRACT_TYPE;
import intellif.ifaas.T_MulAlgFeatureExtReq;
import intellif.ifaas.T_MulAlgFeatureExtRsp;
import intellif.ifaas.T_OneAlgFeatureExtRsp;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.UserServiceItf;
import intellif.settings.ServerSetting;
import intellif.thrift.IFaceSdkTarget;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

@Service
public class MemcachedSpace
{
	private static final Logger LOG = LogManager.getLogger(MemcachedSpace.class);

	@Autowired 
	private IFaceSdkServiceItf iFaceSdkServiceItf;

	@Autowired 
	private UserServiceItf userService;

	@ReadThroughSingleCache(namespace = "face_multi_feature_cache", expiration = 600)
	public String getFacefeatureFromId(@ParameterValueKeyProvider String key, long faceId, int datatype) throws Exception {
		System.out.println(key+" 算法提取未命中缓存..");
		String faceFeature = "";
		try {
			IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
			switch(datatype) {
			case 0: {
				String authority = userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
				if(authority.trim().length()==0){
	        		return faceFeature;
	        	}
				T_MulAlgFeatureExtReq tMulFeatureExtPara = new T_MulAlgFeatureExtReq(faceId, E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_BLACKDETAIL.getValue(), ServerSetting.getAlgVersionList());
            	T_MulAlgFeatureExtRsp mulAlgFeature = ifaceSdkTarget.queryMultipFeature(tMulFeatureExtPara);
				for(int i = 0; i<mulAlgFeature.getFeatureCnt();i++) {
					T_OneAlgFeatureExtRsp algFeature = mulAlgFeature.getFeatureList().get(i);
					if(algFeature.getErrorCode()<0) {
						LOG.error("算法版本:"+algFeature.getAlgVersion()+" 获取特征值错误,错误码:"+algFeature.getErrorCode());
						continue;
					}
					if(faceFeature.length()>0) {
						faceFeature += ";";
					}
					faceFeature += algFeature.getAlgVersion();
					faceFeature += "@";
					faceFeature += DatatypeConverter.printBase64Binary(algFeature.getFeatureVal());
				}
//				faceFeature = this._blackDetailDao.findOne(faceId, authority.split(",")).get(0).getBase64FaceFeature();
				break;
			}
			case 1: {
				T_MulAlgFeatureExtReq tMulFeatureExtPara = new T_MulAlgFeatureExtReq(faceId, E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_FACEINFO.getValue(), ServerSetting.getAlgVersionList());
            	T_MulAlgFeatureExtRsp mulAlgFeature = ifaceSdkTarget.queryMultipFeature(tMulFeatureExtPara);
				for(int i = 0; i<mulAlgFeature.getFeatureCnt();i++) {
					T_OneAlgFeatureExtRsp algFeature = mulAlgFeature.getFeatureList().get(i);
					if(algFeature.getErrorCode()<0) {
						LOG.error("算法版本:"+algFeature.getAlgVersion()+" 获取特征值错误,错误码:"+algFeature.getErrorCode());
						continue;
					}
					if(faceFeature.length()>0) {
						faceFeature += ";";
					}
					faceFeature += algFeature.getAlgVersion();
					faceFeature += "@";
					faceFeature += DatatypeConverter.printBase64Binary(algFeature.getFeatureVal());
				}
//				faceFeature = this.faceService.findOne(faceId).getBase64FaceFeature();
				break;
			}
			case 2: {
				T_MulAlgFeatureExtReq tMulFeatureExtPara = new T_MulAlgFeatureExtReq(faceId, E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_FACEINFO.getValue(), ServerSetting.getAlgVersionList());
            	T_MulAlgFeatureExtRsp mulAlgFeature = ifaceSdkTarget.queryMultipFeature(tMulFeatureExtPara);
				for(int i = 0; i<mulAlgFeature.getFeatureCnt();i++) {
					T_OneAlgFeatureExtRsp algFeature = mulAlgFeature.getFeatureList().get(i);
					if(algFeature.getErrorCode()<0) {
						LOG.error("算法版本:"+algFeature.getAlgVersion()+" 获取特征值错误,错误码:"+algFeature.getErrorCode());
						continue;
					}
					if(faceFeature.length()>0) {
						faceFeature += ";";
					}
					faceFeature += algFeature.getAlgVersion();
					faceFeature += "@";
					faceFeature += DatatypeConverter.printBase64Binary(algFeature.getFeatureVal());
				}
//				faceFeature = this.faceService.findOne(faceId).getBase64FaceFeature();
				break;
			}
			case 3: {
				T_MulAlgFeatureExtReq tMulFeatureExtPara = new T_MulAlgFeatureExtReq(faceId, E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_CIDDETAIL.getValue(), ServerSetting.getAlgVersionList());
            	T_MulAlgFeatureExtRsp mulAlgFeature = ifaceSdkTarget.queryMultipFeature(tMulFeatureExtPara);
				for(int i = 0; i<mulAlgFeature.getFeatureCnt();i++) {
					T_OneAlgFeatureExtRsp algFeature = mulAlgFeature.getFeatureList().get(i);
					if(algFeature.getErrorCode()<0) {
						LOG.error("算法版本:"+algFeature.getAlgVersion()+" 获取特征值错误,错误码:"+algFeature.getErrorCode());
						continue;
					}
					if(faceFeature.length()>0) {
						faceFeature += ";";
					}
					faceFeature += algFeature.getAlgVersion();
					faceFeature += "@";
					faceFeature += DatatypeConverter.printBase64Binary(algFeature.getFeatureVal());
				}
//				faceFeature = this.cidDetailRepository.findOne(faceId).getBase64FaceFeature();
				break;
			}
			case 4: {
				T_MulAlgFeatureExtReq tMulFeatureExtPara = new T_MulAlgFeatureExtReq(faceId, E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_JUNZHUDETAIL.getValue(), ServerSetting.getAlgVersionList());
            	T_MulAlgFeatureExtRsp mulAlgFeature = ifaceSdkTarget.queryMultipFeature(tMulFeatureExtPara);
				for(int i = 0; i<mulAlgFeature.getFeatureCnt();i++) {
					T_OneAlgFeatureExtRsp algFeature = mulAlgFeature.getFeatureList().get(i);
					if(algFeature.getErrorCode()<0) {
						LOG.error("算法版本:"+algFeature.getAlgVersion()+" 获取特征值错误,错误码:"+algFeature.getErrorCode());
						continue;
					}
					if(faceFeature.length()>0) {
						faceFeature += ";";
					}
					faceFeature += algFeature.getAlgVersion();
					faceFeature += "@";
					faceFeature += DatatypeConverter.printBase64Binary(algFeature.getFeatureVal());
				}
//				faceFeature = this.juZhuDetailRepository.findOne(faceId).getBase64FaceFeature();
				break;
			}
			case 5: {
				T_MulAlgFeatureExtReq tMulFeatureExtPara = new T_MulAlgFeatureExtReq(faceId, E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_OTHERDETAIL.getValue(), ServerSetting.getAlgVersionList());
            	T_MulAlgFeatureExtRsp mulAlgFeature = ifaceSdkTarget.queryMultipFeature(tMulFeatureExtPara);
				for(int i = 0; i<mulAlgFeature.getFeatureCnt();i++) {
					T_OneAlgFeatureExtRsp algFeature = mulAlgFeature.getFeatureList().get(i);
					if(algFeature.getErrorCode()<0) {
						LOG.error("算法版本:"+algFeature.getAlgVersion()+" 获取特征值错误,错误码:"+algFeature.getErrorCode());
						continue;
					}
					if(faceFeature.length()>0) {
						faceFeature += ";";
					}
					faceFeature += algFeature.getAlgVersion();
					faceFeature += "@";
					faceFeature += DatatypeConverter.printBase64Binary(algFeature.getFeatureVal());
				}
//				faceFeature = this.otherDetailRepository.findOne(faceId).getBase64FaceFeature();
				break;
			}
			case 6: {
				T_MulAlgFeatureExtReq tMulFeatureExtPara = new T_MulAlgFeatureExtReq(faceId, E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_OTHERDETAIL.getValue(), ServerSetting.getAlgVersionList());
            	T_MulAlgFeatureExtRsp mulAlgFeature = ifaceSdkTarget.queryMultipFeature(tMulFeatureExtPara);
				for(int i = 0; i<mulAlgFeature.getFeatureCnt();i++) {
					T_OneAlgFeatureExtRsp algFeature = mulAlgFeature.getFeatureList().get(i);
					if(algFeature.getErrorCode()<0) {
						LOG.error("算法版本:"+algFeature.getAlgVersion()+" 获取特征值错误,错误码:"+algFeature.getErrorCode());
						continue;
					}
					if(faceFeature.length()>0) {
						faceFeature += ";";
					}
					faceFeature += algFeature.getAlgVersion();
					faceFeature += "@";
					faceFeature += DatatypeConverter.printBase64Binary(algFeature.getFeatureVal());
				}
//				faceFeature = this.otherDetailRepository.findOne(faceId).getBase64FaceFeature();
				break;
			}
			default: {
				T_MulAlgFeatureExtReq tMulFeatureExtPara = new T_MulAlgFeatureExtReq(faceId, E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_OTHERDETAIL.getValue(), ServerSetting.getAlgVersionList());
            	T_MulAlgFeatureExtRsp mulAlgFeature = ifaceSdkTarget.queryMultipFeature(tMulFeatureExtPara);
				for(int i = 0; i<mulAlgFeature.getFeatureCnt();i++) {
					T_OneAlgFeatureExtRsp algFeature = mulAlgFeature.getFeatureList().get(i);
					if(algFeature.getErrorCode()<0) {
						LOG.error("算法版本:"+algFeature.getAlgVersion()+" 获取特征值错误,错误码:"+algFeature.getErrorCode());
						continue;
					}
					if(faceFeature.length()>0) {
						faceFeature += ";";
					}
					faceFeature += algFeature.getAlgVersion();
					faceFeature += "@";
					faceFeature += DatatypeConverter.printBase64Binary(algFeature.getFeatureVal());
				}
//				faceFeature = this.otherDetailRepository.findOne(faceId).getBase64FaceFeature();
				break;
			}
			}
		}catch (Exception e) {
			LOG.error("faceId :"+faceId+" datatype:"+datatype +" 数据不存在！", e);
			throw new MsgException(Constants.error_face_null);
		}
		if(StringUtil.isBlank(faceFeature)){
		    LOG.error("faceId :"+faceId+" datatype:"+datatype +" 数据不存在！");
            throw new MsgException(Constants.error_face_null);
		}
		return faceFeature;
	}
	
	@ReadThroughSingleCache(namespace = "face_red_detail_cache", expiration = 600)
	public int checkFaceIsOrNotInRedDetails(@ParameterValueKeyProvider String key,long id,int dataType) {
	    int result = 0;
	    try{
	        IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
	        int type;
	        switch(dataType){
            case 0: {
                type = E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_BLACKDETAIL.getValue();
                break;
            }
            case 1:
            case 2: {
                type = E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_FACEINFO.getValue();
                break;
            }
            case 3: {
                type = E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_CIDDETAIL.getValue();
                break;
            }
            case 4: {
                type = E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_JUNZHUDETAIL.getValue();
                break;
            }
            case 5:
            case 6: 
            default: {
                 type = E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_OTHERDETAIL.getValue();
                 break;
            }
            
	        } 
	        result = ifaceSdkTarget.iface_engine_ioctrl(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_REDLIST.getValue(), ERedListIoctrlType.REDLIST_IOCTRL_QUERY.getValue(), id, type);
	    }catch(Exception e){
	        e.printStackTrace();
	        LOG.error("red check error:",e.toString());
	    }
	    return result;
	}
}
