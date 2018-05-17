package intellif.share.service.impl;

import intellif.audit.AuditServiceItf;
import intellif.common.Constants;
import intellif.consts.GlobalConsts;
import intellif.controllers.BankCollisionController;
import intellif.dao.AreaDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraInfoDao;
import intellif.dao.CidDetailDao;
import intellif.dao.CidInfoDao;
import intellif.dao.IFaceConfigDao;
import intellif.dao.JuZhuDetailDao;
import intellif.dao.JuZhuInfoDao;
import intellif.dao.OtherAreaDao;
import intellif.dao.OtherCameraDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.OtherInfoDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.RedDetailDao;
import intellif.database.entity.Area;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.OtherArea;
import intellif.database.entity.PersonDetail;
import intellif.dto.FaceResultByCameraDto;
import intellif.dto.FaceResultDto;
import intellif.dto.FaceSearchStatisticDto;
import intellif.dto.HistorySearchOperationDetailDto;
import intellif.dto.ProcessInfo;
import intellif.dto.SearchFaceDto;
import intellif.enums.IFaceSdkTypes;
import intellif.exception.MsgException;
import intellif.exception.RedException;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ERedListIoctrlType;
import intellif.ifaas.E_FACE_EXTRACT_TYPE;
import intellif.ifaas.T_MulAlgFeatureExtReq;
import intellif.ifaas.T_MulAlgFeatureExtRsp;
import intellif.ifaas.T_OneAlgFeatureExtRsp;
import intellif.lire.CameraInfoThread;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.UserServiceItf;
import intellif.settings.LongGangRedPersonSettings;
import intellif.settings.ServerSetting;
import intellif.share.service.ShareSolrDataServiceItf;
import intellif.share.service.ShareSolrServerItf;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.DateUtil;
import intellif.utils.FaceResultDtoComparable;
import intellif.utils.FileUtil;
import intellif.utils.MemcachedSpace;
import intellif.database.entity.BankMatchResultTuple;
import intellif.database.entity.BlackDetailRealName;
import intellif.database.entity.CidInfo;
import intellif.database.entity.IFaceConfig;
import intellif.database.entity.JuZhuInfo;
import intellif.database.entity.OtherInfo;
import intellif.database.entity.RedDetail;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShareSolrServerImpl implements ShareSolrServerItf {
	private static Logger LOG = LogManager.getLogger(ShareSolrServerImpl.class);
    @Autowired
    private FaceServiceItf faceService;
    @Autowired
    private CidDetailDao cidDetailRepository;
    @Autowired
    private CidInfoDao cidInfoRepository;
    @Autowired
    private JuZhuDetailDao juZhuDetailRepository;
    @Autowired
    private JuZhuInfoDao juzhuInfoRepository;
    @Autowired
    private OtherDetailDao otherDetailRepository;
    @Autowired
    private OtherInfoDao otherInfoRepository;
    @Autowired
    private CameraInfoDao _cameraInfoDao;
    @Autowired
    private OtherCameraDao _otherCameraDao;
    @Autowired
    private AreaDao _areaDao;
    @Autowired
    private OtherAreaDao _otherAreaDao;
    @Autowired
    private ShareSolrDataServiceItf serverDataServiceItf;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;
    @Autowired
    private IFaceConfigDao ifaceConfigDao;
    @Autowired
    private PersonDetailServiceItf personDetailServiceItf;
    @Autowired
	private AuditServiceItf _auditService;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Autowired
    private RedDetailDao redDao;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private MemcachedSpace memcachedSpace;
    @Autowired
    private PersonDetailDao personDetailDao;
    @Autowired
    private BlackDetailDao blackDetailDao;
    @Autowired
    private CameraServiceItf cameraService;
    
    @Override
    public List<FaceResultDto> searchFaceByIdInBank(SearchFaceDto searchFaceDto) throws Exception {
        String starttime = null;
        String endtime = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.parse(searchFaceDto.getStarttime());
            starttime = searchFaceDto.getStarttime();
        } catch (Exception e) {
            starttime = DateUtil.getMonthReduce(new Date(), -3);
        }
        try {
            format.parse(searchFaceDto.getEndtime());
            endtime = searchFaceDto.getEndtime();
        } catch (Exception e) {
            endtime = DateUtil.getDateString(new Date()) + " 23:59:59";
        }
		searchFaceDto.setStarttime(starttime);
		searchFaceDto.setEndtime(endtime);
		String datatype = searchFaceDto.getDataType();
        long faceId = searchFaceDto.getFaceId();	
        float scoreThreshold = searchFaceDto.getScoreThreshold();
        int type = searchFaceDto.getType();
        String ids = searchFaceDto.getIds();
        List<FaceResultDto> faceResultList = new ArrayList<FaceResultDto>();
        if (ids.length() == 0) {
            if(searchFaceDto.getForceSearch() == 0){
                int result = checkFaceIsOrNotInRedDetails(faceId,Integer.valueOf(datatype));
                if(result > 0){
                    RedDetail rd = redDao.findOne(new Long(result).longValue());
                    if(null != rd){
                        RedException ex = new RedException(rd.getFaceUrl(),faceId, result, GlobalConsts.search_type_fetch,Integer.valueOf(datatype));
                        throw ex;
                    }
                }
            }
            String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatype)+":"+faceId, faceId, Integer.valueOf(datatype));
			faceResultList = serverDataServiceItf.searchFaceByType(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality())+":"+zoneAuthorizeService.userId(), faceFeature,searchFaceDto);
            // filterByTime(faceResultList, starttime, endtime);
            filterByConditions(faceResultList, searchFaceDto);
            filterByNotSolrCondition(faceResultList, searchFaceDto);           
         /*// 反恐库的搜索过滤
            if (type == 0 && searchFaceDto.getBankIds()[0].contains("-")) {
                int fkType = Integer.valueOf(searchFaceDto.getBankIds()[0].split("-")[1]).intValue();
                filterByFktype(faceResultList, fkType);
            }*/
