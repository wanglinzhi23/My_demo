package intellif.configs;

import intellif.chd.settings.CameraNodeIdSetting;
import intellif.chd.settings.MobileCollectSyncSetting;
import intellif.consts.GlobalConsts;
import intellif.controllers.PoliceManController;
import intellif.fk.settings.FKLoginSettings;
import intellif.oauth.OAuth2Settings;
import intellif.settings.BankImportSetting;
import intellif.settings.CasSSOSetting;
import intellif.settings.ImageSettings;
import intellif.settings.JinxinSetting;
import intellif.settings.LongGangRedPersonSettings;
import intellif.settings.MiningSetting;
import intellif.settings.MqttSettings;
import intellif.settings.OfflineSetting;
import intellif.settings.PerformParamSetting;
import intellif.settings.ResidentSetting;
import intellif.settings.ResumableJsUploadSetting;
import intellif.settings.ServerSetting;
import intellif.settings.SolrCloudSetting;
import intellif.settings.StreamMediaSettings;
import intellif.settings.TableDivideSetting;
import intellif.settings.ThreadSetting;
import intellif.settings.XinYiSettings;
import intellif.settings.XinghuoSettings;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Register this with the DispatcherServlet in a ServletInitializer class like:
 * dispatcherServlet.setContextInitializers(new PropertiesInitializer());
 */
