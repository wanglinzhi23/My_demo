package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import intellif.consts.GlobalConsts;
import intellif.dao.PersonInfoDao;
import intellif.dto.JsonObject;
import intellif.database.entity.PersonInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

/**
 * <h1>The Class PersonInfoController.</h1>
 * The PersonInfoController which serves request of the form /person/info and returns a JSON object representing an instance of PersonInfo.
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
 * @since 2015-03-31
 */
@RestController
//@RequestMapping("/intellif/person/info")
@RequestMapping(GlobalConsts.R_ID_PERSON_INFO)
public class PersonInfoController {
    // ==============
    // PRIVATE FIELDS
    // ==============

    // Auto wire an object of type PersonInfoDao
    @Autowired
    private PersonInfoDao _personInfoDao;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the person info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid PersonInfo personInfo) {
        return new JsonObject(_personInfoDao.save(personInfo));
    }

/*    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of person info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this._personInfoDao.findAll());
    }*/

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the person info id is successfully get or not.")
    public PersonInfo get(@PathVariable("id") long id) {
        return this._personInfoDao.findOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  person info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid PersonInfo personInfo) {
        PersonInfo find = this._personInfoDao.findOne(id);
        personInfo.setId(id);
        personInfo.setCreated(find.getCreated());
        return new JsonObject(this._personInfoDao.save(personInfo));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the person info is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
        this._personInfoDao.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

}