//			for (Map.Entry<Long, List<FaceResultDto>> entry : map.entrySet()) {
//				CameraInfo camera = _cameraInfoDao.findOne(entry.getKey());
//				if(stationId <= 0 || camera.getStationId() == stationId) {
//					faceResultByCameraList.add(new FaceResultByCameraDto(camera, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
//				}
//			}
		} else {
			int k = 0;
			String[] idStrs = ids.split(",");
			String[] datatypes = datatype.split(",");
			for(int num = 0; num<idStrs.length;num++) {
			    if(searchFaceDto.getForceSearch() == 0){
		            int result = checkFaceIsOrNotInRedDetails(Long.valueOf(idStrs[num]),Integer.valueOf(datatypes[num]));
		            if(result > 0){
		                RedDetail rd = redDao.findOne(new Long(result).longValue());
		                if(null != rd){
		                    RedException ex = new RedException(rd.getFaceUrl(),Long.valueOf(idStrs[num]), result, GlobalConsts.search_type_fetch,Integer.valueOf(datatypes[num]));
		                    throw ex;
		                }
		            }
		        }
			    
				String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatypes[num])+":"+Long.valueOf(idStrs[num]), Long.valueOf(idStrs[num]), Integer.valueOf(datatypes[num]));
				List<FaceResultDto> tempFaceResultList = serverDataServiceItf.searchFaceByType(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality()), faceFeature, searchFaceDto);
				//filterByTime(tempFaceResultList, starttime, endtime);
                filterByConditions(tempFaceResultList, searchFaceDto);
                filterByNotSolrCondition(tempFaceResultList, searchFaceDto);
                if (k == 0) {
					faceResultList = tempFaceResultList;
				} else {
					// 多图结果取并集 相似度取最大值
					if(searchFaceDto.getMergeType() == 1) {
						// 第一步 将有相同Camera结果集的合并
						for(FaceResultDto otherFaceRs : tempFaceResultList) {
							int m = 0;
							for(; m<faceResultList.size(); m++) {
								FaceResultDto faceRs = faceResultList.get(m);
								if(faceRs.equals(otherFaceRs)) {
									faceRs.setScore(Math.max(faceRs.getScore(), otherFaceRs.getScore()));
									break;
								}
							}
							if(m == faceResultList.size()) {
								faceResultList.add(otherFaceRs);
							}
						}
					}
					// 多图结果取交集 相似度取平均
					else if (searchFaceDto.getMergeType() == 0) {
						List<FaceResultDto> newFaceResult = new ArrayList<FaceResultDto>();
						for(FaceResultDto faceRs : faceResultList) {
							for(FaceResultDto otherFaceRs : tempFaceResultList) {
								if(faceRs.equals(otherFaceRs)) {
									// 相似度取平均
									faceRs.setScore((faceRs.getScore() + otherFaceRs.getScore())/2);
									newFaceResult.add(faceRs);
									break;
								}
							}
						}
						faceResultList = newFaceResult;
					}
				}
				k++;
			}
		}
		Collections.sort(faceResultList, new FaceResultDtoComparable(searchFaceDto.getSort()));
		return faceResultList;
	}

	@Override
	public List<FaceResultDto> searchFaceByBlackId(long id, float scoreThreshold, int type) throws Exception {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return null;
    	}
