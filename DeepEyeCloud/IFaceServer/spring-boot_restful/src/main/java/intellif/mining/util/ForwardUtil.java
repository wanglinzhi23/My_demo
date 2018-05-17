package intellif.mining.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

import intellif.dto.JsonObject;
import intellif.mining.common.Constant;
import intellif.settings.MiningSetting;
import intellif.utils.CurUserInfoUtil;
import intellif.database.entity.UserInfo;

public class ForwardUtil {

	private static final Logger LOG = LogManager.getLogger(ForwardUtil.class);
	
	public static final String LOGIN_HEADER = "Login";

	public static void close(Closeable closeable) {
		try {
			if (null != closeable) {
				closeable.close();
			}
		} catch (Throwable e) {
			LOG.error("catch exception: ", e);
		}
	}

	public static void disconnect(HttpURLConnection conn) {
		try {
			if (null != conn) {
				conn.disconnect();
			}
		} catch (Throwable e) {
			LOG.error("catch exception: ", e);
		}
	}

	private static void writeResponseBody(HttpServletResponse response, String message) throws IOException {
		PrintWriter respOut = null;
		try {
			respOut = response.getWriter();
			respOut.print(message);
			respOut.flush();
		} finally {
			close(respOut);
		}
	}

	/**
	 * 转发给远程的数据挖掘服务器
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @throws IOException
	 */
	public static void forwardRemote(HttpServletRequest request, HttpServletResponse response, String userName)
			throws IOException {
		long startTime = System.currentTimeMillis();
		PrintWriter out = null;
		PrintWriter respOut = null;
		BufferedReader in = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		String url = null;
		String method = null;
		try {
			String sourceUrl = request.getRequestURL().toString();
			URL realUrl = new URL(
					MiningSetting.getMiningUrlBase() + sourceUrl.substring(sourceUrl.indexOf("/api/intellif/")));
			url = realUrl.toString();

			// 打开和URL之间的连接
			conn = (HttpURLConnection) realUrl.openConnection();
			// 设置头信息
			Enumeration<String> headers = request.getHeaderNames();
			while (headers.hasMoreElements()) {
				String key = (String) headers.nextElement();
				conn.setRequestProperty(key, request.getHeader(key));
			}
			method = request.getMethod();
			conn.setRequestProperty(LOGIN_HEADER, userName);
			conn.setRequestMethod(request.getMethod());
			String body = null;
			String charset = "UTF-8";

			

			// 发送POST请求必须设置如下两行
			if ("PUT".equalsIgnoreCase(request.getMethod()) || "POST".equalsIgnoreCase(request.getMethod())) {
				conn.setDoOutput(true);
				conn.setDoInput(true);
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				int size = request.getContentLength();
				if (size > 0) {
					is = request.getInputStream();
					byte[] buffer = new byte[size];
					is.read(buffer, 0, size);
					body = new String(buffer, charset);
					// 发送请求参数
					out.print(body);
					// flush输出流的缓冲
					out.flush();
				}
			}

			LOG.info("xxxxxxxxxxxxxxxxxx forward to url is {}, method is {}, body is {}, charset is {}", 
					conn.getURL(), conn.getRequestMethod(), body, charset);
			
			response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
			// 设置响应的消息体
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			StringBuilder result = new StringBuilder();
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			writeResponseBody(response, result.toString());
			
			// 设置响应的头
			response.setStatus(conn.getResponseCode());
			for (Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
				if (null == entry || StringUtils.isBlank(entry.getKey()) || CollectionUtils.isEmpty(entry.getValue())) {
					continue;
				}
				for (String value : entry.getValue()) {
					if (StringUtils.isNotBlank(value)) {
						response.setHeader(entry.getKey(), value);
					}
				}
			}
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			LOG.info("ccccc total need {}ms, url is {}, method is {}", System.currentTimeMillis() - startTime,
					url, method);
			close(in);
			close(is);
			close(out);
			close(respOut);
			disconnect(conn);
		}
	}



	public static void forward(HttpServletRequest request, HttpServletResponse response)
			throws JsonProcessingException, IOException {
		try {
			LOG.info("xxxxxxxxxxxxxxxxxx forward from url is {}, method is {}", request.getRequestURL(), 
					request.getMethod());

			UserInfo userInfo = CurUserInfoUtil.getUserInfo();
			if (null == userInfo) {
				writeResponseBody(response, Constant.OBJECT_MAPPER.writeValueAsString(new JsonObject("请先登录！", 1001)));
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				response.setStatus(401);
				return;
			}

			forwardRemote(request, response, userInfo.getLogin());
		} catch (Throwable e) {
			LOG.error("catch exception: ", e);
			writeResponseBody(response, Constant.OBJECT_MAPPER.writeValueAsString(new JsonObject("系统异常！", 1001)));
			response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
			response.setStatus(200);
		}

	}
}
