package intellif.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import intellif.settings.CasSSOSetting;
import net.sf.json.JSONObject;

public class HttpUtil {
    private static Logger LOG = LogManager
            .getLogger(HttpUtil.class);
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            /*
             * Map<String, List<String>> map = connection.getHeaderFields(); //
             * 遍历所有的响应头字段 for (String key : map.keySet()) {
             * System.out.println(key + "--->" + map.get(key)); }
             */
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.out.println("!!!"+result);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        LOG.info("post send message,url:"+url+" ,param:"+param);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LOG.error("post send error,url:"+url+" ,param:"+param+" ,error:",e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    public static Object sendHttpGet(String url){
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("Content-Type","application/json;charset=UTF-8");
        String content = "";
        try {
            HttpResponse response = httpClient.execute(request);
            content = IOUtils.toString(response.getEntity().getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.fromObject(content);
        if (null != jsonObject.getString("token")) {
            return "";
        }
        String token = jsonObject.getString("token");
        return token;
    }
    
    public static void sendHttpPost(String url,String applicationId,String token,JSONObject jsonObject){
        CloseableHttpClient  httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonObject.toString());
            request.addHeader("Content-Type","application/json;charset=UTF-8");
            request.addHeader("Authorization","Bear "+ token);
            request.addHeader("application_id",applicationId);
            request.setEntity(entity);
            httpClient.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    @SuppressWarnings("restriction")
    public static String fkPost(String url,  String param,String token,String applicationId) {
        //http配置
        sun.net.www.protocol.http. HttpURLConnection httpConnection = null;
        URL restServiceURL;
        PrintWriter write = null;
        BufferedReader bufferedReader = null;
        StringBuffer result = null;
        try {
            restServiceURL = new URL(url);
            httpConnection = (sun.net.www.protocol.http.HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpConnection.setRequestProperty("contentType", "UTF-8");
            //证书和平台信息
            httpConnection.setRequestProperty("token", token);
            httpConnection.setRequestProperty("application_id",applicationId);
            httpConnection.setConnectTimeout(100000);
            httpConnection.setReadTimeout(100000);
            write = new PrintWriter(httpConnection.getOutputStream());
            write.write(param);
            write.flush();
            int requestCode = httpConnection.getResponseCode();
            if (requestCode != 200) {
                LOG.error("访问失败");
            } else {
                LOG.info("access to alarm url success");

            }
            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpConnection.getInputStream(),"UTF-8"));
            String line;
            result = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            try {
                if (write != null) {
                    write.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result.toString();
    }
    
    @SuppressWarnings("restriction")
    public static String fkGet(String url) {
        //http配置
        sun.net.www.protocol.http.HttpURLConnection httpConnection = null;
        URL restServiceURL;
        PrintWriter write = null;
        BufferedReader bufferedReader = null;
        StringBuffer result = null;
        try {
            restServiceURL = new URL(url);
            httpConnection = (sun.net.www.protocol.http.HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpConnection.setRequestProperty("contentType", "UTF-8");
            httpConnection.setConnectTimeout(20000);
            httpConnection.setReadTimeout(20000);
            int requestCode = httpConnection.getResponseCode();
            if (requestCode != 200) {
                LOG.error("访问失败");
            } else {
                LOG.info("success");

            }
            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpConnection.getInputStream(),"UTF-8"));
            String line;
            result = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            try {
                if (write != null) {
                    write.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result.toString();
    }
    
    
  //将Map 按post格式转换为String 
    public static String toString(Map<String,Object> map){
        StringBuffer sb = new StringBuffer();
        //构建请求参数
        if(map!=null&&map.size()>0){
            for(Entry<String,Object> e:map.entrySet()){
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
        }
        return sb.toString();
    }


    
    
      public static void main(String args[]){
        
          String user = "";
          String password = "";
          String key = "";
          String application_id = "";
          
          if(StringUtils.isEmpty(user)||StringUtils.isEmpty(password)||StringUtils.isEmpty(key)||StringUtils.isEmpty(application_id)){
             System.err.println("反恐平台单点登录user、password、key、application_id等不能为空"); 
          }
                  
          String url = CasSSOSetting.getFkUserMd5ValidateUrl();
          url = url.replace("{application_id}","application_id");
          //请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
          String param = "user="+user+"&password="+password+"&key="+key+"&application_id="+application_id;
          String result =HttpUtil.sendPost(url,param);
          
          System.err.println("result:"+result);
       
      }

    
    
}