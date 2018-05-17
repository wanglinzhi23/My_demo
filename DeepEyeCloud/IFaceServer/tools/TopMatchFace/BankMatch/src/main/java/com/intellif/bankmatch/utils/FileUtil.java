package com.intellif.bankmatch.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;
import org.apache.solr.common.StringUtils;

import com.intellif.bankmatch.TopFaceMatch;

public class FileUtil {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getWorkingDir() {
        String workingDir = System.getProperty("user.dir");
        return workingDir;
    }
    
    public static String getZipUrl(Boolean isJar) throws Exception{
    	 if (isJar) {
    		 String fix = getJarContainingFolder(TopFaceMatch.class);
    	     String bb[]  = fix.split("api");
             return bb[0]+"download/";
         } else {
             return getWorkingDir() + "/target/classes/uploads/";
         }
    }

    private static String getJarContainingFolder(Class aclass) throws Exception {
        File jarFile = new File(".");
        String jarFileAbsPath = jarFile.getAbsolutePath().toString();
//        LOG.info("jarFile.getAbsolutePath():" + jarFileAbsPath);
        String jarFileAbsFolder = removeEnd(jarFileAbsPath, ".");
//        LOG.info("jarFile folder:" + jarFileAbsFolder);
        return jarFileAbsFolder;
    }
    
    public static String removeEnd(final String str, final String remove) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }
    
    public static void checkFileExist(File file){
    	if(!file .exists()  && !file .isDirectory())      
    	{       
    	    file .mkdirs();    
    	} 
    }
    
    public static boolean copyUrl(String sourceUrl,String descDir){

		//InputStream is = FileUtil.readStreamFromUri(sourceUrl);
		boolean state = true;
		InputStream is = null;
		HttpURLConnection http = null;
		try {
			URL urlGet = new URL(sourceUrl);
			http = (HttpURLConnection) urlGet.openConnection();
			http.setRequestMethod("GET");
			http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); 
			http.connect();
            is = http.getInputStream();
			FileOutputStream os = new FileOutputStream(new File(descDir));
			try {
				IOUtils.copy(is, os);
				os.close();
			}catch (Exception e) {
				//LOG.error("copy file error1:", e);
				System.err.println("iocopy...error");
				state = false;
			} finally {
				IOUtils.closeQuietly(os);
			}
		}catch (Exception e) {
			//LOG.error("copy file error2:", e);
			System.err.println("copyurl报错"+e);
			state = false;
		} finally {
			IOUtils.closeQuietly(is);
			if(null != http){
				http.disconnect();
			}
		}
		return state;	
		
	}
}
