/**
 *
 */
package intellif.controllers;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dao.MaintenanceDao;
import intellif.dao.PoliceStationDao;
import intellif.database.entity.PoliceStation;
import intellif.dto.JsonObject;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ERuleIoctrlType;
import intellif.database.entity.LossPrePerson;
import intellif.database.entity.MaintenancePerson;
import intellif.database.entity.RuleInfo;

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
@RequestMapping(GlobalConsts.R_ID_MAINTENANCE)
public class MaintenanceController {

	 private static Logger LOG = LogManager.getLogger(MaintenanceController.class);
    @Autowired
    private MaintenanceDao _maintenanceDao;
    @Autowired
    private PoliceStationDao _policeStationDao;
   

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the maintenance person info is successfully created or not.")
	public JsonObject create(@RequestBody @Valid MaintenancePerson mPerson) {
		List<MaintenancePerson> list = _maintenanceDao.findByWeixinId(mPerson
				.getWeixinId());
		if (null != list && !list.isEmpty()) {
			return new JsonObject("微信用户已经存在！", 1001);
		} else {
			Iterable<PoliceStation> pList = this._policeStationDao.findAll();
			StringBuffer station_ids = new StringBuffer();
			for (PoliceStation ps : pList) {
				long id = ps.getId();
				station_ids.append(",");
				station_ids.append(id);
			}
			mPerson.setStationIds(station_ids.toString());
			return new JsonObject(_maintenanceDao.save(mPerson));
		}
	}

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of maintenance persons info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this._maintenanceDao.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  maintenance info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid MaintenancePerson mPerson) throws Exception {
    	mPerson.setId(id);
    	MaintenancePerson mp = this._maintenanceDao.save(mPerson);
        return new JsonObject(mp);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the maintenance info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this._maintenanceDao.findOne(id));
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the maintenance info id is successfully delete or not.")
	public JsonObject delete(@PathVariable("id") long id) {
		try {
			this._maintenanceDao.delete(id);
			return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
					HttpStatus.OK));
		} catch (Exception e) {
			LOG.error("运维人员已经被删除，id:" + id, e);
			return new JsonObject("运维人员已经被删除", 1001);
		}
		
	}
}
