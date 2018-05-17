package intellif.controllers;

import java.util.List;

import com.wordnik.swagger.annotations.ApiOperation;
import intellif.consts.GlobalConsts;
import intellif.dao.CrimeFriTypeDao;
import intellif.dao.CrimeSecTypeDao;
import intellif.dto.JsonObject;
import intellif.database.entity.CrimeFriType;
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
 * <h1>The Class CrimeFriTypeController.</h1>
 * The CrimeFriTypeController which serves request of the form /crime/fritype and returns a JSON object representing an instance of CrimeFriType.
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
@RequestMapping(GlobalConsts.R_ID_CRIME_FRI_TYPE)
public class CrimeFriTypeController {

    /**
     * Autowired repository of CrimeFriType
     */
    @Autowired
    private CrimeFriTypeDao crimeFriTypeRepository;
    /**
     * Autowired repository of CrimeSecType
     */
    @Autowired
    private CrimeSecTypeDao crimeSecTypeRepository;

    /**
     * Creating a crimeFriType
     *
     * @param crimeFriType
     * @return Response a string describing if the crime type is successfully created or not.
     * @throws TException
     */
//    @PreAuthorize(GlobalConsts.OAUTH_C_H_R_SUPER_ADMIN)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the crime type is successfully created or not.")
    public JsonObject create(@RequestBody @Valid CrimeFriType crimeFriType) throws Exception {
        if (crimeFriTypeRepository.findSame(0, crimeFriType.getFullName()).size() > 0)
            return new JsonObject("犯罪类型全称或简称已存在，添加失败！", 1001);
        return new JsonObject(crimeFriTypeRepository.save(crimeFriType));
    }

    /**
     * Reading list of crimeFriType
     *
     * @return Response a list describing all of crime types that is successfully get or not.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of crime types that is successfully get or not.")

    public JsonObject list() {
        return new JsonObject(this.crimeFriTypeRepository.findAll());
    }


    /**
     * Reading a crimeFriType
     *
     * @param id id of crimeFriType
     * @return Response a json string describing if the crime type id is successfully get or not.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the crime type id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this.crimeFriTypeRepository.findOne(id));
    }

    /**
     * Querying a crimeFriType
     *
     * @param name
     * @return Response a string describing if the crime type id is successfully get or not.
     */
    @RequestMapping(value = "/query/{name}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the crime type id is successfully get or not.")

    public JsonObject queryByName(@PathVariable("name") String name) {
        return new JsonObject(this.crimeFriTypeRepository.queryByText(name));
    }

    /**
     * Updating a crimeFriType
     *
     * @param id
     * @param crimeFriType
     * @return Response a string describing if the crime type is successfully updated or not.
     * @throws TException
     */
//    @PreAuthorize(GlobalConsts.OAUTH_C_H_R_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the crime type is successfully updated or not.")

    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid CrimeFriType crimeFriType) throws Exception {
        crimeFriType.setId(id);
        if (crimeFriTypeRepository.findSame(id, crimeFriType.getFullName()).size() > 0) {
            return new JsonObject("犯罪类型全称或简称已存在，修改失败！", 1001);
        }
        return new JsonObject(this.crimeFriTypeRepository.save(crimeFriType));
    }

    /**
     * Deleting a crimeFriType
     *
     * @param id
     * @return Response a string describing if the crime type is successfully delete or not.
     */
//    @PreAuthorize(GlobalConsts.OAUTH_C_H_R_SUPER_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the crime type is successfully delete or not.")
    public JsonObject delete(@PathVariable("id") long id) {
        List<CrimeSecType> crimeSecTypeList = this.crimeSecTypeRepository.findByFriId(id);
        if (null != crimeSecTypeList && crimeSecTypeList.size() > 0) {
            return new JsonObject("该犯罪类型存在子犯罪类型，删除失败！", 1001);
        }
        this.crimeFriTypeRepository.delete(id);
        return new JsonObject(true);
    }
}
