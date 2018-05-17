package intellif.mining.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.mining.common.Constant;
import intellif.mining.util.ForwardUtil;

/**
 * <h1>The Class FaceMiningController.</h1> The FaceMiningController which
 * serves request of the form /face/mining and returns a JSON object
 * representing an instance of AuditLog.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc. (see
 * <a href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and
 * static data storages), while REST is a very-high-level API style (mostly for
 * webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
@RequestMapping(Constant.R_ID_FACE_MINING)
public class FaceMiningController {

	/**
	 * 徘徊分析
	 * 
	 * @param faceMiningDto
	 *            条件
	 * @return 人员列表
	 * @throws Exception
	 */
	@ApiOperation(httpMethod = "POST", value = "徘徊分析")
	@RequestMapping(value = "/hover", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void hover(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ForwardUtil.forward(request, response);
	}

	/**
	 * 轨迹分析
	 * 
	 * @param faceMiningDto
	 *            条件
	 * @return 人员列表
	 * @throws Exception
	 */
	@ApiOperation(httpMethod = "POST", value = "轨迹分析")
	@RequestMapping(value = "/locus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void locus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ForwardUtil.forward(request, response);
	}

	/**
	 * 碰撞分析
	 * 
	 * @param faceMiningDto
	 *            条件
	 * @return 人员列表
	 * @throws Exception
	 */
	@ApiOperation(httpMethod = "POST", value = "碰撞分析")
	@RequestMapping(value = "/impact", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	
	public void impact(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ForwardUtil.forward(request, response);
	}

	/**
	 * 场所分析
	 * 
	 * @param faceMiningDto
	 *            条件
	 * @return 人员列表
	 * @throws Exception
	 */
  	@ApiOperation(httpMethod = "POST", value = "场所分析")
	@RequestMapping(value = "/place", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void place(HttpServletRequest request, HttpServletResponse response) throws Exception {
  		ForwardUtil.forward(request, response);
	}

	/**
	 * 同行分析（尾随分析）
	 * 
	 * @param faceMiningDto
	 *            列表
	 * @return 人员列表
	 * @throws Exception
	 */
	@ApiOperation(httpMethod = "POST", value = "同行分析")
	@RequestMapping(value = "/peer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void peer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ForwardUtil.forward(request, response);
	}

}
