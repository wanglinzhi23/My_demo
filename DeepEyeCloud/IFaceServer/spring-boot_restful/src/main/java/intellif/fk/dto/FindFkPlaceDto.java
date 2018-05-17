package intellif.fk.dto;

import java.io.Serializable;


public class FindFkPlaceDto implements Serializable{

    private static final long serialVersionUID = -1588902803798110245L;

    //场所名称或者编号
    private String placeNameOrNo;

    public String getPlaceNameOrNo() {
        return placeNameOrNo;
    }

    public void setPlaceNameOrNo(String placeNameOrNo) {
        this.placeNameOrNo = placeNameOrNo;
    }
  
    

 

}
