package intellif.service;

import java.util.ArrayList;

import intellif.dto.SearchIPDto;
import intellif.database.entity.AllowIpRange;




public interface AllowipServiceItf {
    
	
	
	
	
	public ArrayList findByPage(int page,int pagesize,SearchIPDto searchDto);
	
	
    
  
}
