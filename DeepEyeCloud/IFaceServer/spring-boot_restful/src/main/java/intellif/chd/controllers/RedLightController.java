package intellif.chd.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.chd.dao.ContrastFaceInfoDao;
import intellif.chd.dao.ZipPathDao;
import intellif.chd.dto.FaceQuery;
import intellif.chd.service.TimesClusterPersonItf;
import intellif.chd.vo.ContrastFaceInfo;
import intellif.chd.vo.ZipPath;
import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;

@RestController
@RequestMapping(GlobalConsts.R_ID_PERSON_RED_LIGHT)
public class RedLightController {
	@Autowired
	private TimesClusterPersonItf timesClusterPersonItf;
	@Autowired
	private ZipPathDao zipPathDao;
	@Autowired
	private ContrastFaceInfoDao contrastFaceInfoDao;  
	

	@RequestMapping(value = "/cluster/person", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "聚类")
	public JsonObject clusterPerson(){
		FaceQuery faceQuery = new FaceQuery();
		try {
			timesClusterPersonItf.start(faceQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JsonObject(Boolean.TRUE);
	}
	
	@RequestMapping(value = "/zip/list/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "获取压缩包")
	public JsonObject zipList(@PathVariable("page") int page,@PathVariable("pagesize") int pagesize){
		List<ZipPath> pathList = zipPathDao.findAllByPage((page-1)*pagesize, pagesize);
		return new JsonObject(pathList);
	}
	
	
	@RequestMapping(value = "/contrast/info", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "获取比中详细信息")
	public JsonObject personContrastinfo(@PathVariable("page") int page,@PathVariable("pagesize") int pagesize){
		List<ContrastFaceInfo> faceInfoList = contrastFaceInfoDao.findAllByPage((page-1)*pagesize, pagesize);
		return new JsonObject(faceInfoList);
	}

}
