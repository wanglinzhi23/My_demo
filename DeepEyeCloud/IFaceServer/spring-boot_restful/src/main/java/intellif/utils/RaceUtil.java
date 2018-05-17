package intellif.utils;

import intellif.database.entity.FaceInfo;

import java.util.ArrayList;
import java.util.List;

public class RaceUtil {

	private static List<FaceInfo> raceList = new ArrayList<FaceInfo>();

	public static synchronized List<FaceInfo> getRaceList() {
		return raceList;
	}

	public static synchronized void setRaceList(List<FaceInfo> raceList) {
		RaceUtil.raceList = raceList;
	}
	public static synchronized void appendRaceList(List<FaceInfo> raceList){
		RaceUtil.raceList.addAll(raceList);
	}
	public static synchronized List<FaceInfo> getRaceListByPage(int start, int end ){
		if(raceList.size() < start + end){
			return raceList.subList(start, raceList.size());
		}
		return raceList.subList(start, start+end);
	}
	
	public static synchronized List<FaceInfo> getCameraRaceListByPage(int start, int end, long sourceId){
		List<FaceInfo> resultList = new ArrayList<FaceInfo>();
		
		for(FaceInfo item: raceList){
			long sId = item.getSourceId();
			if(sId == sourceId){
				resultList.add(item);
			}
		}
		if(resultList.size() < start + end){
			return raceList.subList(start, resultList.size());
		}
		return resultList.subList(start, start+end);
	}
}
