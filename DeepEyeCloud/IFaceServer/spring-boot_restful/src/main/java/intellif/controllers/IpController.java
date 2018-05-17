package intellif.controllers;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.audit.AuditServiceImpl;
import intellif.consts.GlobalConsts;
import intellif.dao.AllowIpRangeDao;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.database.entity.UserInfo;
import intellif.dto.HistoryOperationDto;
import intellif.dto.JsonObject;
import intellif.dto.SearchIPDto;
import intellif.dto.SearchUserDto;
import intellif.service.AllowipServiceItf;
import intellif.service.impl.AllowipServiceImpl;
import intellif.utils.IpToNumbers;
import intellif.database.entity.AllowIpRange;
import intellif.database.entity.ServerInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;


@RestController
//@RequestMapping("/intellif/server")
@RequestMapping(GlobalConsts.R_ID_ALLOW_IPS)
public class IpController {

   
	
	 
	 @Autowired
	    private AllowIpRangeDao _allowiprangeDao;
	 @Autowired
	    private UserDao userRepository;
	 @Autowired
	    private RoleDao roleRepository;
	 @Autowired
	    private AllowipServiceItf _allowipService;
	 
	
	 // SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
	 
	//改为分页获取  所以不直接调用findall了  
	 
	@RequestMapping(value = "/page/{page}/pagesize/{pagesize}",method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "Response a list describing all of allowips that is successfully get or not.")
	public JsonObject list(@RequestBody SearchIPDto searchIPDto,@PathVariable("page") int page,@PathVariable("pagesize") int pagesize) {
		
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	 Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
    	 UserInfo userinfo= userRepository.findOne(userid);
    	 String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
    		if(roleName.equals("SUPER_ADMIN")||roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
    	 
	          // return new JsonObject(this._allowiprangeDao.findAll());
    			    ArrayList<AllowIpRange> ips = this._allowipService.findByPage(page,pagesize,searchIPDto);
    		   	    BigInteger biginteger=AllowipServiceImpl.hisopmaxpage;
    		        int maxpage=0;
    		        if(biginteger!=null){
    		        if(((biginteger.intValue())%pagesize)==0){
    		        maxpage=(biginteger.intValue())/pagesize;
    		        }else{
    		        maxpage=(biginteger.intValue())/pagesize+1;
    		        }
    		        }
    		        System.out.println("ggggggggggggggggggggggggggggggg"+biginteger+" nnnnnn "+maxpage+"  "+pagesize);
    		        return new JsonObject(ips,0,maxpage);

    		}else{
    			
    			return new JsonObject("对不起，您没有修改权限！", 1001);
    		}
		
	    }
	
	
	    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the allowips is successfully get or not.")
	    public AllowIpRange get(@PathVariable("id") long id) {
		
		 return this._allowiprangeDao.findOne(id);
	    }
	
   
   /*//  新增的IP段范围不能与已有的IP段有重复。  
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the allowips is successfully created or not.")
    public JsonObject create(@RequestBody @Valid AllowIpRange allowiprange) {
    
    	System.out.println("111111111");
    	
    //Boolean flag=this._allowipService.ipexists(allowiprange);
    	List<AllowIpRange> iflist=this._allowiprangeDao.ipexists(allowiprange.getStartIp(),allowiprange.getEndIp());	
   
    if(iflist.size()==0){
    	
    	//System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+_allowiprangeDao.save(allowiprange.getStartIp(),allowiprange.getEndIp()).getClass());
    	//_allowiprangeDao.save(allowiprange.getStartIp(),allowiprange.getEndIp());
      //  return new JsonObject("success");
    	return new JsonObject(_allowipService.save(allowiprange));
        
    }else{
    	
    	 return new JsonObject("ip段和已有的重复，新增失败");
    	
    }
    
    
    }*/

   
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the allowips is successfully created or not.")
    public JsonObject create(@RequestBody @Valid AllowIpRange allowiprange) {
    
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	 Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
    	 UserInfo userinfo= userRepository.findOne(userid);
    	 allowiprange.setUser(userinfo.getName());
    	 String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
    		if(roleName.equals("SUPER_ADMIN")||roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
		
    	List<AllowIpRange> iflist=this._allowiprangeDao.ipexists(allowiprange.getStartIp(),allowiprange.getEndIp());
   
    if(iflist.size()==0){
    	
    	   
    	     allowiprange.setStartIpNumber(IpToNumbers.ipToLong(allowiprange.getStartIp()));
		    allowiprange.setEndIpNumber(IpToNumbers.ipToLong(allowiprange.getEndIp()));
    	
    	return new JsonObject(_allowiprangeDao.save(allowiprange));
        
    }else{
    	
    	return new JsonObject("ip段和已有的重复，新增失败", 1001);
    	
    }}else{
    	
    	return new JsonObject("对不起，您没有修改权限！", 1001);
    }
    		
    
    
    }
	
	

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the allowips is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
    	

		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
   	 Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
   	 UserInfo userinfo= userRepository.findOne(userid);
   	 String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
   		if(roleName.equals("SUPER_ADMIN")||roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
    	
        this._allowiprangeDao.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
   		}else{
   			
   			return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.NOT_ACCEPTABLE);
   		}
    }
    