//      String faceFeature = this._blackDetailDao.findOne(id, authority.split(",")).get(0).getBase64FaceFeature();
        String faceFeature = memcachedSpace.getFacefeatureFromId(GlobalConsts.BLACK_INFO_TYPE+":"+id, id, GlobalConsts.BLACK_INFO_TYPE);
        return serverDataServiceItf.searchFaceByType(MD5.digest(faceFeature + ":" + scoreThreshold + ":" + type + ":" + zoneAuthorizeService.userId()),
                faceFeature, scoreThreshold, type);
    }

    @Override
	public List<FaceResultDto> getFaceByCameraId(SearchFaceDto searchFaceDto, long cameraId) throws Exception {
		String starttime = null;
		String endtime = null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			format.parse(searchFaceDto.getStarttime());
			starttime = searchFaceDto.getStarttime();
		} catch (Exception e) {
			starttime = DateUtil.getMonthReduce(new Date(), -3);
		}
		try {
			format.parse(searchFaceDto.getEndtime());
			endtime = searchFaceDto.getEndtime();
		} catch (Exception e) {
			endtime = DateUtil.getDateString(new Date())+" 23:59:59";
		}
		searchFaceDto.setStarttime(starttime);
		searchFaceDto.setEndtime(endtime);
		String datatype = searchFaceDto.getDataType();
		long faceId = searchFaceDto.getFaceId();
		float scoreThreshold = searchFaceDto.getScoreThreshold();
		int type = searchFaceDto.getType();
		String ids = searchFaceDto.getIds();
		
		List<FaceResultDto> faceResultList = new ArrayList<FaceResultDto>();
		if(ids.length()==0) {
		    if(searchFaceDto.getForceSearch() == 0){
		        int result = checkFaceIsOrNotInRedDetails(faceId,Integer.valueOf(datatype));
		        if(result > 0){
		            RedDetail rd = redDao.findOne(new Long(result).longValue());
		            if(null != rd){
		                RedException ex = new RedException(rd.getFaceUrl(),faceId, result, GlobalConsts.search_type_fetch,Integer.valueOf(datatype));
		                throw ex;
		            }
		        }
		    }
		    String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatype)+":"+faceId, faceId, Integer.valueOf(datatype));
			Map<Long, List<FaceResultDto>> map = searchFaceForCamera(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality())+":"+zoneAuthorizeService.userId(), faceFeature, searchFaceDto);
			if(map.containsKey(cameraId)) 
                faceResultList.addAll(map.get(cameraId));
        } else {
			int k = 0;
			String[] idStrs = ids.split(",");
			String[] datatypes = datatype.split(",");
			for(int num = 0; num<idStrs.length;num++) {
			    if (searchFaceDto.getForceSearch() == 0) {
                    int result = checkFaceIsOrNotInRedDetails( Long.valueOf(idStrs[num]),Integer.valueOf(datatypes[num]));
                    if(result > 0){
                        RedDetail rd = redDao.findOne(new Long(result).longValue());
                    if(null != rd){
                        RedException ex = new RedException(rd.getFaceUrl(), Long.valueOf(idStrs[num]), result, GlobalConsts.search_type_fetch,Integer.valueOf(datatypes[num]));
                        throw ex;
                    }
                }
                }
				String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatypes[num])+":"+Long.valueOf(idStrs[num]), Long.valueOf(idStrs[num]), Integer.valueOf(datatypes[num]));
				TreeMap<Long, List<FaceResultDto>> map = searchFaceForCamera(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality()), faceFeature,searchFaceDto);
				if(k == 0) {
					if(map.containsKey(cameraId)) 
						faceResultList.addAll(map.get(cameraId));
				} else {
					// 多图结果取并集 相似度取最大值
					if(searchFaceDto.getMergeType() == 1) {
						// 第一步 将有相同Camera结果集的合并
						if(map.containsKey(cameraId)) {
							for(FaceResultDto otherFaceRs : map.get(cameraId)) {
								int m = 0;
								for( ; m<faceResultList.size(); m++) {
									FaceResultDto faceRs = faceResultList.get(m);
									if(faceRs.equals(otherFaceRs)) {
										faceRs.setScore(Math.max(faceRs.getScore(), otherFaceRs.getScore()));
										break;
									}
								}
								if(m == faceResultList.size()) {
									faceResultList.add(otherFaceRs);
								}
							}
						}
					} 
					// 多图结果取交集 相似度取平均
					else if (searchFaceDto.getMergeType() == 0) {
						if(map.containsKey(cameraId)) {
							List<FaceResultDto> newFaceResult = new ArrayList<FaceResultDto>();
							for(FaceResultDto faceRs : faceResultList) {
								for(FaceResultDto otherFaceRs : map.get(cameraId)) {
									if(faceRs.equals(otherFaceRs)) {
										// 相似度取平均
										faceRs.setScore((faceRs.getScore() + otherFaceRs.getScore())/2);
										newFaceResult.add(faceRs);
										break;
									}
								}
							}
							faceResultList = newFaceResult;
						} else {
							return new ArrayList<FaceResultDto>();
						}
					}
				}
				k++;
			}
		}


    Collections.sort(faceResultList, new FaceResultDtoComparable(searchFaceDto.getSort()));

	return faceResultList;
	}

    @Override
	public List<FaceResultDto> getFaceByCameraId(SearchFaceDto searchFaceDto, long cameraId, int page, int pageSize) throws Exception {
		String starttime = null;
		String endtime = null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			format.parse(searchFaceDto.getStarttime());
			starttime = searchFaceDto.getStarttime();
		} catch (Exception e) {
			starttime = DateUtil.getMonthReduce(new Date(), -3);
		}
		try {
			format.parse(searchFaceDto.getEndtime());
			endtime = searchFaceDto.getEndtime();
		} catch (Exception e) {
			endtime = DateUtil.getDateString(new Date())+" 23:59:59";
		}
		searchFaceDto.setStarttime(starttime);
		searchFaceDto.setEndtime(endtime);
		long faceId = searchFaceDto.getFaceId();
		String datatype = searchFaceDto.getDataType();
		float scoreThreshold = searchFaceDto.getScoreThreshold();
		int type = searchFaceDto.getType();
		String ids = searchFaceDto.getIds();
		
		List<FaceResultDto> faceResultList = new ArrayList<FaceResultDto>();
		if(ids.length()==0) {
		    if(searchFaceDto.getForceSearch() == 0){
		        int result = checkFaceIsOrNotInRedDetails(faceId,Integer.valueOf(datatype));
		        if(result > 0){
		            RedDetail rd = redDao.findOne(new Long(result).longValue());
		            if(null != rd){
		                RedException ex = new RedException(rd.getFaceUrl(),faceId, result, GlobalConsts.search_type_fetch,Integer.valueOf(datatype));
		                throw ex;
		            }
		        }
		    }
			String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatype)+":"+faceId, faceId, Integer.valueOf(datatype));
			
			Map<Long, List<FaceResultDto>> map = searchFaceForCamera(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality())+":"+zoneAuthorizeService.userId(), faceFeature, searchFaceDto);
			if(map.containsKey(cameraId)) 
                faceResultList.addAll(map.get(cameraId));
        } else {
			int k = 0;
			String[] idStrs = ids.split(",");
			String[] datatypes = datatype.split(",");
			for(int num = 0; num<idStrs.length;num++) {
			    if (searchFaceDto.getForceSearch() == 0) {
                    int result = checkFaceIsOrNotInRedDetails(Long.valueOf(idStrs[num]),Integer.valueOf(datatypes[num]));
                    if(result > 0){
                        RedDetail rd = redDao.findOne(new Long(result).longValue());
                    if(null != rd){
                        RedException ex = new RedException(rd.getFaceUrl(), Long.valueOf(idStrs[num]), result, GlobalConsts.search_type_fetch,Integer.valueOf(datatypes[num]));
                        throw ex;
                    }
                }
                }
				String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatypes[num])+":"+Long.valueOf(idStrs[num]), Long.valueOf(idStrs[num]), Integer.valueOf(datatypes[num]));
				TreeMap<Long, List<FaceResultDto>> map = searchFaceForCamera(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality()), faceFeature, searchFaceDto);
				if(k == 0) {
					if(map.containsKey(cameraId)) 
						faceResultList.addAll(map.get(cameraId));
				} else {
					// 多图结果取并集 相似度取最大值
					if(searchFaceDto.getMergeType() == 1) {
						// 第一步 将有相同Camera结果集的合并
						if(map.containsKey(cameraId)) {
							for(FaceResultDto otherFaceRs : map.get(cameraId)) {
								int m = 0;
								for( ; m<faceResultList.size(); m++) {
									FaceResultDto faceRs = faceResultList.get(m);
									if(faceRs.equals(otherFaceRs)) {
										faceRs.setScore(Math.max(faceRs.getScore(), otherFaceRs.getScore()));
										break;
									}
								}
								if(m == faceResultList.size()) {
									faceResultList.add(otherFaceRs);
								}
							}
						}
					} 
					// 多图结果取交集 相似度取平均
					else if (searchFaceDto.getMergeType() == 0) {
						if(map.containsKey(cameraId)) {
							List<FaceResultDto> newFaceResult = new ArrayList<FaceResultDto>();
							for(FaceResultDto faceRs : faceResultList) {
								for(FaceResultDto otherFaceRs : map.get(cameraId)) {
									if(faceRs.equals(otherFaceRs)) {
										// 相似度取平均
										faceRs.setScore((faceRs.getScore() + otherFaceRs.getScore())/2);
										newFaceResult.add(faceRs);
										break;
									}
								}
							}
							faceResultList = newFaceResult;
						} else {
							return new ArrayList<FaceResultDto>();
						}
					}
				}
				k++;
			}
		}

		if(faceResultList.size()>page*pageSize) {
			return faceResultList.subList((page-1)*pageSize, page*pageSize);
		} else if(faceResultList.size()>(page-1)*pageSize) {
			return faceResultList.subList((page-1)*pageSize, faceResultList.size());
		} else {
			return new ArrayList<FaceResultDto>();
		}
	}

    private CameraInfo getCameraInfoByCameraId(long id){
        List<String> filterList = new ArrayList<String>();
        filterList.add("id = "+id);
        CameraInfo ci = (CameraInfo) cameraService.queryALLCameraInfoByConditions(filterList).get(0);
        return ci;
    }
	@Override
	public List<FaceResultByCameraDto> getFaceByFaceId(SearchFaceDto searchFaceDto, long stationId, int size) throws Exception {
		String starttime = null;
		String endtime = null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			format.parse(searchFaceDto.getStarttime());
			starttime = searchFaceDto.getStarttime();
		} catch (Exception e) {
			starttime = DateUtil.getMonthReduce(new Date(), -3);
		}
		try {
			format.parse(searchFaceDto.getEndtime());
			endtime = searchFaceDto.getEndtime();
		} catch (Exception e) {
			endtime = DateUtil.getDateString(new Date())+" 23:59:59";
		}
		searchFaceDto.setStarttime(starttime);
		searchFaceDto.setEndtime(endtime);
		long faceId = searchFaceDto.getFaceId();
		String datatype = searchFaceDto.getDataType();
		float scoreThreshold = searchFaceDto.getScoreThreshold();
		int type = searchFaceDto.getType();
		String ids = searchFaceDto.getIds();
		List<FaceResultByCameraDto> faceResultByCameraList = new ArrayList<FaceResultByCameraDto>();
		if(ids.length()==0) {
		    if(searchFaceDto.getForceSearch() == 0){
		        int result = checkFaceIsOrNotInRedDetails(faceId,Integer.valueOf(datatype));
		        if(result > 0){
		            RedDetail rd = redDao.findOne(new Long(result).longValue());
		            if(null != rd){
		                RedException ex = new RedException(rd.getFaceUrl(),faceId, result, GlobalConsts.search_type_fetch,Integer.valueOf(datatype));
		                throw ex;
		            }
		        }
		    }
			String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatype)+":"+faceId, faceId, Integer.valueOf(datatype));

            FileUtil.log("id:" + searchFaceDto.getFaceId() + " type:" + searchFaceDto.getType() + " 开始根据摄像头取数据!");
			TreeMap<Long, List<FaceResultDto>> map = searchFaceForCamera(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality())+":"+zoneAuthorizeService.userId(), faceFeature, searchFaceDto);
        	FileUtil.log("id:"+searchFaceDto.getFaceId()+" type:"+searchFaceDto.getType()+" 完成根据摄像头取数据!");
            for (Map.Entry<Long, List<FaceResultDto>> entry : map.entrySet()) {
                // 不要让重点人员查询摄像头，见 BUG 1467
                if (stationId <= 0 && entry.getKey() < 0) {
                    faceResultByCameraList.add(new FaceResultByCameraDto(null, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
                    continue;
                }
                 CameraInfo camera = CameraInfoThread.cameraMap.get(entry.getKey());
                 if(null == camera){
                   camera = getCameraInfoByCameraId(entry.getKey());
                   if(null != camera){
                       CameraInfoThread.cameraMap.put(entry.getKey(), camera);
                   }
                 }
				// stationId = 0 时为重点人员库检索，故camera必为空，不能认为是错误。
				if(stationId > 0&&camera == null) {
					LOG.error("摄像头不存在："+entry.getKey());
				} else if(stationId <= 0 || camera.getStationId() == stationId) {
					faceResultByCameraList.add(new FaceResultByCameraDto(camera, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
				}
			}
		} else {
			int k = 0;
			String[] idStrs = ids.split(",");
			String[] datatypes = datatype.split(",");
			for(int num = 0; num<idStrs.length;num++) {
			    if (searchFaceDto.getForceSearch() == 0) {
                    int result = checkFaceIsOrNotInRedDetails(Long.valueOf(idStrs[num]),Integer.valueOf(datatypes[num]));
                    if(result > 0){
                        RedDetail rd = redDao.findOne(new Long(result).longValue());
                    if(null != rd){
                        RedException ex = new RedException(rd.getFaceUrl(), Long.valueOf(idStrs[num]), result, GlobalConsts.search_type_fetch,Integer.valueOf(datatypes[num]));
                        throw ex;
                    }
                }
                }
				String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatypes[num])+":"+Long.valueOf(idStrs[num]), Long.valueOf(idStrs[num]), Integer.valueOf(datatypes[num]));
				TreeMap<Long, List<FaceResultDto>> map = searchFaceForCamera(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality()), faceFeature, searchFaceDto);
				if(k == 0) {
					for (Map.Entry<Long, List<FaceResultDto>> entry : map.entrySet()) {
					     // 不要让重点人员查询摄像头，见 BUG 1467
		                if (stationId <= 0 && entry.getKey() < 0) {
		                    faceResultByCameraList.add(new FaceResultByCameraDto(null, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
		                    continue;
		                }
		                 CameraInfo camera = CameraInfoThread.cameraMap.get(entry.getKey());
		                 if(null == camera){
		                   camera = getCameraInfoByCameraId(entry.getKey());
		                   if(null != camera){
		                       CameraInfoThread.cameraMap.put(entry.getKey(), camera);
		                   }
		                 }
						// stationId = 0 时为重点人员库检索，故camera必为空，不能认为是错误。
						if(stationId > 0&&camera == null) {
							LOG.error("摄像头不存在："+entry.getKey());
						} else if(stationId <= 0 || camera.getStationId() == stationId) {
							faceResultByCameraList.add(new FaceResultByCameraDto(camera, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
						}
					}
				} else {
					// 多图结果取并集 相似度取最大值
					if(searchFaceDto.getMergeType() == 1) {
						// 第一步 将有相同Camera结果集的合并
						for(int i = 0; i<faceResultByCameraList.size(); i++) {
							FaceResultByCameraDto faceResult = faceResultByCameraList.get(i);
							List<FaceResultDto> faceRsList = faceResult.getFaceResult();
							if(map.containsKey(faceResult.getCamera().getId())) {
								for(FaceResultDto otherFaceRs : map.get(faceResult.getCamera().getId())) {
									int m = 0;
									for(; m<faceRsList.size(); m++) {
										FaceResultDto faceRs = faceRsList.get(m);
										if(faceRs.equals(otherFaceRs)) {
											faceRs.setScore(Math.max(faceRs.getScore(), otherFaceRs.getScore()));
											break;
										}
									}
									if(m == faceRsList.size()) {
										faceRsList.add(otherFaceRs);
									}
								}
								map.remove(faceResult.getCamera().getId());
							}
						}
						// 第二步 将多出的Camera结果集Add进来
						for (Map.Entry<Long, List<FaceResultDto>> entry : map.entrySet()) {
			                if (stationId <= 0 && entry.getKey() < 0) {
			                    faceResultByCameraList.add(new FaceResultByCameraDto(null, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
			                    continue;
			                }
			                 CameraInfo camera = CameraInfoThread.cameraMap.get(entry.getKey());
			                 if(null == camera){
			                   camera = getCameraInfoByCameraId(entry.getKey());
			                   if(null != camera){
			                       CameraInfoThread.cameraMap.put(entry.getKey(), camera);
			                   }
			                 }
							// stationId = 0 时为重点人员库检索，故camera必为空，不能认为是错误。
							if(stationId > 0&&camera == null) {
								LOG.error("摄像头不存在："+entry.getKey());
							} else if(stationId <= 0 || camera.getStationId() == stationId) {
								faceResultByCameraList.add(new FaceResultByCameraDto(camera, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
							}
						}
					} 
					// 多图结果取交集 相似度取平均
					else if (searchFaceDto.getMergeType() == 0) {
						for(int i = faceResultByCameraList.size()-1; i>=0; i--) {
							FaceResultByCameraDto faceResult = faceResultByCameraList.get(i);
							if(map.containsKey(faceResult.getCamera().getId())) {
								List<FaceResultDto> newFaceResult = new ArrayList<FaceResultDto>();
								for(FaceResultDto faceRs :faceResult.getFaceResult()) {
									for(FaceResultDto otherFaceRs : map.get(faceResult.getCamera().getId())) {
										if(faceRs.equals(otherFaceRs)) {
											// 相似度取平均
											faceRs.setScore((faceRs.getScore() + otherFaceRs.getScore())/2);
											newFaceResult.add(faceRs);
											break;
										}
									}
								}
								if(newFaceResult.size()>0) {
									faceResult.setFaceResult(newFaceResult);
								} else {
									faceResultByCameraList.remove(i);
								}
							} else {
								faceResultByCameraList.remove(i);
							}
						}
					}
				}
				k++;
			}
		}
		return faceResultByCameraList;
	}

	private long getDistrictIdByStationId(long id){
		long dId = 0;
		Area area = _areaDao.findOne(id);
		if(null == area){
		OtherArea  oArea= _otherAreaDao.findOne(id);
		if(null != oArea){
			dId = oArea.getDistrictId();
		}
		}else{
			dId = area.getDistrictId();
		}
		return dId;
	}
	@Override
	public FaceSearchStatisticDto getFaceStatistic(SearchFaceDto searchFaceDto) throws Exception {
		int type = searchFaceDto.getType();
		FaceSearchStatisticDto faceSearchStatistic = new FaceSearchStatisticDto();
		// 抓拍与候问室统计需要分摄像头统计
		if(type==GlobalConsts.BLACK_INFO_TYPE||type==GlobalConsts.FACE_INFO_TYPE||type==GlobalConsts.INSTATION_INFO_TYPE) {
			FileUtil.log("id:"+searchFaceDto.getFaceId()+" type:"+searchFaceDto.getType()+" 开始获取数据!");
			List<FaceResultByCameraDto> rsList = getFaceByFaceId(searchFaceDto, 0, 2000000000);
			FileUtil.log("id:"+searchFaceDto.getFaceId()+" type:"+searchFaceDto.getType()+" 完成获取数据!");

			int total = 0;
			HashMap<Long, Integer> stationStatistic = new HashMap<Long, Integer>();
			HashMap<Long, Integer> cameraStatistic = new HashMap<Long, Integer>();
			HashMap<Long, Integer> districtStatistic = new HashMap<Long, Integer>();
			Date startTime = null;
			Date endTime = null;
			LOG.info("statistic faceResultList:"+rsList.size()+",search faceId:"+searchFaceDto.getFaceId());
            if (type == GlobalConsts.BLACK_INFO_TYPE) {
                total = faceService.parseBlackFaceResultList(0, Integer.MAX_VALUE, rsList).size();
            } else {
                for (FaceResultByCameraDto rs : rsList) {

                    // 按摄像头统计总数
                    Long cameraId = rs.getCamera().getId();
                if (null == cameraId || cameraId.equals(0L)) {
                    continue;
                }
				if(cameraStatistic.containsKey(cameraId)) {
					int num = cameraStatistic.get(cameraId)+rs.getFaceResult().size();
					cameraStatistic.put(cameraId, num);
				} else {
					cameraStatistic.put(cameraId, rs.getFaceResult().size());
				}

				// 按所统计总数
				Long stationId = rs.getCamera().getStationId(); //当type = 0重点人员统计时 stationId = 0正常不能排除
                                                                // stationId =
                                                                // 0正常不能排除
                if (null != stationId) {
                    if (stationStatistic.containsKey(stationId)) {
                        int num = stationStatistic.get(stationId) + rs.getFaceResult().size();
                        stationStatistic.put(stationId, num);
                    } else {
                        stationStatistic.put(stationId, rs.getFaceResult().size());
                    }

                    // 按行政区域统计总数
                    long dId = getDistrictIdByStationId(stationId);

                    if (districtStatistic.containsKey(dId)) {
                        int num = districtStatistic.get(dId) + rs.getFaceResult().size();
                        districtStatistic.put(dId, num);
                    } else {
                        districtStatistic.put(dId, rs.getFaceResult().size());
                    }
                }
                

				// 统计总数
				total += rs.getFaceResult().size();
//				LOG.info("camera id:"+cameraId+",count:"+rs.getFaceResult().size());

				if(null!= rs.getFaceResult() && rs.getFaceResult().size() > 0) {
					if(startTime == null) {
						startTime = rs.getFaceResult().get(rs.getFaceResult().size()-1).getTime();
					} else  if(rs.getFaceResult().get(rs.getFaceResult().size()-1).getTime().getTime() < startTime.getTime()) {
						startTime = rs.getFaceResult().get(rs.getFaceResult().size()-1).getTime();
					}
					if(endTime == null) {
						endTime = rs.getFaceResult().get(0).getTime();
					} else if(rs.getFaceResult().get(0).getTime().getTime() > endTime.getTime()) {
                            endTime = rs.getFaceResult().get(0).getTime();
                        }
                    }
				}
			}
			if(startTime == null) {
				startTime = new Date();
			}
			if(endTime == null) {
				endTime = new Date();
			}
			faceSearchStatistic.setTotal(total);
			faceSearchStatistic.setCameraStatistic(cameraStatistic);
			faceSearchStatistic.setStationStatistic(stationStatistic);
			faceSearchStatistic.setDistrictStatistic(districtStatistic);
			faceSearchStatistic.setStartTime(startTime);
			faceSearchStatistic.setEndTime(endTime);
		} 
		// 警务云数据有比中确认特殊处理
		else {
			List<FaceResultDto> faceResultList = searchFaceByIdInBank(searchFaceDto);
			int total = 0;
			if(faceResultList.size()==0||faceResultList==null){
				faceSearchStatistic.setTotal(0);
				return faceSearchStatistic;
			}
			List<Long> ids = new ArrayList<Long>();             
			for(FaceResultDto faceRs : faceResultList) {
				ids.add(faceRs.getCamera());
			}
			switch(searchFaceDto.getType()) {
			case GlobalConsts.CID_INFO_TYPE:{ //type为3 即 是警务云 中身份信息的搜素
				List<CidInfo> cidList = cidInfoRepository.findByIds(ids.toArray(new Long[ids.size()]));  
				Map<Long, CidInfo> cidMap = new HashMap<Long, CidInfo> ();
				Map<String, Boolean> flagMap = new HashMap<String, Boolean> ();
				for(CidInfo cid : cidList) {
					cidMap.put(cid.getId(), cid);
				}
				for(int i = 0; i<faceResultList.size(); i++) {
					FaceResultDto faceRs = faceResultList.get(i);
					if(cidMap.containsKey(faceRs.getCamera())) {
						if(faceRs.getFile().endsWith("?vip")) {
							if(flagMap.containsKey(cidMap.get(faceRs.getCamera()).getPhoto())) continue;
							flagMap.put(cidMap.get(faceRs.getCamera()).getPhoto(), true);
						} else {
							if(flagMap.containsKey(faceRs.getFile())) continue;
							flagMap.put(faceRs.getFile(), true);
						}
						total++;
					}
				}
			}break;
			case GlobalConsts.JUZHU_INFO_TYPE:{ //type为4 即 是警务云 中居民证信息的搜素
				List<JuZhuInfo> juzhuList = juzhuInfoRepository.findByIds(ids.toArray(new Long[ids.size()]));  
				Map<Long, JuZhuInfo> juzhuMap = new HashMap<Long, JuZhuInfo> ();
				Map<String, Boolean> flagMap = new HashMap<String, Boolean> (); 
				for(JuZhuInfo juzhuid : juzhuList) {
					juzhuMap.put(juzhuid.getId(), juzhuid);
				}
				for(int i = 0; i<faceResultList.size(); i++) {
					FaceResultDto faceRs = faceResultList.get(i);
					if(juzhuMap.containsKey(faceRs.getCamera())) {
						if(faceRs.getFile().endsWith("?vip")) {
							if(flagMap.containsKey(juzhuMap.get(faceRs.getCamera()).getPhoto())) continue;
							flagMap.put(juzhuMap.get(faceRs.getCamera()).getPhoto(), true);
						} else {
							if(flagMap.containsKey(faceRs.getFile())) continue;
							flagMap.put(faceRs.getFile(), true);
						}
						total++;
					}
				}
			}break;
			case GlobalConsts.SEARCH_INFO_TYPE:{				
				   //搜索库的搜素结果得排除本身这一次的搜索记录
				    String login = CurUserInfoUtil.getUserInfo().getLogin();
	                String fids = searchFaceDto.getIds();
	                if(fids.length()==0) {
	                    String searchFaceUrl = getFaceUrlFromId(searchFaceDto.getFaceId(),Integer.valueOf(searchFaceDto.getDataType()));
	                    Iterator<FaceResultDto> faceList = faceResultList.iterator(); 
	                    while (faceList.hasNext()) { 
	                        FaceResultDto faceNext = faceList.next();
	                        HistorySearchOperationDetailDto historySearchOpe = _auditService.findSearchAuditDeatil(Long.parseLong(faceNext.getId()));
	                        String searchUser = historySearchOpe.getOperator();
	                        String imageDate = faceNext.getFile();
	                        if(imageDate.equals(searchFaceUrl)&&searchUser.equals(login)){
	                            faceList.remove();
	                        }
	                    }   
	                } else {
	                    for(int i = 0; i<fids.split(",").length; i++) {
	                        String searchFaceUrl = getFaceUrlFromId(Long.valueOf(fids.split(",")[i]),Integer.valueOf(searchFaceDto.getDataType().split(",")[i]));
	                        Iterator<FaceResultDto> faceList = faceResultList.iterator(); 
	                        while (faceList.hasNext()) { 
	                            FaceResultDto faceNext = faceList.next();
	                            HistorySearchOperationDetailDto historySearchOpe = _auditService.findSearchAuditDeatil(Long.parseLong(faceNext.getId()));
	                            String searchUser = historySearchOpe.getOperator();
	                            String imageDate = faceNext.getFile();
	                            if(imageDate.equals(searchFaceUrl)&&searchUser.equals(login)){
	                                faceList.remove();
	                            }
	                        }
	                    }
	                }       
					total = faceResultList.size();				
			}break;
//			case GlobalConsts.CRIME_INFO_TYPE:{
//				total = faceResultList.size();
//			}break;
//			case GlobalConsts.POLICE_INFO_TYPE:{
//				total = faceResultList.size();
//			}break;
			default:{
				total = faceResultList.size();
			}
			}
			faceSearchStatistic.setTotal(total);
		}
		
		return faceSearchStatistic;
	}

    @Override
    public List<FaceResultDto> searchFaceByIdInCamera(long faceId, float scoreThreshold, int type, long cameraId, int hours) throws Exception {
//  String faceFeature = this.faceService.findOne(faceId).getBase64FaceFeature();
    String faceFeature = memcachedSpace.getFacefeatureFromId(GlobalConsts.FACE_INFO_TYPE+":"+faceId, faceId, GlobalConsts.FACE_INFO_TYPE);
        return serverDataServiceItf.searchFaceByCamera(MD5.digest(faceFeature + ":" + scoreThreshold + ":" + cameraId+ ":" + zoneAuthorizeService.userId()+":"+hours), cameraId, faceFeature, scoreThreshold,
                type,hours);
    }

	@Override
	public ConcurrentSkipListMap<Long, BankMatchResultTuple> searchFaceByDatasetInBank(long targetbankid, int staticbankid, long key, final int matchnum) throws Exception {
		ConcurrentSkipListMap<Long, BankMatchResultTuple> result = null;
		try {
			List<BlackDetailRealName> blackDetailList = this.personDetailServiceItf.findBlackDetailByBankId(targetbankid);
			ProcessInfo process = new ProcessInfo();
			process.setTotalSize(blackDetailList.size());
			GlobalConsts.bankMatchMap.put(key, process);
			result = this.serverDataServiceItf.bankMatch(blackDetailList, targetbankid, staticbankid, key, matchnum);
			BankCollisionController.cachedMatchBankResults.get(String.valueOf(targetbankid)).put(staticbankid, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public ConcurrentSkipListMap<Long, BankMatchResultTuple> searchFaceByNewRecordsInBank(long blackid, long targetbankid, int staticbankid, long key, final int matchnum) throws Exception {
		try {
			List<BlackDetailRealName> blackDetailListGreater = this.personDetailServiceItf.findAllGreaterId(targetbankid, blackid);
			List<BlackDetailRealName> blackDetailListLess = this.personDetailServiceItf.findAllLessId(targetbankid, blackid);
			if((blackDetailListGreater != null && blackDetailListGreater.size() > 0) || (blackDetailListLess == null || blackDetailListLess.size() < 1)) {
				searchFaceByDatasetInBank(targetbankid, staticbankid, key, matchnum);
			} else {
				final ProcessInfo process = new ProcessInfo();
				GlobalConsts.bankMatchMap.put(key, process);
				process.setTotalSize(BankCollisionController.cachedMatchBankResults.get(String.valueOf(targetbankid)).get(staticbankid).size());
				process.setSuccessNum(process.getTotalSize());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BankCollisionController.cachedMatchBankResults.get(String.valueOf(targetbankid)).get(staticbankid);
	}
	
	public TreeMap<Long, List<FaceResultDto>> searchFaceForCamera(String key, String faceFeature,SearchFaceDto searchDto) throws Exception {
		String starttime = null;
		String endtime = null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			format.parse(searchDto.getStarttime());
			starttime = searchDto.getStarttime();
		} catch (Exception e) {
			starttime = DateUtil.getMonthReduce(new Date(), -3);
		}
		try {
			format.parse(searchDto.getEndtime());
			endtime = searchDto.getEndtime();
		} catch (Exception e) {
			endtime = DateUtil.getDateString(new Date())+" 23:59:59";
		}
		searchDto.setStarttime(starttime);
		searchDto.setEndtime(endtime);
		TreeMap<Long, List<FaceResultDto>> faceRsMap = new TreeMap<Long, List<FaceResultDto>>();

    	FileUtil.log("key:"+key+" type:"+searchDto.getType()+" 开始Solr取数据!");
		List<FaceResultDto> pojos = serverDataServiceItf.searchFaceByType(key, faceFeature, searchDto);
    	FileUtil.log("key:"+key+" type:"+searchDto.getType()+" 完成Solr取数据!");
    	
		filterByConditions(pojos, searchDto);
		filterByNotSolrCondition(pojos, searchDto);
		Collections.sort(pojos, new FaceResultDtoComparable(searchDto.getSort()));
		if(searchDto.getType() == 0) {
			faceRsMap.put(-1L, pojos);
		} else {
			for(FaceResultDto face : pojos) {
				if(faceRsMap.containsKey(face.getCamera())){
					faceRsMap.get(face.getCamera()).add(face);
				} else {
					List<FaceResultDto> faceMap = new ArrayList<FaceResultDto>();
					faceMap.add(face);
					faceRsMap.put(face.getCamera(), faceMap);
				}
			}
		}
		return faceRsMap;
	}

	
	public List<FaceResultDto> filterByTime(List<FaceResultDto> pojos, Date starttime, Date endtime) {
		if(starttime!=null && endtime!=null && pojos.size()>0) {
			for(int i = pojos.size()-1; i >=0; i--) {
				if((endtime!=null && pojos.get(i).getTime().getTime() > endtime.getTime()) || (starttime!=null &&pojos.get(i).getTime().getTime() < starttime.getTime())) {
					pojos.remove(i);
				}
			}
		}
		return pojos;
	}
	
    /**
     * 根据faceId判断人脸是否在红名单内 ,当result 为大于0值时，代表匹配上了红名单，红名单ID为result值
     * @param id
     * @return
     */
    public int checkFaceIsOrNotInRedDetails(long id,int dataType){
        int result = 0;
        if(id == 0){
            return 0;
        }
        if(GlobalConsts.redConfig == null){
            getRedSwitch();
        }
        if(GlobalConsts.redConfig.getConValue()==0){
            return 0;
        }
           //通知给C++引擎
        result = memcachedSpace.checkFaceIsOrNotInRedDetails(dataType+":"+id, id, dataType);
        return result;
    }
    /**
     * 特殊条件下对结果集进行特殊过滤，过滤字段为非索引条件 v1.2.7添加针对静态库搜索过滤
     * @param rsList
     */
    private void filterByNotSolrCondition(List<FaceResultDto> faceResultList,SearchFaceDto searchFaceDto){
        if(GlobalConsts.POLICE_INFO_TYPE==searchFaceDto.getType()){
            //警综库再过滤 
            String[] caseIdArray = searchFaceDto.getCaseNumArray();
            List<String> caseList = new ArrayList<String>();
            if(null != caseIdArray){//过滤不同案件类型
              for(String item : caseIdArray){
                  caseList.add(item);
              }
              if(faceResultList.size() > 0){
                  for(int i = faceResultList.size()-1; i >=0; i--) {
                      long iid = faceResultList.get(i).getCamera();
                      OtherInfo oi = otherInfoRepository.findOne(iid);
                      if(null == oi || !caseList.contains(String.valueOf(oi.getDatatype()))){
                          faceResultList.remove(i);
                          continue;
                      }
                  }
              }
            }
        }
        
        if (GlobalConsts.MOBILE_INFO_TYPE == searchFaceDto.getType()) {
            // 移动采集人脸库根据单位再过滤
          long pId =  searchFaceDto.getPoliceStationId();
          LOG.info("mobile info type 11 filter,search statistic，policeStationId:"+pId);
            if (0 != pId && !faceResultList.isEmpty()) {
                String pStr = String.valueOf(pId);
                    for (int i = faceResultList.size() - 1; i >= 0; i--) {
                        long iid = faceResultList.get(i).getCamera();
                        OtherInfo oi = otherInfoRepository.findOne(iid);
                        if (null == oi || !pStr.equals(oi.getExtendField4())) {
                            faceResultList.remove(i);
                            continue;
                        }
                    }
                }
            }
    }
	/**
	 * 根据检索条件对solr查询结果集进行过滤
	 * @param pojos
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public List<FaceResultDto> filterByConditions(List<FaceResultDto> pojos, SearchFaceDto searchDto) {
		Date dayStartTime = null;
		Date dayEndTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		try{
		if(searchDto.getDayStartTime() != null&&!"".equals(searchDto.getDayStartTime())) {
			dayStartTime = sdf.parse(searchDto.getDayStartTime());
		}
		if(searchDto.getDayEndTime() != null&&!"".equals(searchDto.getDayEndTime())) {
			dayEndTime = sdf.parse(searchDto.getDayEndTime());
		}
		
		
		if(pojos.size()>0) {
			for(int i = pojos.size()-1; i >=0; i--) {
				FaceResultDto result = pojos.get(i);
				if(null ==result.getTime()) continue;
				Date fDate = result.getTime();
				String fStr = sdf.format(fDate);
				Date fDate1 = sdf.parse(fStr);
				int week = DateUtil.convertDateToWeek(fDate);
				
//				if((endtime!=null && fDate.getTime() > endtime.getTime()) || (starttime!=null &&fDate.getTime() < starttime.getTime())) {
//					pojos.remove(i);
//					continue;
//				}
				if((dayEndTime!=null && fDate1.getTime() > dayEndTime.getTime()) || (dayStartTime!=null &&fDate1.getTime() < dayStartTime.getTime())) {
					pojos.remove(i);
					continue;
				}
				if(!"0".equals(searchDto.getWeekDay()) && !"".equals(searchDto.getWeekDay()) && null != searchDto.getWeekDay() && 0!=week && !CommonUtil.checkWeekDay(searchDto.getWeekDay(), week)){
					pojos.remove(i);
					continue;
				}
				/*if((0!=searchDto.getAge()) && (0!=result.getAge()) && (!CommonUtil.checkAge(searchDto.getAge(), result.getAge()))){
					pojos.remove(i);
					continue;
				}
				if((0!=searchDto.getAccessories()) && (0!=result.getAccessories()) && (result.getAccessories()!=searchDto.getAccessories())){
					pojos.remove(i);
					continue;
				}
				if((0!=searchDto.getGender()) && (0!=result.getGender()) && (result.getGender()!=(searchDto.getGender()))){
					pojos.remove(i);
					continue;
				}
				if(null != searchDto.getRace() && !"".equals(searchDto.getRace()) && !CommonUtil.checkRace(searchDto.getRace(), result.getRace())){
					pojos.remove(i);
					continue;
				}*/
			}
		}
		}catch(Exception e){
			LOG.error("检索过滤出错：",e);
		}
		return pojos;
	}
	
	 private void getRedSwitch(){
	    	List<IFaceConfig> switchList = (List<IFaceConfig>) ifaceConfigDao.findByConKey(GlobalConsts.IFACE_CONFIG_RED);
	    	IFaceConfig rSwitch = null;
			if(null == switchList || switchList.isEmpty()){
				IFaceConfig rs = new IFaceConfig(GlobalConsts.IFACE_CONFIG_RED,0,"红名单开关");
				rSwitch = ifaceConfigDao.save(rs);
			}else{
				rSwitch = switchList.get(0);
			}
			GlobalConsts.redConfig= rSwitch;
	    	
	    }
	 
	 
	 private String getFaceUrlFromId(long faceId, int datatype) throws Exception {
			String faceUrl = "";
			try {
				switch(datatype) {
				case 0: {
					String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
					if(authority.trim().length()==0){
		        		return faceUrl;
		        	}
					faceUrl = this._blackDetailDao.findOne(faceId, authority.split(",")).get(0).getImageData();
					break;
				}
				case 1: {
					faceUrl = this.faceService.findOne(faceId).getImageData();
					break;
				}
				case 2: {
					faceUrl = this.faceService.findOne(faceId).getImageData();
					break;
				}
				case 3: {
					faceUrl = this.cidDetailRepository.findOne(faceId).getImageData();
					break;
				}
				case 4: {
					faceUrl = this.juZhuDetailRepository.findOne(faceId).getImageData();
					break;
				}
				case 5: {
					faceUrl = this.otherDetailRepository.findOne(faceId).getImageData();
					break;
				}
				case 6: {
					faceUrl = this.otherDetailRepository.findOne(faceId).getImageData();
					break;
				}
				default: {
					faceUrl = this.otherDetailRepository.findOne(faceId).getImageData();
					break;
				}
				}
			}catch (Exception e) {
				LOG.error("faceId :"+faceId+" datatype:"+datatype +" 数据不存在！", e);
				throw new MsgException(Constants.error_face_null);
			}
			return faceUrl;
		}
	 
	 
	    // 搜索重点人员库时 根据 反恐子类进行过滤
	    public List<FaceResultDto> filterByFktype(List<FaceResultDto> faecList, int fkType) {

	        Iterator<FaceResultDto> face = faecList.iterator();
	        while (face.hasNext()) {
	            FaceResultDto faceNext = face.next();
	            BlackDetail blackDetail = blackDetailDao.findById(Long.valueOf(faceNext.getId()).longValue()).get(0);
	            long personDetailId = blackDetail.getFromPersonId();
	            PersonDetail personDetail = personDetailDao.findOne(personDetailId);
	            if (personDetail != null) {
	                int fType = personDetail.getFkType();
	                if (fType != fkType) {
	                    face.remove();
	                }
	            }

	        }

	        return faecList;

	    }

	    /**
	     * getFaceByFaceId方法替代方法，支持多个Area区域,若搜索非区域采集数据,原areaId传0改为现在传null
	     * @author shixiaohua
	     */
        @Override
        public List<FaceResultByCameraDto> getFaceByFaceIdAreas(SearchFaceDto searchFaceDto, Set<Long> areaIds, int size) throws Exception {

            String starttime = null;
            String endtime = null;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                format.parse(searchFaceDto.getStarttime());
                starttime = searchFaceDto.getStarttime();
            } catch (Exception e) {
                starttime = DateUtil.getMonthReduce(new Date(), -3);
            }
            try {
                format.parse(searchFaceDto.getEndtime());
                endtime = searchFaceDto.getEndtime();
            } catch (Exception e) {
                endtime = DateUtil.getDateString(new Date())+" 23:59:59";
            }
            searchFaceDto.setStarttime(starttime);
            searchFaceDto.setEndtime(endtime);
            long faceId = searchFaceDto.getFaceId();
            String datatype = searchFaceDto.getDataType();
            float scoreThreshold = searchFaceDto.getScoreThreshold();
            int type = searchFaceDto.getType();
            String ids = searchFaceDto.getIds();
            List<FaceResultByCameraDto> faceResultByCameraList = new ArrayList<FaceResultByCameraDto>();
            if(ids.length()==0) {
                if(searchFaceDto.getForceSearch() == 0){
                    int result = checkFaceIsOrNotInRedDetails(faceId,Integer.valueOf(datatype));
                    if(result > 0){
                        RedDetail rd = redDao.findOne(new Long(result).longValue());
                        if(null != rd){
                            RedException ex = new RedException(rd.getFaceUrl(),faceId, result, GlobalConsts.search_type_fetch,Integer.valueOf(datatype));
                            throw ex;
                        }
                    }
                }
                String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatype)+":"+faceId, faceId, Integer.valueOf(datatype));

                FileUtil.log("id:" + searchFaceDto.getFaceId() + " type:" + searchFaceDto.getType() + " 开始根据摄像头取数据!");
                TreeMap<Long, List<FaceResultDto>> map = searchFaceForCamera(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality())+":"+zoneAuthorizeService.userId(), faceFeature, searchFaceDto);
                FileUtil.log("id:"+searchFaceDto.getFaceId()+" type:"+searchFaceDto.getType()+" 完成根据摄像头取数据!");
                for (Map.Entry<Long, List<FaceResultDto>> entry : map.entrySet()) {
                    // 不要让重点人员查询摄像头，见 BUG 1467
                    if (null == areaIds && entry.getKey() < 0) {
                        faceResultByCameraList.add(new FaceResultByCameraDto(null, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
                        continue;
                    }
                     CameraInfo camera = CameraInfoThread.cameraMap.get(entry.getKey());
                     if(null == camera){
                       camera = getCameraInfoByCameraId(entry.getKey());
                       if(null != camera){
                           CameraInfoThread.cameraMap.put(entry.getKey(), camera);
                       }
                     }
                    // stationId = 0 时为重点人员库检索，故camera必为空，不能认为是错误。
                    if(null != areaIds &&camera == null) {
                        LOG.error("摄像头不存在："+entry.getKey());
                    } else if(null == areaIds || areaIds.contains(camera.getStationId())) {
                        faceResultByCameraList.add(new FaceResultByCameraDto(camera, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
                    }
                }
            } else {
                int k = 0;
                String[] idStrs = ids.split(",");
                String[] datatypes = datatype.split(",");
                for(int num = 0; num<idStrs.length;num++) {
                    if (searchFaceDto.getForceSearch() == 0) {
                        int result = checkFaceIsOrNotInRedDetails(Long.valueOf(idStrs[num]),Integer.valueOf(datatypes[num]));
                        if(result > 0){
                            RedDetail rd = redDao.findOne(new Long(result).longValue());
                        if(null != rd){
                            RedException ex = new RedException(rd.getFaceUrl(), Long.valueOf(idStrs[num]), result, GlobalConsts.search_type_fetch,Integer.valueOf(datatypes[num]));
                            throw ex;
                        }
                    }
                    }
                    String faceFeature = memcachedSpace.getFacefeatureFromId(Integer.valueOf(datatypes[num])+":"+Long.valueOf(idStrs[num]), Long.valueOf(idStrs[num]), Integer.valueOf(datatypes[num]));
                    TreeMap<Long, List<FaceResultDto>> map = searchFaceForCamera(MD5.digest(faceFeature+":"+scoreThreshold+":"+type+":"+starttime+":"+endtime+":"+searchFaceDto.getSort()+":"+searchFaceDto.getQuality()), faceFeature, searchFaceDto);
                    if(k == 0) {
                        for (Map.Entry<Long, List<FaceResultDto>> entry : map.entrySet()) {
                             // 不要让重点人员查询摄像头，见 BUG 1467
                            if (null == areaIds && entry.getKey() < 0) {
                                faceResultByCameraList.add(new FaceResultByCameraDto(null, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
                                continue;
                            }
                             CameraInfo camera = CameraInfoThread.cameraMap.get(entry.getKey());
                             if(null == camera){
                               camera = getCameraInfoByCameraId(entry.getKey());
                               if(null != camera){
                                   CameraInfoThread.cameraMap.put(entry.getKey(), camera);
                               }
                             }
                            // stationId = 0 时为重点人员库检索，故camera必为空，不能认为是错误。
                            if(null != areaIds && camera == null) {
                                LOG.error("摄像头不存在："+entry.getKey());
                            } else if(null == areaIds || areaIds.contains(camera.getStationId())) {
                                faceResultByCameraList.add(new FaceResultByCameraDto(camera, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
                            }
                        }
                    } else {
                        // 多图结果取并集 相似度取最大值
                        if(searchFaceDto.getMergeType() == 1) {
                            // 第一步 将有相同Camera结果集的合并
                            for(int i = 0; i<faceResultByCameraList.size(); i++) {
                                FaceResultByCameraDto faceResult = faceResultByCameraList.get(i);
                                List<FaceResultDto> faceRsList = faceResult.getFaceResult();
                                if(map.containsKey(faceResult.getCamera().getId())) {
                                    for(FaceResultDto otherFaceRs : map.get(faceResult.getCamera().getId())) {
                                        int m = 0;
                                        for(; m<faceRsList.size(); m++) {
                                            FaceResultDto faceRs = faceRsList.get(m);
                                            if(faceRs.equals(otherFaceRs)) {
                                                faceRs.setScore(Math.max(faceRs.getScore(), otherFaceRs.getScore()));
                                                break;
                                            }
                                        }
                                        if(m == faceRsList.size()) {
                                            faceRsList.add(otherFaceRs);
                                        }
                                    }
                                    map.remove(faceResult.getCamera().getId());
                                }
                            }
                            // 第二步 将多出的Camera结果集Add进来
                            for (Map.Entry<Long, List<FaceResultDto>> entry : map.entrySet()) {
                                if (null == areaIds && entry.getKey() < 0) {
                                    faceResultByCameraList.add(new FaceResultByCameraDto(null, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
                                    continue;
                                }
                                 CameraInfo camera = CameraInfoThread.cameraMap.get(entry.getKey());
                                 if(null == camera){
                                   camera = getCameraInfoByCameraId(entry.getKey());
                                   if(null != camera){
                                       CameraInfoThread.cameraMap.put(entry.getKey(), camera);
                                   }
                                 }
                                // stationId = 0 时为重点人员库检索，故camera必为空，不能认为是错误。
                                if(null != areaIds&&camera == null) {
                                    LOG.error("摄像头不存在："+entry.getKey());
                                } else if(null == areaIds || areaIds.contains(camera.getStationId())) {
                                    faceResultByCameraList.add(new FaceResultByCameraDto(camera, entry.getValue().subList(0, entry.getValue().size()>size?size:entry.getValue().size())));
                                }
                            }
                        } 
                        // 多图结果取交集 相似度取平均
                        else if (searchFaceDto.getMergeType() == 0) {
                            for(int i = faceResultByCameraList.size()-1; i>=0; i--) {
                                FaceResultByCameraDto faceResult = faceResultByCameraList.get(i);
                                if(map.containsKey(faceResult.getCamera().getId())) {
                                    List<FaceResultDto> newFaceResult = new ArrayList<FaceResultDto>();
                                    for(FaceResultDto faceRs :faceResult.getFaceResult()) {
                                        for(FaceResultDto otherFaceRs : map.get(faceResult.getCamera().getId())) {
                                            if(faceRs.equals(otherFaceRs)) {
                                                // 相似度取平均
                                                faceRs.setScore((faceRs.getScore() + otherFaceRs.getScore())/2);
                                                newFaceResult.add(faceRs);
                                                break;
                                            }
                                        }
                                    }
                                    if(newFaceResult.size()>0) {
                                        faceResult.setFaceResult(newFaceResult);
                                    } else {
                                        faceResultByCameraList.remove(i);
                                    }
                                } else {
                                    faceResultByCameraList.remove(i);
                                }
                            }
                        }
                    }
                    k++;
                }
            }
            return faceResultByCameraList;
        
        }

}
