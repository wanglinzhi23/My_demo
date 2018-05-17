package intellif.utils;

import intellif.dto.FaceResultDto;

import java.util.Date;
import java.util.Comparator;

public class FaceResultDtoComparable implements Comparator<FaceResultDto> {

	// 对象的排序方式[升、降]
	public static boolean sortASC = false;

	// 对象的排序属性
	public  boolean sortByScore = true;
	public  boolean sortByTime = false;
	public  boolean sortById = false;

	public FaceResultDtoComparable(String type) {
		super();
		if("score".equals(type)) {
			sortByScore = true;
			sortByTime = false;
			sortById = false;
		} else if("time".equals(type)) {
			sortByScore = false;
			sortByTime = true;
			sortById = false;
		} else if("id".equals(type)) {
			sortByScore = false;
			sortByTime = false;
			sortById = true;
		}
	}

	@Override  
	public int compare(FaceResultDto face1, FaceResultDto face2) {
		int result = 0;
		if(sortASC){
			if(sortByScore){
				Float face1Score = face1.getScore();
				Float face2Score = face2.getScore();
				result = face1Score.compareTo(face2Score);
			}else if(sortByTime){
				Date face1Time = face1.getTime();
				Date face2Time = face2.getTime();
				result = face1Time.compareTo(face2Time);
			}else if(sortById){
				Long face1Id = Long.valueOf(face1.getId().substring(face1.getId().indexOf("_")+1));
				Long face2Id = Long.valueOf(face2.getId().substring(face2.getId().indexOf("_")+1));
				result = face1Id.compareTo(face2Id);
			}
		}else{
			if(sortByScore){
				Float face1Score = face1.getScore();
				Float face2Score = face2.getScore();
				result = -face1Score.compareTo(face2Score);
			}else if(sortByTime){
				Date face1Time = face1.getTime();
				Date face2Time = face2.getTime();
				result = -face1Time.compareTo(face2Time);
			}else if(sortById){
				Long face1Id = Long.valueOf(face1.getId().substring(face1.getId().indexOf("_")+1));
				Long face2Id = Long.valueOf(face2.getId().substring(face2.getId().indexOf("_")+1));
				result = -face1Id.compareTo(face2Id);
			}
		}
		return result;
	}
}  
