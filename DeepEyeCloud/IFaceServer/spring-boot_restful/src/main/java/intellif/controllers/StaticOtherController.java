package intellif.controllers;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dao.PoliceCaseDao;
import intellif.dto.JsonObject;
import intellif.database.entity.PoliceCase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_STATIC_OTHER)
public class StaticOtherController {

    private static Logger LOG = LogManager.getLogger(StaticOtherController.class);

    @Autowired
    private PoliceCaseDao policeCaseDao;
 
    @RequestMapping(value = "/policecase", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取所有案件类型")
    public JsonObject getAllPoliceCase() {
        List<PoliceCase> pList = null;
        try{
            pList = policeCaseDao.findAll();
        }catch(Exception e){
            LOG.error("get all police case error:",e);
        }
        return new JsonObject(pList);
    }

}
