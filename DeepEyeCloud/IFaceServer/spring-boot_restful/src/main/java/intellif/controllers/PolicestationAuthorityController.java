package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import intellif.consts.GlobalConsts;
import intellif.dao.PoliceStationAuthorityDao;
import intellif.dao.PoliceStationDao;
import intellif.dto.AuthorityListDto;
import intellif.dto.JsonObject;
import intellif.dto.PoliceStationDto;
import intellif.service.PoliceStationServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.Pageable;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.UserInfo;
import net.sf.json.JSON;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(GlobalConsts.R_ID_POLICE_STATION_AUTHORITY)
public class PolicestationAuthorityController {

    // ==============
    // PRIVATE FIELDS
    // ==============
    private static Logger LOG = LogManager.getLogger(PolicestationAuthorityController.class);
    @Autowired
    private PoliceStationAuthorityDao policeStationAuthorityRepository;
    @Autowired
    private PoliceStationServiceItf policeStationServiceItf;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PoliceStationServiceItf policeStationService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "新增单位权限")
    public JsonObject create(@RequestBody @Valid PoliceStationAuthority policeStationAuthority) {
    	String authority = ","+_userService.getAuthorityIds(GlobalConsts.CONTROL_AUTORITY_TYPE)+",";
    	if(authority.indexOf(","+policeStationAuthority.getBankId()+",")<0||policeStationAuthority.getType()>=2||policeStationAuthority.getType()<0) return new JsonObject("对不起，您没有操作权限！", 1001);
    	List<PoliceStationAuthority> policeStationAuthorityList = policeStationAuthorityRepository.findByStationIdAndBankId(policeStationAuthority.getStationId(), policeStationAuthority.getBankId());
    	if(policeStationAuthorityList.size()>0) return new JsonObject("该库已被分配权限给该单位！", 1001);
        return new JsonObject(policeStationAuthorityRepository.save(policeStationAuthority));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询所有单位的所有权限")
    public JsonObject list() {
        return new JsonObject(this.policeStationAuthorityRepository.findAll());
    }
    
    @RequestMapping(value = "/blacktype/{type}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据库类型查询所有单位的所有权限")
    public JsonObject list(@PathVariable("type") int type) {
    	if(type==2){
    		 return new JsonObject(this.policeStationAuthorityRepository.findAll());
    	}else{
    		return new JsonObject(this.policeStationAuthorityRepository.findByBankType(type));
    	}
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询指定ID的单位权限")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this.policeStationAuthorityRepository.findOne(id));
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    @ApiOperation(httpMethod = "PUT", value = "指定ID的单位权限")
//    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid PoliceStationAuthority policeStationAuthority) {
//    	policeStationAuthority.setId(id);
//        return new JsonObject(this.policeStationAuthorityRepository.save(policeStationAuthority));
//    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "删除指定ID的单位权限")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
    	PoliceStationAuthority pa = policeStationAuthorityRepository.findOne(id);
        this.policeStationAuthorityRepository.delete(id);
        LOG.info(((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLogin()+" delete BankId:" +pa.getBankId()+" StationId:"+pa.getStationId()+" Type:"+pa.getType());
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @RequestMapping(value = "/station/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询指定单位的所有权限")
    public JsonObject findByStationId(@PathVariable("id") long id) {
        List<PoliceStationAuthority> policeStationAuthorityList = this.policeStationAuthorityRepository.findByStationId(id);
        return new JsonObject(policeStationAuthorityList);
    }
    @RequestMapping(value = "/station/{id}/blacktype/{type}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询指定单位的所有权限")
    public JsonObject findByStationIdAndBlackType(@PathVariable("id") long id,@PathVariable("type") int type) {
        List<PoliceStationAuthority> policeStationAuthorityList = this.policeStationAuthorityRepository.findByBankTypeAndStationId(type, id);
        return new JsonObject(policeStationAuthorityList);
    }
    
    @RequestMapping(value = "/batch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "批量新增单位权限")
    public JsonObject createByBatch(@RequestBody @Valid AuthorityListDto authorityListDto) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        long bankId = authorityListDto.getBankId();
        int type = authorityListDto.getType();
        try {
            List<PoliceStationAuthority> policeStationAuthorityList = policeStationAuthorityRepository.findByBankId(bankId);
            List<Long> receivedStationIdList = authorityListDto.getpStationList();
            StringBuilder updateStationId = new StringBuilder("");
            StringBuilder saveStationId = new StringBuilder("");
            StringBuilder deleteStationId = new StringBuilder("");
            long userStationId = CurUserInfoUtil.getUserInfo().getPoliceStationId();
            List<PoliceStation> forefathers = policeStationService.getForefathers(userStationId);
            List<Long> ignoreList = new ArrayList<Long>();
            ignoreList.add(userStationId);
            for (PoliceStation policeStation : forefathers) {
                ignoreList.add(policeStation.getId());
            }
            for (PoliceStationAuthority policeStationAuthority : policeStationAuthorityList) {
                long stationId = policeStationAuthority.getStationId();
                if (!ignoreList.contains(stationId)) {
                    if (receivedStationIdList.contains(stationId)) {
                        if (policeStationAuthority.getType() != type) {
                            updateStationId.append("," + stationId);
                        }
                        receivedStationIdList.remove(stationId);
                    } else {
                        if (policeStationAuthority.getType() == type) {
                            deleteStationId.append("," + stationId);
                        }
                    }
                }
            }
            String before = "),(" + bankId + ", " + type + ", '" + time + "', '" + time + "',";
            for (long stationId : receivedStationIdList) {
                if (!ignoreList.contains(stationId)) {
                saveStationId.append(before + stationId);
                }
            }
            if (!"".equals(updateStationId.toString())) {
                String updateSQL = "UPDATE " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY 
                        + " SET updated = '" + time + "', type = " + type + " WHERE bank_id = " + bankId + " and station_id IN(" + updateStationId.substring(1) + ")";
                jdbcTemplate.update(updateSQL);
            }
            if (!"".equals(saveStationId.toString())  ) {
                saveStationId.append(")");
                String saveSQL = "INSERT INTO " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY 
                        + " (bank_id, type, created, updated, station_id) VALUES " + saveStationId.substring(2);
                jdbcTemplate.update(saveSQL);
            }
            if (!"".equals(deleteStationId.toString())) {
                String deleteSQL = "DELETE FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY 
                        + " WHERE bank_id = " + bankId + " AND station_id IN(" + deleteStationId.substring(1) + ")";
                jdbcTemplate.update(deleteSQL);
            }
        }catch (Exception e) {
            LOG.error("", e);
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }
}
