/**
 *
 */
package intellif.controllers;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dao.LossPreDao;
import intellif.dto.JsonObject;
import intellif.database.entity.LossPrePerson;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class ImageController.</h1>
 * The ImageController which serves request of the form /image and returns a JSON object representing an instance of ImageInfo.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc.
 * (see <a href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and static data storages),
 * while REST is a very-high-level API style (mostly for webservices and other 'live' systems)..
 *
 * @author shixiaohua
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
//@RequestMapping("/intellif/image")
@RequestMapping(GlobalConsts.R_ID_LOSS_PRE)
public class LossPreController {

	 private static Logger LOG = LogManager.getLogger(LossPreController.class);
    @Autowired
    private LossPreDao _lossPreDao;
   

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the loss person info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid LossPrePerson lPerson) {
    	List<LossPrePerson> list = _lossPreDao.findByWeixinIdAndStationId(lPerson.getStationId(), lPerson.getWeixinId());
    	
    	if(null != list && !list.isEmpty()){
    		 return new JsonObject("微信用户已经存在！",1001);
    	}else{
    		return new JsonObject(_lossPreDao.save(lPerson));
    	}
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of loss persons info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this._lossPreDao.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  loss person info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid LossPrePerson lPerson) throws Exception {
    	lPerson.setId(id);
    	LossPrePerson mp = this._lossPreDao.save(lPerson);
        return new JsonObject(mp);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the loss person info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this._lossPreDao.findOne(id));
    }
    
    @RequestMapping(value = "/station/{stationId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the loss person info id is successfully get or not.")
    public JsonObject getByStationId(@PathVariable("stationId") long stationId) {
        return new JsonObject(this._lossPreDao.findByStationId(stationId));
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the loss person info id is successfully delete or not.")
	public JsonObject delete(@PathVariable("id") long id) {
		try {
			this._lossPreDao.delete(id);
			return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
					HttpStatus.OK));
		} catch (Exception e) {
			LOG.error("防损人员已经被删除，id:" + id, e);
			return new JsonObject("防损人员已经被删除", 1001);
		}
		
	}
}
