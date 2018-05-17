package intellif.utils;

import intellif.settings.JinxinSetting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JinxinUtil {
    private static Logger LOG = LogManager.getLogger(JinxinUtil.class);
    private static final String rightImg64 ="iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3ZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTExIDc5LjE1ODMyNSwgMjAxNS8wOS8xMC0wMToxMDoyMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDplMzdmMTBlMy1iMjFhLWYyNDAtOGI5Mi04MzczN2ZhMzc1ZTEiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6M0NFRDEyNzk2NjFCMTFFNzgxQzY4QTY1RDVFRTgxODEiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6M0NFRDEyNzg2NjFCMTFFNzgxQzY4QTY1RDVFRTgxODEiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKFdpbmRvd3MpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6YzI5MzdiMzktMGY3MC1jZTRiLWJiYjAtMWMxZTM1ZDM5MjlhIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOmUzN2YxMGUzLWIyMWEtZjI0MC04YjkyLTgzNzM3ZmEzNzVlMSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PqqI5dYAAAJCSURBVHjavJc9TFNRFMdfa8UEg0oBB8dGBx1YGgeWaiIpcUOSkhpGqTGxEJBNoiQO4OZHrBoXdClqWFgwyqhhQmc/omGrtDQlugjkqf9D/q95ebnvq73lJL+Q5t7eH1zOPffcSG/1vBEgDoIBkAZnQQJ0cqwGfoA18BasgG2/BSM+4mNgElwHXUawqIICuAe23CZFPRbIgi/gdgipwbnyna9cI7D4AHgMFsBxo/Ho4RrPQMw5GFNIX4MhQ1/kQDfIANPtL36gWWrFJe6icquzTKJWxVUw4hRL9j40Wh+S6XG7+AaTQWd8Bn8UCTdlids0b/FfJtRpcEExfg0cirEixTVKR8E8P39XzBHXxSjFOsJ0SNvBK5e5aREnPRb7HUKac0iXwTmX+UkRn3QZHARHwYxmqUQiyqOkivfgH7gDpkNsr59UotPrknjKEioxq5Bb0uc26ZsA0noBcbu6pLYWHfKbNukVhTQVMCdqcpy+sYirYljubHCZsjlufwm8aFC6d8xE/BH0eUzK8Kclv2sbO8z/aSrk0VuNsl3xiwzv1pgGqcSSiN+BSkB5kf3XkSakm+CDiHecd6WPfJ3NXarBCicu0zpO98FGwC+eCNmD2aPCq7F+LcqRGt+H+3jCOr72AiK91qMWSp8wR5Q9l/xGiy2QyppjXu2tyd6roFFa4JqmX18tE/LM4FITwp8sOnmn1O8lIdtzBtwC5RDCMl8S0vq8bPTtZH+09fPRJo3DKdDBOv6L9f4Ti5E82nb9FvwvwACuY3r36y5wMgAAAABJRU5ErkJggg==";
    private static final String wrongImg64 ="iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3ZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTExIDc5LjE1ODMyNSwgMjAxNS8wOS8xMC0wMToxMDoyMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDplMzdmMTBlMy1iMjFhLWYyNDAtOGI5Mi04MzczN2ZhMzc1ZTEiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6MkE4ODgwODI2NjFCMTFFN0IwMzc4Mjk3RTZCMkQyNDkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6MkE4ODgwODE2NjFCMTFFN0IwMzc4Mjk3RTZCMkQyNDkiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKFdpbmRvd3MpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6OGIzMzdmM2ItYjNlOC02NTQ2LTg1OGEtMWY1MjkzNDE3NjUxIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOmUzN2YxMGUzLWIyMWEtZjI0MC04YjkyLTgzNzM3ZmEzNzVlMSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PhpSXbEAAAJkSURBVHjatJfBS1RRFIfvjM+xdqNm+zIq3KoL27iwklq6KC2SSVRSF1kLQ9BWEbbSokJrUNAoaCMRFiGIC91Ji3CjweAfMDrG2EKdQX8HzoXL5d337n3MHPgQedfz+d4797xzYyetrcIiKkE7uAmawUVQzddyIAM2wC+wDA7DEsZCxEnwBAyBWmEXu+AdmAT7pkXxgASdYAs8d5AKXkt/s805rMUV4D34As6L6FHHOT4AT7/o+Ui/gg5Ruujjp3AHFE13/LrEUhmU863pUd/lIipXPAL3dDFV7xufxcfgI1h1EGxyLr+KnpLbUIqHDYU0C/rBdbBgIV0H18BjMG4ouKdSnACDhkSXuOCoKB6GyNfALZDn3xsM68iViHM3qjMsagNpCzlJbyvSETBgyFlD/6DHrTAoUvyzV5FTPAiQvgrJeYPEjRbvziS/EEFK0eTxexQR5WfBgaOUot7jrSQiyqX0GZhwyFMdj9AI6AmdUb9wARUsgj4S/xzWy0L6r0hPQA+Yd8iTI/FfR2leebyzylZzkWfoHf8GLRGk6jvtVeQU3SH5NuI8rgiHjqRLU1qTsbnzZSneNSz4w1J1y0wYql2XfzPk3AM/SXzEM5JfrDjsU13+3bBumoZBOewleUbSezZNkGO8XWy/1YtgCYxSo9CuZcEVumt1yuwCn0V5g/r7J30CocFspozSOSn1m7mG+FGVOhZ5oDCOt0WeBtMllKY5ZyFsri7wSHqfiyFqZLmR9OnSsJMEFdpl8IL3nm3Q2pfgatCoFLM8tFVxy6Shr4kHgHPKne3woY0ObD9sDm2nAgwAVgmWZ65O96kAAAAASUVORK5CYII=";  
    private static final String whiteImg64 ="iVBORw0KGgoAAAANSUhEUgAAAL0AAABoCAIAAAArRmb7AAABzklEQVR4Ae3SsQ0AIAwEMWD/nYMYgaudOt+cvGdmOQU+C5zPf+8KvALccFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZoNNwyUAtyUajbcMFAKcFOq2XDDQCnATalmww0DpQA3pZrNBVh2A82QQWbJAAAAAElFTkSuQmCC";
  
    @SuppressWarnings("unchecked")
    public static String caller(String url, String param) {
        String str = "";
        HttpClient client = new HttpClient();
        /** 设置连接超时时间 */
        client.getHttpConnectionManager().getParams().setConnectionTimeout(3 * 1000);
        /** 设置获取信息时间 */
        client.getHttpConnectionManager().getParams().setSoTimeout(3 * 1000);
     
        PostMethod postMethod = new PostMethod(url);
        try {
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, "UTF-8");
        postMethod.getParams().setContentCharset("UTF-8");
        if (StringUtils.isNotBlank(param)) {
            JSONObject object = JSONObject.fromObject(param);
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                System.out.println();
                postMethod.addParameter(key, object.getString(key));
            }
        }
        BufferedReader reader;
        StringBuffer sb = new StringBuffer();
        
            client.executeMethod(postMethod);
            // 根据对方服务器的响应信息的编码设置此处
            reader = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod.getResponseCharSet()));
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            str = sb.toString();

        } catch (Exception e) {
            LOG.error("send jinxin message error,e:",e);
        } finally {
            postMethod.releaseConnection();
        }
        return str;
    }
   
    
    public static void sendJinxinMessage(long id,String phone,String faceUrl) { 
    JSONObject ss = new JSONObject();
    String base64Str = base64fromProcessImage(FileUtil.wrapProxyUrl(faceUrl));
    String method = "&PostOrGet=GET";
    //String fUrl = "http://172.28.0.56:9000/supp/httpClient?APP_URL=http://172.18.224.160/lg01/WebClient/asset/img/test.png&PostOrGet=GET";
    ss.put("msgType", 4);
    JSONObject msgParams = new JSONObject();
    msgParams.put("brief", "您现在疑似被人像系统检索，请审核确认是否本人，审核结果会上传至龙岗分局视频大队，如有疑问请联系龙岗分局视频大队.");
    JSONArray ja = new JSONArray();
    JSONObject content1 = new JSONObject();
    content1.put("text", "红名单预警：有人搜索了此照片!");
    content1.put("img", base64Str);
    content1.put("actionType", 0);
    content1.put("actionParams", "url");
    
    JSONObject content10 = new JSONObject();
    content10.put("text", "您现在疑似被人像系统检索，请审核确认是否本人，审核结果会上传至龙岗分局视频大队，如有疑问请联系龙岗分局视频大队.");
    content10.put("img", whiteImg64);
    content10.put("actionType", 0);
    content10.put("actionParams", "url");
    
    JSONObject content2 = new JSONObject();
    content2.put("text", "此照片是我");
    content2.put("img", rightImg64);
    content2.put("actionType", 1);
    content2.put("actionParams", JinxinSetting.getDailiUrl()+JinxinSetting.getCheckUrl()+"1"+"/name/"+phone+"/id/"+id+method);
    
    JSONObject content3 = new JSONObject();
    content3.put("text", "此照片不是我");
    content3.put("img", wrongImg64);
    content3.put("actionType", 1);
    content3.put("actionParams", JinxinSetting.getDailiUrl()+JinxinSetting.getCheckUrl()+"2"+"/name/"+phone+"/id/"+id+method);
    
    ja.add(content1);
    ja.add(content10);
    ja.add(content2);
    ja.add(content3);
    
    msgParams.put("content", ja);
    ss.put("msgParams", msgParams);
    ss.put("receivers",phone);
    ss.put("receiversName", phone);
    ss.put("appServerId", "com.searchresident");
   // LOG.info("send jinxin message:"+ss.toString());
    String result = caller(JinxinSetting.getSendUrl(), ss.toString());
    LOG.info("send jinxin red record result:"+result);
    //caller("http://172.28.0.56:9000/supp/httpClient?APP_URL=http://10.42.0.235:10001/services/gaw/sendLinkMessage&PostOrGet=POST",ss.toString());}
    }
    
    /**
     * 警信推送报警信息
     * @param id
     * @param phone
     * @param faceUrl
     */
    public static String sendJinxinAlarmMessage(String phone,String faceUrl,String dateStr,String cName,String realName) { 
        JSONObject ss = new JSONObject();
        String base64Str = base64fromProcessImage(FileUtil.wrapProxyUrl(faceUrl));
        String method = "&PostOrGet=GET";
        //String fUrl = "http://172.28.0.56:9000/supp/httpClient?APP_URL=http://172.18.224.160/lg01/WebClient/asset/img/test.png&PostOrGet=GET";
        ss.put("msgType", 4);
        JSONObject msgParams = new JSONObject();
        String brief = "动态人像库重点人员出现预警! "+realName+" 于 "+dateStr+" 出现";
        msgParams.put("brief", brief);
        JSONArray ja = new JSONArray();
        JSONObject content1 = new JSONObject();
        content1.put("text", "重点人员预警提醒!");
        content1.put("img", base64Str);
        content1.put("actionType", 0);
        content1.put("actionParams", "url");
        
        JSONObject content10 = new JSONObject();
        String str = realName+" 于 "+dateStr+" 出现在 "+cName;
        content10.put("text", str);
        content10.put("img", whiteImg64);
        content10.put("actionType", 0);
        content10.put("actionParams", "url");
      
        ja.add(content1);
        ja.add(content10);
        msgParams.put("content", ja);
        ss.put("msgParams", msgParams);
        ss.put("receivers",phone);
        ss.put("receiversName", phone);
        ss.put("appServerId", "com.searchresident");
        //LOG.info("send jinxin message:"+ss.toString());
        String result = caller(JinxinSetting.getSendUrl(), ss.toString());
        return result;
        //caller("http://172.28.0.56:9000/supp/httpClient?APP_URL=http://10.42.0.235:10001/services/gaw/sendLinkMessage&PostOrGet=POST",ss.toString());}
        }
    
    /**
=======
>>>>>>> origin/v1.4.3.1
     * 本地调试方法
     * @param id
     * @param phone
     * @param faceUrl
     */
    public static void sendJinxinMessage1(long id,String phone,String faceUrl) {
        JSONObject ss = new JSONObject();
        String method = "&PostOrGet=GET";
        String base64Str = base64fromImage(FileUtil.wrapProxyUrl(faceUrl));
        String dailiUrl = "http://172.28.0.56:9000/supp/httpClient?APP_URL=";
        String checkUrl = "http://172.18.225.233:8082/api/intellif/red/detail/check/result/";
        String fUrl = "http://172.28.0.56:9000/supp/httpClient?APP_URL=http://172.18.224.160/lg01/WebClient/asset/img/test.png&PostOrGet=GET";
        ss.put("msgType", 4);
        JSONObject msgParams = new JSONObject();
        msgParams.put("brief", "brief");
        JSONArray ja = new JSONArray();
        JSONObject content1 = new JSONObject();
        content1.put("text", "红名单预警：有人搜索了此照片!");
        content1.put("img", base64Str);
        content1.put("actionType", 0);
        content1.put("actionParams", "url");
        
        JSONObject content2 = new JSONObject();
        content2.put("text", "此照片是我");
        content2.put("actionType", 1);
        content2.put("img", rightImg64);
        content2.put("actionParams", dailiUrl+checkUrl+"aa"+"/name/"+"liuyu"+"/id/"+1);
        
        JSONObject content3 = new JSONObject();
        content3.put("text", "此照片不是我");
        content3.put("actionType", 1);
        content3.put("img", wrongImg64);
        content3.put("actionParams", dailiUrl+checkUrl+"误报"+"/name/"+phone+"/id/"+id);
        
        ja.add(content1);
        ja.add(content2);
        ja.add(content3);
        
        msgParams.put("content", ja);
        ss.put("msgParams", msgParams);
        ss.put("receivers",phone);
        ss.put("receiversName", phone);
        ss.put("appServerId", "com.searchresident");

        //caller(JinxinSetting.getSendUrl(), ss.toString());
        caller("http://172.28.0.56:9000/supp/httpClient?APP_URL=http://10.42.0.235:10001/services/gaw/sendLinkMessage&PostOrGet=POST",ss.toString());
    }
    
    public static String base64fromProcessImage(String url){
        String base64Str = null;
        try{
        Image src = javax.imageio.ImageIO.read(new URL(url)); // 构造Image对象  
        int width = src.getWidth(null); // 得到源图宽  
        int height = src.getHeight(null); // 得到源图长  

        int newwidth = width*2; 
        int newheight = height; 
        BufferedImage image = new BufferedImage(newwidth, newheight,  
          BufferedImage.TYPE_INT_RGB);  
        Graphics graphics = image.getGraphics();  
        graphics.fillRect(0,0,1000,1000);    
        graphics.drawImage(src, width/2, 0, width, height, null);

        ByteArrayOutputStream os = new ByteArrayOutputStream();  
        ImageIO.write(image, "jpg", os);  
        InputStream is = new ByteArrayInputStream(os.toByteArray());  

        byte[] data = null;  
        //读取图片字节数组  
        try   
        {  
            data = new byte[is.available()];  
            is.read(data);  
            is.close();  
        }   
        catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
        //对字节数组Base64编码  
        base64Str = DatatypeConverter.printBase64Binary(data);
       }catch(Exception e){
          LOG.error("red image convert to base64 error,image:"+url+",e:",e); 
        }
       return base64Str;
    }
    
    public static String base64fromImage(String url){
        String base64Str = null;
        try{
           FileInputStream is = new FileInputStream(new File("d:\\shi\\white.png")); 

        byte[] data = null;  
        //读取图片字节数组  
        try   
        {  
            data = new byte[is.available()];  
            is.read(data);  
            is.close();  
        }   
        catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
        //对字节数组Base64编码  
        base64Str = DatatypeConverter.printBase64Binary(data);
       }catch(Exception e){
          LOG.error("red image convert to base64 error,image:"+url+",e:",e); 
        }
        System.out.println(base64Str);
       return base64Str;
    }
}
