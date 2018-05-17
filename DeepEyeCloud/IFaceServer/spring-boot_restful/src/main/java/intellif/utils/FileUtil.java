package intellif.utils;

import intellif.Application;
import intellif.common.Constants;
import intellif.dto.StoreImageDto;
import intellif.settings.ImageSettings;
import intellif.settings.ServerSetting;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

public class FileUtil {

    private static Logger LOG = LogManager
            .getLogger(FileUtil.class);

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getWorkingDir() {
        String workingDir = System.getProperty("user.dir");
        return workingDir;
    }

    public static String getUploads(Boolean isJar) throws Exception {
        // if(!new File("/uploads/").exists()) new File("/uploads/").mkdirs();
        // return "/uploads/";
    	if (isJar) {
            //return getJarContainingFolder(Application.class) + "/uploads/";
            return ImageSettings.getUploadDir();
            
        } else {
            return getWorkingDir() + "/target/classes/uploads/";
        }
    }
    
    public static String getZipUrl(Boolean isJar) throws Exception{
    	 if (isJar) {
    		 String fix = getJarContainingFolder(Application.class);
    	     String bb[]  = fix.split("api");
             return bb[0]+"download/";
         } else {
             return getWorkingDir() + "/target/classes/uploads/";
         }
    }
    
    public static String getChdZipUrl(Boolean isJar) throws Exception{
        if (isJar) {
            String fix = getJarContainingFolder(Application.class);
            String bb[]  = fix.split("api");
            return bb[0]+"chd/";
        } else {
            return getWorkingDir() + "/target/classes/uploads/";
        }
   }
    
    public static String getZipHttpUrl(Boolean isJar) throws Exception{
    	 if (isJar) {
             return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getWserverPort()
                     + ServerSetting.getWserverPath().replace("/api", "") + "/download/";
         } else {
             return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getPort()
                     + "/target/classes/uploads/";
         }
   }
    
    public static String getChdZipHttpUrl(Boolean isJar) throws Exception{
        if (isJar) {
            return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getWserverPort()
                    + ServerSetting.getWserverPath().replace("/api", "") + "/chd/";
        } else {
            return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getPort()
                    + "/target/classes/uploads/";
        }
  }
    
    public static String getUploadsHttpUrl(Boolean isJar) throws Exception{
   	 if (isJar) {
            return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getWserverPort()
                    + ServerSetting.getWserverPath() + "/uploads/";
        } else {
            return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getPort()
                    + "/target/classes/uploads/";
        }
  }

    //@see: http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
    private static String getJarContainingFolder(Class aclass) throws Exception {
        //@see:http://stackoverflow.com/questions/2837263/how-do-i-get-the-directory-that-the-currently-executing-jar-file-is-in
        File jarFile = new File(".");
        String jarFileAbsPath = jarFile.getAbsolutePath().toString();
        LOG.info("jarFile.getAbsolutePath():" + jarFileAbsPath);
        String jarFileAbsFolder = StringUtils.removeEnd(jarFileAbsPath, ".");
        LOG.info("jarFile folder:" + jarFileAbsFolder);
        return jarFileAbsFolder;
//        CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();
//        LOG.info("codeSource:" + codeSource.toString());
//        LOG.info("codeSource.getLocation():" + codeSource.getLocation().toString());
//        LOG.info("codeSource.getLocation().getPath():" + codeSource.getLocation().getPath().toString());
//        LOG.info("codeSource.getLocation().getFile():" + codeSource.getLocation().getFile().toString());
//        LOG.info("codeSource.getLocation().getFile():" + codeSource.getLocation().getFile().toString());
//        LOG.info("codeSource.getLocation().toURI():" + codeSource.getLocation().toURI().toString());
////        File jarFile;
//        File jarFile = new File(codeSource.getLocation().toURI().getPath());
//        String jarDir = jarFile.getParentFile().getPath();
//        LOG.info("jarFile:" + jarFile.toString());
//        LOG.info("jarDir:" + jarDir);
//        //
//        if (codeSource.getLocation() != null) {
//            jarFile = new File(codeSource.getLocation().toURI());
//        } else {
//            String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
//            String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
//            jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
//            jarFile = new File(jarFilePath);
//        }
//        LOG.info("jarFile:" + jarFile.toString());
//        LOG.info("jarFile.getParentFile():" + jarFile.getParentFile().toString());
//        LOG.info("jarFile.getParentFile().getAbsolutePath():" + jarFile.getParentFile().getAbsolutePath().toString());
//        return jarFile.getParentFile().getAbsolutePath();
    }