/*  // 新增的IP段范围不能与已有的IP段有重复。
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  allowips is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid AllowIpRange allowiprange) {

    	
    	 // Boolean flag=this._allowipService.ipexists(allowiprange);
    	List<AllowIpRange> iflist=this._allowiprangeDao.ipexists(allowiprange.getStartIp(),allowiprange.getEndIp());
    	
 
    	 
    	  if(iflist.size()==0){
    		  
    		    allowiprange.setId(id);
    		  //  _allowiprangeDao.update(allowiprange.getStartIp(),allowiprange.getEndIp(),allowiprange.getId());
               // return new JsonObject("success");
    		    return new JsonObject(_allowipService.update(allowiprange));
    	        
    	    }else{
    	    	
    	    	 return new JsonObject("ip段和已有的重复，新增失败");
    	    	
    	    }
    	
    }
    */
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  allowips is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid AllowIpRange allowiprange) {

    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	 Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
    	 UserInfo userinfo= userRepository.findOne(userid);
    	 String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
    		if(roleName.equals("SUPER_ADMIN")||roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
    	
    	List<AllowIpRange> iflist=this._allowiprangeDao.ipexists(allowiprange.getStartIp(),allowiprange.getEndIp());
    	
    	  if(iflist.size()==0){
    		  
    		    allowiprange.setId(id);
    		    allowiprange.setStartIpNumber(IpToNumbers.ipToLong(allowiprange.getStartIp()));
    		    allowiprange.setEndIpNumber(IpToNumbers.ipToLong(allowiprange.getEndIp()));
    		
    		    return new JsonObject(_allowiprangeDao.save(allowiprange));
    	        
    	    }else if(iflist.size()==1){
    	    	
    	    	AllowIpRange air=iflist.get(0);
    	    	AllowIpRange ipchoosed=this._allowiprangeDao.findOne(id);
 	
    	    	if((air.getEndIp().equals(ipchoosed.getEndIp()))&&(air.getStartIp().equals(ipchoosed.getStartIp()))){
    	    		
    	    		    allowiprange.setId(id);
    	    		    allowiprange.setStartIpNumber(IpToNumbers.ipToLong(allowiprange.getStartIp()));
    	    		    allowiprange.setEndIpNumber(IpToNumbers.ipToLong(allowiprange.getEndIp()));
    	    		
    	    		    return new JsonObject(_allowiprangeDao.save(allowiprange));
    	    	}else{
    	    		
    	    		return new JsonObject("要更新的ip段和已有的重复，更新失败",1001);
    	    		
    	    	}
    	   
    	    	
    	    }else{
    	    	
    	    	return new JsonObject("要更新的ip段和已有的重复，更新失败",1001);
    	    	
    	    } }else{
    	    	
    	    	return new JsonObject("对不起，您没有修改权限！", 1001);
    	    	
    	    }
    	
    }
    
    
   
    
    
}
