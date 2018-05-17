package intellif.lire;

import intellif.configs.PropertiesBean;
import intellif.utils.FileUtil;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DownloadClearThread extends Thread {

    private static Logger LOG = LogManager.getLogger(DownloadClearThread.class);
    @Autowired
    private PropertiesBean propertiesBean;

    @Scheduled(cron = "0 0 0 * * ?")
    public void run() {
    	try{
    		File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/zip/");
    		File file1 = new File(FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/image/");
    		FileUtil.deleteFile(file,true);
    		FileUtil.deleteFile(file1,true);
    	}catch(Exception e){
    		LOG.error("定时删除导出数据出错",e);
    	}
    }

}
