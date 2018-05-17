package intellif.controllers;

import intellif.consts.GlobalConsts;
import intellif.dao.PersonInzonesDao;
import intellif.dto.JsonObject;
import intellif.service.FaceServiceItf;
import intellif.service.IoContrlServiceItf;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_PERSON_INZONES)
public class PersonInzonesController {

    private static Logger LOG = LogManager.getLogger(PersonInzonesController.class);
    
//    private static final int DEFAULT_BIG_PAGE_SIZE = 200;

    // ==============
    // PRIVATE FIELDS
    // ==============

    @Autowired
    private PersonInzonesDao _personInzonesDao;
    
    @Autowired
    private FaceServiceItf faceService;

    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;

//    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
//    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the rule info is successfully created or not.")
//    public JsonObject create(@RequestBody @Valid PersonInzones personInzones) {
//        return new JsonObject(_personInzonesRepository.save(personInzones));
//    }

//    @RequestMapping(method = RequestMethod.GET)
//    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of rule info that is successfully get or not.")
//    public JsonObject list() {
//        return new JsonObject(this._personInzonesRepository.findAll());
//    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the rule info id is successfully get or not.")
//    public JsonObject get(@PathVariable("id") long id) {
//        return new JsonObject(this._personInzonesRepository.findOne(id));
//    }
    
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "统计各区域人数")
    public JsonObject statisticByStation() {
    	try{
    		List<Object[]> s = this._personInzonesDao.countByStationId();
        	return new JsonObject(s);
    	}
    	catch(Exception e){
    		LOG.error("统计各区域人数出错："+e.getMessage());
    		return new JsonObject(e.getMessage(), 1001);
    	}
    }

    @RequestMapping(value = "/station/{id}/page/{page}/time/{time}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定区域的人员采集列表")
    public JsonObject getByStationId(@PathVariable("id") long id, @PathVariable("page") int page, @PathVariable("time") String time, @PathVariable("pagesize") int pagesize) {
        return new JsonObject(this.faceService.findPersonInzonesByStationId(id, page, pagesize, time));
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  rule info is successfully updated or not.")
//    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid PersonInzones personInzones) throws TException {
//        personInzones.setId(id);
//        PersonInzones personInzonesSaved = this._personInzonesRepository.save(personInzones);
//        //
//        ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_VERIFYRULE.getValue(), ERuleIoctrlType.RULE_IOCTRL_UPDATE.getValue(), id, 0);
//        //
//        return new JsonObject(personInzonesSaved);
//    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the rule info is successfully delete or not.")
//    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
//        this._personInzonesRepository.delete(id);
//        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
//    }

}
