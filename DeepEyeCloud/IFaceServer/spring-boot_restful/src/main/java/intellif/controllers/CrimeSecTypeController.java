package intellif.controllers;

import java.util.List;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.CrimeSecTypeDao;
import intellif.dao.PersonDetailDao;
import intellif.database.entity.PersonDetail;
import intellif.dto.JsonObject;
import intellif.database.entity.CrimeSecType;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

/**
 * <h1>The Class CrimeSecTypeController.</h1>
 * The CrimeSecTypeController which serves request of the form /crime/sectype and returns a JSON object representing an instance of CrimeSecType.
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
 * @author <a href="mailto:peng.cheng@intellif.com">PengCheng</a>
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_CRIME_SEC_TYPE)
public class CrimeSecTypeController {

    /**
     * Autowired repository of CrimeSecFriType
     */
    @Autowired

    private CrimeSecTypeDao crimeSecTypeRepository;
    /**
     * Autowired repository of PersonDetail
     */
    @Autowired

    private PersonDetailDao personDetailDao;

    /**
     * Creating a crimeSecType
     *
     * @param crimeSecType
     * @return Response a string describing if the crime type is successfully created or not.
     * @throws TException
     */
//    @PreAuthorize(GlobalConsts.OAUTH_C_H_R_SUPER_ADMIN)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the crime type is successfully created or not.")
    public JsonObject createCrimeType(@RequestBody @Valid CrimeSecType crimeSecType) throws Exception {
        if (crimeSecTypeRepository.findSame(crimeSecType.getName()).size() > 0)
            return new JsonObject("犯罪类型名称已存在，添加失败！", 1001);
        return new JsonObject(crimeSecTypeRepository.save(crimeSecType));
    }

    /**
     * Reading list of crimeSecType
     *
     * @return Response a list describing all of crime types that is successfully get or not.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of crime types that is successfully get or not.")

    public JsonObject list() {
        return new JsonObject(this.crimeSecTypeRepository.findAll());
    }

    /**
     * Reading a crimeSecType
     *
     * @param id id of crimeSecType
     * @return Response a string describing if the crime type id is successfully get or not.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the crime type id is successfully get or not.")

    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this.crimeSecTypeRepository.findOne(id));
    }

    /**
     * Querying a crimeSecType by crimeFirType id
     *
     * @param id id of crimeFriType
     * @return Response a string describing if the crime type id is successfully get or not.
     */
    @RequestMapping(value = "/fri/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the crime type id is successfully get or not.")
    public JsonObject getByFriId(@PathVariable("id") long id) {
        return new JsonObject(this.crimeSecTypeRepository.findByFriId(id));
    }

    /**
     * Querying a crimeSecType name
     *
     * @param name name of crimeSecType
     * @return Response a string describing if the crime type id is successfully get or not.
     */
    @RequestMapping(value = "/query/{name}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the crime type id is successfully get or not.")

    public JsonObject queryByName(@PathVariable("name") String name) {
        return new JsonObject(this.crimeSecTypeRepository.queryByText(name));
    }

    /**
     * Updating a crimeSecType
     *
     * @param id           id of updating crimeSecType
     * @param crimeSecType updating content
     * @return Response a string describing if the crime type is successfully updated or not.
     * @throws TException
     */
//    @PreAuthorize(GlobalConsts.OAUTH_C_H_R_SUPER_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the crime type is successfully updated or not.")

    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid CrimeSecType crimeSecType) throws Exception {
        crimeSecType.setId(id);
        if (crimeSecTypeRepository.findSame(crimeSecType.getName()).size() > 0)
            return new JsonObject("犯罪类型名称已存在，修改失败！", 1001);
        return new JsonObject(this.crimeSecTypeRepository.save(crimeSecType));
    }

    /**
     * Deleting a crimeSecType
     *
     * @param id id of updating crimeSecType
     * @return Response a string describing if the crime type is successfully delete or not.
     */
//    @PreAuthorize(GlobalConsts.OAUTH_C_H_R_SUPER_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the crime type is successfully delete or not.")

    public JsonObject delete(@PathVariable("id") long id) {
        List<PersonDetail> personList = this.personDetailDao.findByCrimeType(id);
        if (null != personList && personList.size() > 0) {
            return new JsonObject("该犯罪类型有嫌疑人在使用，删除失败！", 1001);
        }
        this.crimeSecTypeRepository.delete(id);
        return new JsonObject(true);
    }
}
