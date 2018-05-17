package intellif.controllers;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.settings.XinYiSettings;

@RestController
@RequestMapping(GlobalConsts.R_ID_XINYI)
public class XinyiController {

	private static Logger LOG = LogManager.getLogger(XinyiController.class);

	@RequestMapping(value = "/{identity}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "通过身份证号码查询头像图片")
	public Object findByTableCode(@PathVariable("identity") String identity) {
		String content = null;
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String url = XinYiSettings.getIdentityQueryApiUrlBegin() + identity
					+ XinYiSettings.getIdentityQueryApiUrlEnd();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			content = IOUtils.toString(response.getEntity().getContent());
		} catch (Exception e) {
			LOG.error("xinyi api query identity error", e);
			return new JsonObject("信义接口查询出错", 1001);
		}
		if (StringUtils.isNotBlank(content)) {
			if (content.indexOf("\"result\"") != -1) {
				content = content.replaceFirst("\"result\"", "\"data\"");
				content = content.substring(0, content.length()-1)+",\"errCode\":0,\"maxPage\":0}";
			}
		}
		return content;
	}

}
