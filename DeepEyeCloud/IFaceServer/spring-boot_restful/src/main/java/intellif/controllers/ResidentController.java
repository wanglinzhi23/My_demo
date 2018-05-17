
package intellif.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import intellif.consts.GlobalConsts;
import intellif.dao.AuditLogDao;
import intellif.dao.ResidentAreaDao;
import intellif.dto.JsonObject;
import intellif.service.ResidentServiceItf;
import intellif.utils.CommonUtil;
import intellif.database.entity.ResidentInfo;
import intellif.database.entity.ResidentPerson;
import intellif.database.entity.ResidentResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_RESIDENT)
public class ResidentController {
    //
    private static Logger LOG = LogManager.getLogger(ResidentController.class);
    private static final int DEFAULT_PAGE_SIZE = 40;
    @Autowired
    private ResidentServiceItf residentService;
    @Autowired
    private ResidentAreaDao residentAreaRepository;

    @RequestMapping(value = "/summary/date/{date}/id/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the police station info id is successfully get or not.")
    public JsonObject getResidentInfoByAreaId(@PathVariable("id") long id, @PathVariable("date") String date) {
    	try{
    		List<ResidentInfo> infoList = new ArrayList<ResidentInfo>();
    		String baseDate = date;
    		for(int i=0; i<30; i++){
    			String dateStr = getDatesByDay(baseDate);
        		String[] ds = dateStr.split("#");
        		ResidentInfo ri = residentService.getResidentInfo(id,ds[0], ds[1]);
        		if(null != ri){
        			infoList.add(ri);
        		}
        		baseDate = getPerDay(baseDate);
    		}
    	
    		return new JsonObject(infoList);
    		
    	}catch(Exception e){
    		LOG.error("获取常住人口出错",e);
    		return new JsonObject(e.getMessage(), 1001);
    	}
        
    }
    
    @RequestMapping(value = "/details/date/{date}/id/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the police station info id is successfully get or not.")
    public JsonObject getResidentPersonByAreaId(@PathVariable("id") long id,@PathVariable("page") int page,@PathVariable("pagesize") int pagesize, @PathVariable("date") String date) {
    	ResidentResult result = new ResidentResult();
    	try{
    		String dateStr = getDatesByDay(date);
    		String[] ds = dateStr.split("#");
    		List<ResidentPerson> ri = residentService.getResidentPersonByAreaId(id,ds[0], ds[1],(page - 1) * pagesize, pagesize);
    		List<ResidentPerson> ri1 = residentService.getResidentIndexsByAreaId(id,ds[0], ds[1]);
    		if(null != ri1){
    		Map<String,Integer> indexMap = CommonUtil.processResidentPerson(ri1);
    		result.setResidentMap(indexMap);
    		}
    		result.setResidentList(ri);
    		return new JsonObject(result);
    		
    	}catch(Exception e){
    		LOG.error("获取常住人口列表信息出错",e);
    		return new JsonObject(e.getMessage(), 1001);
    	}
        
    }
    @RequestMapping(value = "/area", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response all resident area informations")
    public JsonObject getAllResidentByAreas() {
    	  return new JsonObject(this.residentAreaRepository.findAll());
    }
    
    private String getDatesByMonth(String dateStr) throws Exception{
    	try{
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
    	 Calendar cal=Calendar.getInstance();
		 Date date = sdf.parse(dateStr);
		 cal.setTime(date);
		 int maxDay=cal.getActualMaximum(Calendar.DAY_OF_MONTH);    
		 String dateStr1 = dateStr+"-"+maxDay;
		 System.out.println(dateStr1);
		  sdf = new SimpleDateFormat("yyyy-MM-dd");  
		  Date date1 = sdf.parse(dateStr1);
		long time = date1.getTime()/1000;
		long time1 = time +24*60*60;
		Date tt = new Date(time*1000);
		Date tt1 = new Date(time1*1000);
		System.out.println(sdf.format(tt));
		System.out.println(sdf.format(tt1));
		return sdf.format(tt)+"#"+sdf.format(tt1);
    	}catch(Exception e){
    		LOG.error("时间解析出错：",e);
    		throw new Exception("时间格式错误");
    	}
	
    }
    private String getDatesByDay(String dateStr) throws Exception{
    	try{
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		  Date date = sdf.parse(dateStr);
		long time = date.getTime()/1000;
		long time1 = time +24*60*60;
		Date tt = new Date(time*1000);
		Date tt1 = new Date(time1*1000);
		System.out.println(sdf.format(tt));
		System.out.println(sdf.format(tt1));
		return sdf.format(tt)+"#"+sdf.format(tt1);
    	}catch(Exception e){
    		LOG.error("时间解析出错：",e);
    		throw new Exception("时间格式错误");
    	}
	
    }
    /**
     * 往后推一天
     * @param dateStr
     * @return
     * @throws Exception
     */
    private String getPerDay(String dateStr) throws Exception{
    	
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		  Date date = sdf.parse(dateStr);
		long time = date.getTime()/1000;
		long time1 = time - 24*60*60;
		Date tt1 = new Date(time1*1000);
	    return sdf.format(tt1);
	   
    }
}
