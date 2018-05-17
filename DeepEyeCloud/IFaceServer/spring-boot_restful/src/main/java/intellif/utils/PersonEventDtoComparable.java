package intellif.utils;

import intellif.dto.EventDto;
import intellif.dto.FaceResultDto;

import java.util.Date;
import java.util.Comparator;

public class PersonEventDtoComparable implements Comparator<EventDto> {

    // 对象的排序方式[升、降]
    public static boolean sortASC = false;

    public PersonEventDtoComparable(String type) {
        super();
        if("asc".equals(type)) {
            sortASC = true;
        } else if("desc".equals(type)) {
           sortASC = false;
        } 
    }

    @Override  
    public int compare(EventDto dto1, EventDto dto2) {
        int result = 0;
        Integer index1 = dto1.getIndex();
        Integer index2 = dto2.getIndex();
        if(sortASC){
            result = index1.compareTo(index2);
        }else{
            result = index2.compareTo(index1);
        }
        return result;
    }
}  
