package intellif.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import intellif.consts.GlobalConsts;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dto.PoliceManDto;
import intellif.dto.ShowAlarmInfoDto;
import intellif.service.CameraServiceItf;
import intellif.service.PoliceManServiceItf;
import intellif.database.entity.FaceInfo;

@Service
public class PoliceManServiceImpl implements PoliceManServiceItf {

	private static Logger LOG = LogManager
			.getLogger(PoliceManServiceImpl.class);

	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	FaceInfoDaoImpl faceInfoDao;
	@Autowired
	CameraServiceItf cameraService;
	
	public static BigInteger policeManTotalNum=null;   //获取警员数的最大page数

	@Override
	public List<ShowAlarmInfoDto> findByUserId(String id, int pagesize) {

		List<ShowAlarmInfoDto> resp = null;

		List<BigInteger> faceid = new ArrayList<BigInteger>();

		List<FaceInfo> faceList = null;

		HashMap<BigInteger, FaceInfo> facemap = new HashMap<BigInteger, FaceInfo>();

		String sqlString = "SELECT b.id,a.id alarm_id,a.confidence,a.task_id,a.`level`,a.time,a.black_id,a.face_id , c.from_person_id,d.real_name,d.photo_data ,d.real_gender,e.task_name camera_name FROM "
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_ALARM_INFO
				+ " a,"
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_PUSH_ALARM_INFO
				+ " b ,"
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_BLACK_DETAIL
				+ " c,"
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_PERSON_DETAIL
				+ " d ,"
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_TASK_INFO
				+ " e "
				+ " where b.receiver_no= '"
				+ id
				+ "' and b.alarm_id=a.id and b.checked=0 and c.id=a.black_id and d.id=c.from_person_id and e.id=a.task_id ORDER BY b.id desc limit 0,"
				+ pagesize + ";";
		System.out.println("获取未读告警消息的sql语句：" + sqlString);

		try {
			Query query = this.entityManager.createNativeQuery(sqlString,
					ShowAlarmInfoDto.class);
			resp = (ArrayList<ShowAlarmInfoDto>) query.getResultList();

			// 此时 resp对象里面的imagedata 和 cameraname 字段还是空的
			// 所以得查face表和camerainfo表来拼装
			for (int i = 0; i < resp.size(); i++) {
				faceid.add(resp.get(i).getFaceId());
			}

			faceList = faceInfoDao.findByIds(faceid);

			for (int i = 0; i < faceList.size(); i++) {
				facemap.put(new BigInteger(faceList.get(i).getId().toString()),
						faceList.get(i));
			}

			System.out.println("resp的大小：" + resp.size() + "faceid的大小： "
					+ faceid.size() + "faceList的大小：" + faceList.size());

			for (int j = 0; j < resp.size(); j++) {

				FaceInfo f = facemap.get(resp.get(j).getFaceId());
				resp.get(j).setImageData(
						facemap.get(resp.get(j).getFaceId()).getImageData());

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("", e);
		} finally {
			entityManager.close();
		}

		return resp;

	}

	/*@Override
	public List<PoliceManDto> findPoliceMan() {

		List<PoliceManDto> resp = null;
		ArrayList<PoliceManDto> result = new ArrayList<PoliceManDto>();

		String sqlString = "select a.*,b.station_name FROM "
				+ GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_POLICEMAN_INFO + " a ,"
				+ GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_POLICE_STATION
				+ " b where a.station_id=b.id ";

		try {
			Query query = this.entityManager.createNativeQuery(sqlString,
					PoliceManDto.class);
			resp = (ArrayList<PoliceManDto>) query.getResultList();

		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}

		// 把结果集封装成前端要的对象返回
		for (int i = 0; i < resp.size(); i++) {

			PoliceManDto p = (PoliceManDto) resp.get(i);

			result.add(p);

		}

		return result;

	}
*/
	
	@Override
	public List<PoliceManDto> findPoliceMan(PoliceManDto policeManDto,int page,int pageSize) {

		List<PoliceManDto> resp = null;
		ArrayList<PoliceManDto> result = new ArrayList<PoliceManDto>();

		String sqlString = "select a.*,b.station_name FROM "
				+ GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_POLICEMAN_INFO + " a ,"
				+ GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_POLICE_STATION
				+ " b where a.station_id=b.id ";
	  /*  if(!searchString.equals("\"\"")&&searchString!=null&&!searchString.equals("{}")){	  
			  
			  sqlString +=  " and ( a.name like \"%"+searchString+"%\" or a.police_no like \"%"+searchString+"%\")"; 
			  
		  }*/
		//适用于 显示警员姓名的那个页面 的模糊查询
		if(policeManDto.getName()!=null){	  
			  
			  sqlString +=  " and ( a.name like \"%"+policeManDto.getName()+"%\")";
			  
			  if(policeManDto.getName().equals("男")){
					sqlString +=  " or ( a.sex = 0 )";
				}else if(policeManDto.getName().equals("女")) {
					sqlString +=  " or ( a.sex = 1 )";
				}
			  
		  }
		//适用于 显示警员号 警员姓名 等全部信息 要全局检索的模糊查询
		if(policeManDto.getPoliceNo()!=null){	  
			  
			  sqlString +=  " and ( a.name like \"%"+policeManDto.getPoliceNo()+"%\" or a.police_no like \"%"+policeManDto.getPoliceNo()+"%\" or b.station_name like \"%"+policeManDto.getPoliceNo()+"%\" or a.phone like \"%"+policeManDto.getPoliceNo()+"%\")";
			  
			  if(policeManDto.getPoliceNo().equals("男")){
					sqlString +=  " or ( a.sex = 0 )";
				}else if(policeManDto.getPoliceNo().equals("女")) {
					sqlString +=  " or ( a.sex = 1 )";
				}
			  
		  }
	    
		
		
	       policeManTotalNum=getTotalNum(sqlString);
		   sqlString += " order by id desc LIMIT " + (page - 1) * pageSize + "," + pageSize + "";  
		

		try {
			Query query = this.entityManager.createNativeQuery(sqlString,
					PoliceManDto.class);
			resp = (ArrayList<PoliceManDto>) query.getResultList();

		} catch (Exception e) {
			LOG.error("", e);
			if(resp == null){ return null ; }   
		} finally {
			entityManager.close();
		}

		// 把结果集封装成前端要的对象返回
		for (int i = 0; i < resp.size(); i++) {

			PoliceManDto p = (PoliceManDto) resp.get(i);

			result.add(p);

		}

		return result;

	}
	
	
	@Override
	public BigInteger findCountByUserId(String id) {

		ArrayList resp = null;
		BigInteger result = null;

		String sqlString = " select count(1) from "
				+ GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_PUSH_ALARM_INFO + " where receiver_no='"
				+ id + "' and checked=0; ";

		try {
			Query query = this.entityManager.createNativeQuery(sqlString);
			resp = (ArrayList) query.getResultList();

		} catch (Exception e) {
			LOG.error("", e);
			if(resp == null){ return null ; }
		} finally {
			entityManager.close();
		}

		if (resp.size() != 0) {
			result = (BigInteger) resp.get(0);

		}

		return result;
	}
	
	public BigInteger getTotalNum(String sql){
		
		BigInteger maxpage=null;
		String getmaxpagesql="";
		if(sql.indexOf("a.*")>0){
			getmaxpagesql=sql.replace("a.*,b.station_name","count(*)");
		}else{
			 getmaxpagesql=sql.replace("*,b.station_name","count(*)");
		}

		System.out.println("此处获取最大页数的sql语句是："+getmaxpagesql);
		ArrayList resp = null;
		
		try { 
			Query query = this.entityManager.createNativeQuery(getmaxpagesql);
			
			resp =(ArrayList) query.getResultList();
		
		} catch (Exception e) {
			LOG.error("", e);
			if(resp == null){ return null ; }   
		} finally {
			entityManager.close();
		}
		if(resp.size()!=0){
		maxpage=(BigInteger) resp.get(0);}
		
		return maxpage;
		
		
	}


}
