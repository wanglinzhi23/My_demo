/**
 *
 */
package intellif.mining.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vividsolutions.jts.io.ParseException;
import com.wordnik.swagger.annotations.ApiOperation;

import intellif.mining.common.Constant;
import intellif.mining.util.ForwardUtil;

/**
 * <h1>The Class BlackBankController.</h1>
 * The BlackBankController which serves request of the form /black/bank and returns a JSON object representing an instance of BlackBank.
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
//@RequestMapping("/intellif/black/bank")
@RequestMapping(Constant.R_ID_PLACE)
public class PlaceController {

    /**
     * 新增场所
     * @param place
     * @return
     * @throws ParseException 
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "POST", value = "新增场所")
    public void create(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 修改场所
     * @param place
     * @return
     * @throws ParseException 
     */
    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改场所")
    public void modify(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 查询单个场所
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询单个场所")
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }

    /**
     * 分页查询场所
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "POST", value = "分页查询场所")
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 根据场所ID列表查询场所详情
     * @param idlistdto
     * @param login
     * @return
     */
    @RequestMapping(value = "ids", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据场所ID列表查询场所详情")
    public void findByIdList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }


    /**
     * 删除场所
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除场所")
    public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
}
