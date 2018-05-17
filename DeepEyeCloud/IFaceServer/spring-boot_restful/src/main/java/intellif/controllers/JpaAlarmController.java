package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import intellif.consts.GlobalConsts;
import intellif.dao.JpaAlarmDao;
import intellif.dto.AlarmInfoDto;
import intellif.dto.JsonObject;
import intellif.service.AlarmServiceItf;
import intellif.utils.PageResource;
import intellif.database.entity.AlarmInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * <h1>The Class JpaAlarmController.</h1>
 * The JpaAlarmController which serves request of the form /jpa/alarm and returns a JSON object representing an instance of AlarmInfo.
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
//@RequestMapping("/intellif/jpa/alarm")
@RequestMapping(GlobalConsts.R_ID_JPA_ALARM)
public class JpaAlarmController {

    // ==============
    // PRIVATE FIELDS
    // ==============

    private static Logger LOG = LogManager.getLogger(JpaAlarmController.class);

    @Autowired
    private AlarmServiceItf _alarmService;

    @Autowired
    private JpaAlarmDao _jpaAlarmRepository;


    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the paging and sorting alarm info is successfully updated or not.")
    public PageResource<AlarmInfo> pageList(@RequestParam int page, @RequestParam int size) {

        Pageable pageable = new PageRequest(
                page, size, new Sort("time")
        );
        Page<AlarmInfo> pageResult = _jpaAlarmRepository.findAll(pageable);
        return new PageResource<AlarmInfo>(pageResult, "page", "size");
    }

    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the paging and sorting alarm info is successfully updated or not.", notes = "Default as 2015~2017 time period.")
    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public JsonObject findByCombinedConditions(@RequestBody @Valid AlarmInfoDto alarmInfoDto, @RequestParam int page, @RequestParam int size) {
//@see:http://www.java2s.com/Code/Java/Collections-Data-Structure/Pagingoveracollection.htm
        // Pageable pageable = new PageRequest(
//                page, size, new Sort("time")
//        );
//        int page = 1;
//        int size = 10;
        List<AlarmInfoDto> listResult = _alarmService.findByCombinedConditions(alarmInfoDto);
        intellif.utils.Pageable pageableResult = new intellif.utils.Pageable(listResult);
        LOG.info("Pageable parameter for AlarmInfoDto,page:" + page + ",size:" + size);
        pageableResult.setPage(page);
        pageableResult.setPageSize(size);
        //FIXME:none pageSized results return.
        return new JsonObject(pageableResult.getListForPage());
    }
}