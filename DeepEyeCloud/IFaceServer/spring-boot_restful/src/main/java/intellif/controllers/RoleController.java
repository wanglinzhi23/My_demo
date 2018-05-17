package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.RoleDao;
import intellif.database.entity.RoleInfo;
import intellif.dto.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

/**
 * <h1>The Class RoleController.</h1>
 * The RoleController which serves request of the form /role and returns a JSON object representing an instance of RoleInfo.
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
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-09-18
 */
@RestController
//@RequestMapping("/intellif/role")
@RequestMapping(GlobalConsts.R_ID_ROLE)
public class RoleController {

    // ==============
    // PRIVATE FIELDS
    // ==============
    private static Logger LOG = LogManager.getLogger(RoleController.class);
    // Autowire an object of type userInfoDao
    @Autowired
    private RoleDao roleRepository;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the roleInfo info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid RoleInfo roleInfo) {
        return new JsonObject(roleRepository.save(roleInfo));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of role info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this.roleRepository.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the role info is successfully get or not.")
    public RoleInfo get(@PathVariable("id") long id) {
        return this.roleRepository.findOne(id);
    }

//    @RequestMapping(value = "/{id}/scopes", method = RequestMethod.GET)
//    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the role scope info is successfully get or not.")
//    public RoleInfo getScopes(@PathVariable("id") int id) {
//        return this.roleRepository.findOne(id);
//    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  roleInfo info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid RoleInfo roleInfo) {
//		userInfo find = this._userInfoDao.findOne(id);
        roleInfo.setId(id);
        return new JsonObject(this.roleRepository.save(roleInfo));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the role info is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
        this.roleRepository.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

}
