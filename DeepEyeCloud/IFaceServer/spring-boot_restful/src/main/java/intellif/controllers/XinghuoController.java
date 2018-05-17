package intellif.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.settings.XinghuoSettings;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(GlobalConsts.R_ID_XINGHUO)
public class XinghuoController {

	private static Logger log = LogManager.getLogger(XinghuoController.class);
	private static final Integer CONNECTION_TIMEOUT = 8*1000;
	private static final Integer SOCKET_TIMEOUT = 15*1000;

	@RequestMapping(value = "/facerecog/faceapp/ytfaces", method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "星火融合人脸1:N适配接口")
	public Object ytfaces(@RequestBody @Valid Map<String,Object> map) {
	    if(map == null || map.isEmpty()){
	        return new JsonObject("请求参数为空", 1001);
	    }
	    
	    String result = null;
	    String jsonString = null;
	    String url = XinghuoSettings.getYtFacesUrl();
	    
		try {
		    JSONObject jsonObject = JSONObject.fromObject(map);
		    jsonString = jsonObject.toString();
		    log.info("begin request ytfaces url: {}, param:{}", url, jsonString);
		    
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(url);
			StringEntity entity = new StringEntity(jsonString);
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			result = IOUtils.toString(response.getEntity().getContent());
		} catch (Exception e) {
		    log.error("request ytfaces error,url: {}, param: {}", url, jsonString, e);
		    return new JsonObject("请求出错"+result, 1001);
		}
		
		log.info("end request ytfaces url: {}, result: {}", url, result);
		return result;
	}
	
	
	@RequestMapping(value = "/facerecog/faceapp/ytSureFace", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "星火融合人脸识别比对确认接口")
    public Object ytSureFace(@RequestBody @Valid Map<String,Object> map) {
	    if(map == null || map.isEmpty()){
            return new JsonObject("请求参数为空", 1001);
        }
	    
	    String result = null;
        String jsonString = null;
        String url = XinghuoSettings.getYtSureFaceUrl();
	    
        try {
            JSONObject jsonObject = JSONObject.fromObject(map);
            jsonString = jsonObject.toString();
            log.info("begin request ytSureFace url: {}, param:{}", url, jsonString);
            
            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
           
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonString);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            result = IOUtils.toString(response.getEntity().getContent());
        } catch (Exception e) {
            log.error("request ytSureFace error,url: {}, param: {}", url, jsonString, e);
            return new JsonObject("请求出错"+result, 1001);
        }
        
        log.info("end request ytSureFace url: {}, result: {}", url, result);
        return new JsonObject(result);
    }
	
}
