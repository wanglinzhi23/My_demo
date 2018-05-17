package intellif.utils;

import intellif.database.entity.FaceInfo;

import java.util.Comparator;
import java.util.Date;

public class FaceInfoComparable implements Comparator<FaceInfo> {

    // 对象的排序方式[升、降]
    public static boolean sortASC = false;

    // 对象的排序属性
    public  boolean sortByScore = true;
    public  boolean sortByTime = false;

    public FaceInfoComparable(String type) {
        super();
        if("score".equals(type)) {
            sortByScore = true;
            sortByTime = false;
        } else if("time".equals(type)) {
            sortByScore = false;
            sortByTime = true;
        }
    }

    @Override  
    public int compare(FaceInfo face1, FaceInfo face2) {
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
            }
        }
        return result;
    }
}  
