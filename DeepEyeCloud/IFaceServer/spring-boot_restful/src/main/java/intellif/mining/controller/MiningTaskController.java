/**
 *
 */
package intellif.mining.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
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
@RequestMapping(Constant.R_ID_MINING_TASK)
public class MiningTaskController {

    /**
     * 新增任务
     * @param miningTask
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "POST", value = "新增任务")
    public void create(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 修改任务
     * @param miningTask
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改任务")
    public void update(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }

    /**
     * 分页查询任务列表
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "POST", value = "分页查询任务列表")
    public void findAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 查询单个任务
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询单个任务")
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 对任务的输出结果进行分页查询
     * @param id
     * @return
     */
    @RequestMapping(value = "output/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "GET", value = "对任务的输出结果进行分页查询")
    public void outputList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }


    /**
     * 删除任务
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除任务")
    public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 删除任务结果中的人脸
     * @param faceId
     * @return
     * @throws IOException 
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/delete/face/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "删除任务结果中的人脸")
    public void deleteByFaceId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ForwardUtil.forward(request, response);
    }
    
    /**
     * 启动任务
     * @param faceId
     * @return
     */
    @RequestMapping(value = "start/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "GET", value = "启动任务")
    public void start(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }

    /**
     * 启动任务
     * @param faceId
     * @return
     */
    @RequestMapping(value = "{clusterType}/cluster/{faceAmount}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "GET", value = "聚类分析")
    public void cluster(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 对任务的输出结果中的内容进行分页查询
     * @param id
     * @return
     */
    @RequestMapping(value = "output/content/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "POST", value = "对任务的输出结果中的内容进行分页查询")
    public void content(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
    
    /**
     * 根据指定的摄像头列表和时间段查询人脸
     * 
     * @return
     */
    @RequestMapping(value = "face/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据指定的摄像头列表和时间段查询人脸")
    public void face(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ForwardUtil.forward(request, response);
    }
}
