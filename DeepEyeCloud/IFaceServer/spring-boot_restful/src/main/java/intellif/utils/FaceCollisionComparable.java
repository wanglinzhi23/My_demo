package intellif.utils;

import intellif.facecollision.vo.FaceCollisionResult;
import intellif.database.entity.FaceInfo;

import java.util.Comparator;
import java.util.Date;

public class FaceCollisionComparable implements Comparator<FaceCollisionResult> {

    // 对象的排序方式[升、降]
    public static boolean sortASC = false;


    @Override  
    public int compare(FaceCollisionResult face1, FaceCollisionResult face2) {
        int result = 0;
        Integer mode1 = face1.getMode();
        Integer mode2 = face2.getMode();
        Long tCount1 = face1.getTargetCount();
        Long tCount2 = face2.getTargetCount();
        Long sCount1 = face1.getSourceCount();
        Long sCount2 = face2.getSourceCount();
        if(sortASC){
          result = mode1.compareTo(mode2);
          if(result == 0){
              result = tCount1.compareTo(tCount2);
              if(result == 0){
                  result = sCount1.compareTo(sCount2);
              }
          }
        }else{
          result = -mode1.compareTo(mode2);
          if(result == 0){
              result = -tCount1.compareTo(tCount2);
              if(result == 0){
                  result = -sCount1.compareTo(sCount2);
              }
          }
        }
        return result;
    }
}  