    public static StoreImageDto getStoreImageUri(String imageUrl, Boolean isJar) {
        //Translate the store located image URI.
        StoreImageDto result = new StoreImageDto();
        try {
            new URL(imageUrl);
            String baseName_img = FilenameUtils.getBaseName(imageUrl);
            String extension_img = FilenameUtils.getExtension(imageUrl);
            String fileNameAppendix_img = baseName_img + "." + extension_img;
//                System.out.println("fileNameAppendix_img:" + fileNameAppendix_img);
            String fullFileName_img = FileUtil.getUploads(isJar) + fileNameAppendix_img;
            try {
                //image
                File oriFile_img = new File(fullFileName_img);
                result.oriImageUri = fullFileName_img;
                result.storeImageUri = ImageSettings.getStoreLocalPath() + oriFile_img.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static StoreImageDto getStoreFaceUri(String faceUrl, Boolean isJar) {
        //Translate the store located face URI.
        StoreImageDto result = new StoreImageDto();
        try {
            new URL(faceUrl);
            String baseName_face = FilenameUtils.getBaseName(faceUrl);
            String extension_face = FilenameUtils.getExtension(faceUrl);
            String fileNameAppendix_face = baseName_face + "." + extension_face;
            String fullFileName_face = FileUtil.getUploads(isJar) + fileNameAppendix_face;
//                System.out.println("fullFileName_face:" + fullFileName_face);
            try {
                //face
                File oriFile_face = new File(fullFileName_face);
                result.oriImageUri = fullFileName_face;
                result.storeImageUri = ImageSettings.getStoreLocalPath() + oriFile_face.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 压缩文件
     * @param sourceStr
     * @param descStr
     * @throws Exception
     */
    public static void zipCompress(String sourceStr, String descStr)throws Exception{
    	     File zipFile = new File(descStr);  
	          File srcdir = new File(sourceStr);  
	          if (!srcdir.exists()){
	        	  throw new RuntimeException("文件无数据");  
	          }
	          Project prj = new Project();  
	          Zip zip = new Zip();  
	          zip.setProject(prj);  
	          zip.setDestFile(zipFile);  
	          FileSet fileSet = new FileSet();  
	          fileSet.setProject(prj);  
	          fileSet.setDir(srcdir);  
	          zip.addFileset(fileSet);  
	          zip.execute();  
    }
    /**
     * 删除某个文件下的所有文件
     * @param file
     */
    public static void deleteFile(File file,boolean isDelete) { 
   
        if (file.exists()) {//判断文件是否存在  
         if (file.isFile()) {//判断是否是文件  
          file.delete();//删除文件   
         } else if (file.isDirectory()) {//否则如果它是一个目录  
          File[] files = file.listFiles();//声明目录下所有的文件 files[];  
          if(files==null){return;}    
          for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件  
           deleteFile(files[i],true);//把每个文件用这个方法进行迭代  
          }  
          if(isDelete){
        	  file.delete();//删除文件夹  
          }
         }  
        } else {  
         System.out.println("所删除的文件不存在");  
        }  
       }  
    
    public static void checkFileExist(File file){
    	//如果文件夹不存在则创建    
    	if(!file .exists()  && !file .isDirectory())      
    	{       
    	    file .mkdirs();    
    	} 
    }

	public static synchronized void log(String content) {
		try {
			FileWriter writer = new FileWriter("search.log", true);
			writer.write(sdf.format(new Date())+" -> "+content+"\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String GetImageStr(String urlPath)  
    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
        InputStream in = null;  
        byte[] data = null;  
        //读取图片字节数组  
        try   
        {  
			URL url = new URL(urlPath);

			DataInputStream dataInputStream = new DataInputStream(url.openStream());
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[4096];
			int length;

			while ((length = dataInputStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			data = outStream.toByteArray();
			
			dataInputStream.close();
			outStream.close();
			
//			URLConnection con = url.openConnection();
//            data = new byte[con.getContentLength()];
//            in = url.openStream();
//            in.read(data);
//            in.close();  
        }   
        catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
        //对字节数组Base64编码  
        return DatatypeConverter.printBase64Binary(data);//返回Base64编码过的字节数组字符串  
    }  
	
	
	public static void writeStreamToFile(InputStream is,File file) throws IOException{
		FileOutputStream fos = null;
		try{
			 fos = new FileOutputStream(file);
			byte[] buffer = new byte[Constants.MAX_FILE_BUFFER_ZIP];// BufferSize
			int len;
			while ((len = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
		}catch(Exception e){
			LOG.error("write inputStream to file error,filepath:"+file.getAbsolutePath(),e);
		}
		finally{
					if(fos!=null){
						fos.close();	
					}
					if(is!=null){
						is.close();	
					}
		}
	}
	
	public static void writeStringToFile(String content, String filePath) {
		FileOutputStream fos = null;
		try {
		    fos = new FileOutputStream(filePath, true);// true表示在文件末尾追加
			fos.write(content.getBytes());
			
		} catch (Exception e) {
			LOG.error("write String to file error,filePath:" + filePath, e);
		}finally{
			try {
				if(fos!=null){
					fos.close();	
				}
			} catch (IOException e) {
				LOG.error("close stream error:",e);
			}// 流要及时关闭
		}
	}
	
	public static List<String> readFile(String filePath){
		List<String> list = new ArrayList<String>();
        try {
                String encoding="UTF8";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	if(lineTxt.trim().length()>0) {
                    		list.add(lineTxt.trim());
                    	}
                    }
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
		return list;
    }
	
	public static void copyDir(String sourceDir,String descDir){
		BufferedOutputStream stream = null;
		try{
		Path path = Paths.get(sourceDir);
	    stream = new BufferedOutputStream(new FileOutputStream(new File(descDir)));
        stream.write(Files.readAllBytes(path));
		}catch(Exception e){
			LOG.error("copy file error, sourceDir:"+sourceDir+" descDir:"+descDir);
		}finally{
			if(null != stream){
				try {
					stream.close();
				} catch (IOException e) {
					LOG.error("close stream error:",e);
				}
			}
		}
	}
	/**
	 * 适配nigux代理
	 * @param url
	 * @return
	 */
	public static String wrapProxyUrl(String url){
	    if(!url.startsWith("http:")) {
	        url = "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getWserverPort() + "/" +url;
        }
	    return url;
	}
	public static boolean copyUrl(String sourceUrl,String descDir){
		boolean state = true;
		InputStream is = null;
		HttpURLConnection http = null;
	try {
		if(!sourceUrl.startsWith("http:")) {
			sourceUrl = "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getWserverPort() + "/" +sourceUrl;
		}
		//InputStream is = FileUtil.readStreamFromUri(sourceUrl);
		
	     URL urlGet = new URL(sourceUrl);
          http = (HttpURLConnection) urlGet
                 .openConnection();
         http.setRequestMethod("GET"); // ������get��ʽ����
         http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
         http.setDoOutput(true);
         http.setDoInput(true);
         System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// ���ӳ�ʱ30��
         System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // ��ȡ��ʱ30��
         http.connect();
         // ��ȡ�ļ�ת��Ϊbyte��
            is = http.getInputStream();
			FileOutputStream os = new FileOutputStream(new File(descDir));
			try {
				IOUtils.copy(is, os);
				os.close();
			}catch (Exception e) {
				LOG.error("copy file error1:", e);
				state = false;
			} finally {
				IOUtils.closeQuietly(os);
			}
		}catch (Exception e) {
			LOG.error("copy file error2:", e);
			state = false;
		} finally {
			IOUtils.closeQuietly(is);
			if(null != http){
				http.disconnect();
			}
		}
		return state;	
	}
	public static boolean checkUrlIsOrNotExist(String url){
		URL uRl = null;
		try {  
			uRl = new URL(url);  
			uRl.openStream();  
		} catch (Exception e1) {  
			return false;
		}  
		return true;
	}
	/**
	 * 遍历获取某个文件夹下所有文件
	 * @param dir
	 * @param dirList
	 */
	public static void getAllFiles(File dir,List<String> dirList){
		
		try{
			if(dir.isDirectory()){
				String[] nameList = dir.list();
				if(nameList==null){return ;} 
				for(String item : nameList){
					getAllFiles(new File(dir.getPath()+"/"+item),dirList);
				}
			}else{
				String abDir = dir.getPath();
				dirList.add(abDir);
				dir = null;
			}
		}catch(Exception e){
			LOG.error("count pk base size error",e);
		}
		
	}
	
	public static String escapeExprSpecialWord(String keyword) {  
	    if (StringUtils.isNotBlank(keyword)) {  
	        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };  
	        for (String key : fbsArr) {  
	            if (keyword.contains(key)) {  
	                keyword = keyword.replace(key, "\\" + key);  
	            }  
	        }  
	    }  
	    return keyword;  
	}  
	public static String compressZip(String randPath, String folderName,boolean isJar) throws Exception {
		String httpPath = null;
		String sourcePath = FileUtil.getUploads(isJar) + File.separator + randPath + File.separator
				+ folderName + File.separator;
		String descPath = FileUtil.getZipUrl(isJar) + "export/zip/" + randPath + "/";
		File file = new File(descPath);
		FileUtil.deleteFile(file, true);
		FileUtil.checkFileExist(file);
		FileUtil.zipCompress(sourcePath, descPath + File.separator + folderName + ".zip");
		File zipFile = new File(descPath + File.separator + folderName + ".zip");
		if (!zipFile.exists()) {
			throw new Exception("压缩失败");
		}
		httpPath = FileUtil.getZipHttpUrl(isJar) + "export/zip/" + randPath + File.separator
				+ folderName + ".zip";
		return httpPath;
	}
}