public class PropertiesInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger LOG = LogManager.getLogger(PropertiesInitializer.class);

    /**
     * Runs as appInitializer so properties are wired before spring beans
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();

        String[] activeProfiles = getActiveProfiles(env);

        for (String profileName : activeProfiles) {
            LOG.info("Loading properties for Spring Active Profile: {}", profileName);
            try {
                // ResourcePropertySource propertySource =
                // new ResourcePropertySource(profileName + "EnvProperties",
                // "classpath:application-" + profileName
                // + ".properties");

	            InputStream inputStream = null;
	            try {
		            inputStream = new FileInputStream(new File("application-" + profileName + ".properties"));
	            } catch (FileNotFoundException e) {
		            inputStream = PropertiesInitializer.class.getClassLoader().getResourceAsStream("application-" + profileName + ".properties");
		            if(inputStream==null){
			            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application-" + profileName + ".properties");
		            }
	            }

	            Properties p = new Properties();
                p.load(inputStream);
                inputStream.close();
                PropertiesPropertySource propertySource = new PropertiesPropertySource(profileName + "EnvProperties", p);

                env.getPropertySources().addLast(propertySource);
                LOG.debug("propertySource:" + propertySource.toString());
                // Work-flow setting initialization here.
                // TODO: @see https://github.com/EsotericSoftware/yamlbeans to
                // replace this staff.
                MqttSettings.setUri((String) propertySource.getProperty("mqtt.uri"));

                // stream media
                StreamMediaSettings.setIp((String) propertySource.getProperty("stream.ip"));
                StreamMediaSettings.setPort(Integer.valueOf((String) propertySource.getProperty("stream.port")));

                ResidentSetting.setRate(Float.valueOf((String) propertySource.getProperty("resident.rate")));
                ResidentSetting.setIp((String) propertySource.getProperty("resident.ip"));
                ResidentSetting.setUsername((String) propertySource.getProperty("resident.username"));
                ResidentSetting.setPassword((String) propertySource.getProperty("resident.password"));
                ResidentSetting.setCommand((String) propertySource.getProperty("resident.command"));
                ServerSetting.setPort(Integer.valueOf((String) propertySource.getProperty("server.port")));
                ServerSetting.setContextPath((String) propertySource.getProperty("server.contextPath"));
                ServerSetting.setSolrServer((String) propertySource.getProperty("solr.server"));
                ServerSetting.setWserverPort(Integer.valueOf((String) propertySource.getProperty("web.server.port")));
                ServerSetting.setWserverPath((String) propertySource.getProperty("web.server.path"));
                ServerSetting.setIndexRate(Long.valueOf((String) propertySource.getProperty("solr.index.rate")));
                ServerSetting.setIndexHours(Long.valueOf((String) propertySource.getProperty("solr.index.hours")));
                ServerSetting.setIndexStep(Integer.valueOf(propertySource.getProperty("solr.index.increase.step").toString()));
                try {
                    ServerSetting.setUseRabbit(Boolean.valueOf(propertySource.getProperty("solr.use.rabbit").toString().trim()));
                } catch (Throwable t) {
                    LOG.warn("not set 'solr.use.rabbit' in properties, use default false");
                }
                
                try {
                    ServerSetting.setSolrStaticResultMaxSize(Integer.valueOf(propertySource.getProperty("solr.static.max").toString().trim()));
                } catch (Throwable t) {
                    LOG.warn("not set 'solr.static.max' in properties, use default 500");
                }
                // 离线布控阈值
                ServerSetting.setThreshold(Float.valueOf((String) propertySource.getProperty("crime.alarm.threshold")));

                // 警务云推送告警消息的地址及参数
                PoliceManController.uri = propertySource.getProperty("uri").toString();
                PoliceManController.param = propertySource.getProperty("param").toString();

                //
                // System.out.println((String)
                // propertySource.getProperty("imageStore.local"));
                // System.out.println((String)
                // propertySource.getProperty("imageStore.remote"));
                ImageSettings.setStoreLocalPath((String) propertySource.getProperty("image.store.local"));
                ImageSettings.setStoreRemoteUrl((String) propertySource.getProperty("image.store.remote"));
                ImageSettings.setFaceScale(Integer.valueOf((String) propertySource.getProperty("image.face.scaleXY")));
                ImageSettings.setFaceOffsetX(Integer.valueOf((String) propertySource.getProperty("image.face.offsetX")));
                ImageSettings.setFaceOffsetY(Integer.valueOf((String) propertySource.getProperty("image.face.offsetY")));
                ImageSettings.setStoreRemoteHost((String) propertySource.getProperty("image.store.host"));
                BankImportSetting.setPkDir((String) propertySource.getProperty("image.pk.dir"));
                ImageSettings.setUploadDir((String) propertySource.getProperty("image.store.local"));
                Object poolSize = propertySource.getProperty("pk.thread.corePoolSize");
                if (null != poolSize && !"".equals(poolSize)) {
                    BankImportSetting.setCorePoolSize(Integer.parseInt((String) propertySource.getProperty("pk.thread.corePoolSize")));
                }
                ImageSettings.setJsonSwitch(Boolean.parseBoolean((String) propertySource.getProperty("image.face.json.switch")));
                TableDivideSetting.setTable_divide_size(Integer.parseInt((String) propertySource.getProperty("table.divide.size")));
                TableDivideSetting.setTable_divide_starttime((String) propertySource.getProperty("table.divide.starttime"));
                ThreadSetting.setBlackThreadsNum(Integer.parseInt((String) propertySource.getProperty("black.thread.num")));

                LongGangRedPersonSettings.setRedPersonSearch(propertySource.getProperty("longgang.redperson.switch").toString());
                Object engineListenOn = propertySource.getProperty("engine.Listen");
                if (null != engineListenOn && !"".equals(engineListenOn)) {
                    if ("true".equals(engineListenOn)) {
                        ServerSetting.setEngineStatusOn(true);
                    }
                }

                // 文件IP分离功能开关
                Object SeparateIpOn = propertySource.getProperty("separate.ip");
                if (null != SeparateIpOn && !"".equals(SeparateIpOn)) {
                    if ("true".equals(SeparateIpOn)) {
                        ServerSetting.setSeparateIpOn(true);
                    }
                }

                OauthAuthorizationServerConfiguration.setToken_expire_in(Integer.parseInt((String) propertySource.getProperty("expire_in"))); // 设置token的过期时间
                OauthAuthorizationServerConfiguration
                        .setToken_invalidate_time(new Integer(propertySource.getProperty("invalidate_time").toString()).intValue()); // 设置token的失效时间
                OAuth2Settings.setAccessTimeInterval(new Integer(propertySource.getProperty("api.access.limit").toString()));
                String whiteList = propertySource.getProperty("api.swagger.whitelist.endwith").toString();
                OAuth2Settings.setWhiteList(Arrays.asList(whiteList.split(",")));
                Object blackList = propertySource.getProperty("api.swagger.blacklist");
                if (blackList != null) {
                    OAuth2Settings.setBlackList(Arrays.asList(blackList.toString().split(",")));
                }

                SolrCloudSetting.setZkServers(propertySource.getProperty("solr.cloud.zookeeper.server").toString());

                CasSSOSetting.setTicketValidateUrl(propertySource.getProperty("sso.cas.validate.url").toString()); 
                CasSSOSetting.setFkUserMd5ValidateUrl(propertySource.getProperty("fkUser.auth.validate.url").toString());

                //solr搜索的超时时间 

                ServerSetting.setSolrSearchTimeOutTime(Integer.valueOf((String) propertySource.getProperty("solr.search.timeout")));
                // solr服务的连接超时时间
                ServerSetting.setSolrServerConnectOutTime(Integer.valueOf((String) propertySource.getProperty("solr.connet.timeout")));
                // solr返回结果限制
                ServerSetting.setSolrResultMaxSize(Integer.valueOf((String) propertySource.getProperty("solr.result.maxsize")));

                // 系统混合算法版本列表
                if (null != propertySource.getProperty("alg.version")) {
                    List<Integer> versionList = new ArrayList<Integer>();
                    try {
                        for(String version : propertySource.getProperty("alg.version").toString().split(",")) {
                            versionList.add(Integer.valueOf(version));
                        }
                    }   catch(Exception e) {
                    }
                    if(versionList.size() == 0) versionList.add(0);
                    ServerSetting.setAlgVersionList(versionList);
                }
                
                XinYiSettings.setVehicleApiUrl((String) propertySource.getProperty("xinyi.vehicle.server"));
                XinYiSettings.setUserApiUrl((String) propertySource.getProperty("xinyi.user.url")); 
                XinYiSettings.setQiangdanUrl((String) propertySource.getProperty("xinyi.qiangdan.url")); 
                XinYiSettings.setIdentityQueryApiUrlBegin(propertySource.getProperty("xinyi.identity.url.begin")!=null ? propertySource.getProperty("xinyi.identity.url.begin").toString() : ""); 
                XinYiSettings.setIdentityQueryApiUrlEnd(propertySource.getProperty("xinyi.identity.url.end")!=null ?propertySource.getProperty("xinyi.identity.url.end").toString(): "");
                XinYiSettings.setXinyiSwitch(propertySource.getProperty("enable.xinyi.switch").toString());


                // chd
                CameraNodeIdSetting.setNodeId(Long.parseLong((String) (propertySource.getProperty("camera.node.id"))));
         
                /**
                 * fk setting
                 * 
                 */
                FKLoginSettings.setLoginUrl((String)propertySource.getProperty("login.url"));
                FKLoginSettings.setTokenUrl((String)propertySource.getProperty("token.url"));
                FKLoginSettings.setAlarmUrl((String)propertySource.getProperty("alarm.url"));
                FKLoginSettings.setApplicationId((String)propertySource.getProperty("application.id"));
                //lire stop
                if (null != propertySource.getProperty("mining.url.base")) {
                    MiningSetting.setMiningUrlBase(propertySource.getProperty("mining.url.base").toString());
                }
                if(propertySource.getProperty("run").equals("true")){
                     GlobalConsts.run =true;
                }else{
                	 GlobalConsts.run =false;

                }
                if (null != propertySource.getProperty("offline.bukong.switch")) {
                    if(propertySource.getProperty("offline.bukong.switch").equals("true")){
                       OfflineSetting.setRun(true);
                   }else{
                       OfflineSetting.setRun(false);

                   }
                }

                if(null != propertySource.getProperty("jinxin.check.url")){
                    JinxinSetting.setCheckUrl((String)propertySource.getProperty("jinxin.check.url"));
                }
                if(null != propertySource.getProperty("jinxin.send.url")){
                    JinxinSetting.setSendUrl((String)propertySource.getProperty("jinxin.send.url"));
                }
                if(null != propertySource.getProperty("jinxin.daili.url")){
                    JinxinSetting.setDailiUrl((String)propertySource.getProperty("jinxin.daili.url"));
                }
                if(null != propertySource.getProperty("jinxin.alarm.confidence")){
                    JinxinSetting.setConfidence(Float.parseFloat((String)propertySource.getProperty("jinxin.alarm.confidence")));
                }
                if(null != propertySource.getProperty("jinxin.alarm.areaIds")){
                    JinxinSetting.setpIds((String)propertySource.getProperty("jinxin.alarm.areaIds"));
                }
                if(null != propertySource.getProperty("jinxin.alarm.run")){
                    if(propertySource.getProperty("jinxin.alarm.run").equals("true")){
                        JinxinSetting.run =true;
                    }else{
                        JinxinSetting.run =false;
                    }
                }

                Object xinyiUserSwitch = propertySource.getProperty("xinyi.user.switch");
                if (null != xinyiUserSwitch && !"".equals(xinyiUserSwitch)) {
                    if ("true".equals(xinyiUserSwitch)) {
                        XinYiSettings.setXinyiUserSwitch(true);
                    }
                }

                if (null != propertySource.getProperty("mobilecollect.serverurl")) {
                    MobileCollectSyncSetting.setServerUrl((String) propertySource.getProperty("mobilecollect.serverurl"));
                }

                if (null != propertySource.getProperty("cron")) {
                    MobileCollectSyncSetting.setCron((String) propertySource.getProperty("cron"));
                }
                
                Object alarmSelectCount = propertySource.getProperty("perform.alarm.select");
                if (null != alarmSelectCount && !"".equals(alarmSelectCount)) {
                    PerformParamSetting.setSelectAlarmNum(Integer.parseInt((String) propertySource.getProperty("perform.alarm.select")));
                }
                Object scanCount = propertySource.getProperty("perform.scan.count");
                if (null != scanCount && !"".equals(scanCount)) {
                    PerformParamSetting.setScanCount(Integer.parseInt((String) propertySource.getProperty("perform.scan.count")));
                }
                Object bankCount = propertySource.getProperty("perform.bank.count");
                if (null != bankCount && !"".equals(bankCount)) {
                    PerformParamSetting.setBankPersonNum(Integer.parseInt((String) propertySource.getProperty("perform.bank.count")));
                }
                Object qiangdanSyncUrl = propertySource.getProperty("xinyi.qiangdansync.url");
                if (null != qiangdanSyncUrl && !"".equals(qiangdanSyncUrl)) {
                    XinYiSettings.setQiangdanSyncUrl((String) propertySource.getProperty("xinyi.qiangdansync.url"));
                }
                //大文件上传路径
                ResumableJsUploadSetting.setLocalPath(propertySource.getProperty("file.upload.local").toString());
                ResumableJsUploadSetting.setRemotePath(propertySource.getProperty("file.upload.remote").toString());
                
                Object ytFacesUrl = propertySource.getProperty("xinghuo.ytfaces.url");
                if (ytFacesUrl!=null) {
                    XinghuoSettings.setYtFacesUrl(ytFacesUrl.toString().trim());
                }
                Object ytSureFaceUrl = propertySource.getProperty("xinghuo.ytsureface.url");
                if (ytSureFaceUrl!=null) {
                    XinghuoSettings.setYtSureFaceUrl(ytSureFaceUrl.toString().trim());
                }
                
            } catch (IOException e) {
                LOG.error("ERROR during environment properties setup - TRYING TO LOAD: " + profileName, e);

                // Okay to silently fail here, as we might have profiles that do
                // not have properties files (like dev1, dev2, etc)
            }
        }
    }

    /**
     * Returns either the ActiveProfiles, or if empty, then the DefaultProfiles
     * from Spring
     */
    protected String[] getActiveProfiles(ConfigurableEnvironment env) {
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length > 0) {
            LOG.info("Using registered Spring Active Profiles: {}", StringUtils.join(activeProfiles, ", "));
            return activeProfiles;
        }

        String[] defaultProfiles = env.getDefaultProfiles();
        LOG.info("No Active Profiles found, using Spring Default Profiles: {}", StringUtils.join(defaultProfiles, ", "));
        return defaultProfiles;
    }

}