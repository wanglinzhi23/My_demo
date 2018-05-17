package intellif.service.impl;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import intellif.dto.VehiclePlateQueryDto;
import intellif.service.VehiclePlateServiceItf;
import intellif.settings.XinYiSettings;

@Service
public class VehiclePlateServiceImpl implements VehiclePlateServiceItf {

	private static Logger LOG = LogManager.getLogger(VehiclePlateServiceImpl.class);
	
	@Override
	public String gatherNumberByTime(VehiclePlateQueryDto vehiclePlateQueryDto) {
		String url = XinYiSettings.getVehicleApiUrl() + "gatherNumberByTime/HAIKANGDB/HK_BMS_VEHICLE_PASS";
		url += "?crossing_id=" + vehiclePlateQueryDto.getCrossingId();
		url += "&startTime=" + vehiclePlateQueryDto.getStartTime();
		url += "&endTime=" + vehiclePlateQueryDto.getEndTime();
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		try {
			LOG.info("Start request XinYi " + url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity httpEntity = response.getEntity();
			String responseStr = EntityUtils.toString(httpEntity, "UTF-8");
			LOG.info("response: " + responseStr);
			return responseStr;
		} catch (ClientProtocolException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
		return null;
	}

	@Override
	public String gatherCountByCrossing_Id(VehiclePlateQueryDto vehiclePlateQueryDto) {
		String url = XinYiSettings.getVehicleApiUrl() + "gatherCountByCrossing_Id/HAIKANGDB/HK_BMS_VEHICLE_PASS";
		url += "?crossing_id=" + vehiclePlateQueryDto.getCrossingId();
		url += "&startTime=" + vehiclePlateQueryDto.getStartTime();
		url += "&endTime=" + vehiclePlateQueryDto.getEndTime();
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		try {
			LOG.info("Start request XinYi " + url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity httpEntity = response.getEntity();
			String responseStr = EntityUtils.toString(httpEntity, "UTF-8");
			LOG.info("response: " + responseStr);
			return responseStr;
		} catch (ClientProtocolException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
		return null;
	}

	@Override
	public String getTotalCountByEveMinute(VehiclePlateQueryDto vehiclePlateQueryDto) {
		String url = XinYiSettings.getVehicleApiUrl() + "getTotalCountByEveMinute/HAIKANGDB/HK_BMS_VEHICLE_PASS";
		url += "?crossing_id=" + vehiclePlateQueryDto.getCrossingId();
		url += "&startTime=" + vehiclePlateQueryDto.getStartTime();
		url += "&endTime=" + vehiclePlateQueryDto.getEndTime();
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		try {
			LOG.info("Start request XinYi " + url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity httpEntity = response.getEntity();
			String responseStr = EntityUtils.toString(httpEntity, "UTF-8");
			LOG.info("response: " + responseStr);
			return responseStr;
		} catch (ClientProtocolException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
		return null;
	}
	
}
