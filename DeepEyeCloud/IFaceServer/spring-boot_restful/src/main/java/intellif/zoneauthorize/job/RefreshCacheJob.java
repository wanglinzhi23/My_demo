package intellif.zoneauthorize.job;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.core.tree.itf.TreeNode;
import intellif.zoneauthorize.common.LocalCache;
import intellif.zoneauthorize.service.ZoneAuthorizeCacheItf;

@Component
public class RefreshCacheJob {

    @Autowired
    private ZoneAuthorizeCacheItf zoneAuthorizeCache;
    private static Map<Class<? extends TreeNode>, Long> tempMap;
    private static Logger LOG = LogManager.getLogger(RefreshCacheJob.class);

    @Scheduled(fixedDelay = 60000L)
    public void run() {
        try {
            if (null == LocalCache.tree) {
                // 启动的时候更新一次树和版本
                //zoneAuthorizeCache.tree();
                //zoneAuthorizeCache.updateTree();
                LocalCache.tree = zoneAuthorizeCache.tree();
                zoneAuthorizeCache.versionMap();
                zoneAuthorizeCache.updateVersionMap(zoneAuthorizeCache.versionMapWithoutCache());
            }
            boolean switchFromCache = zoneAuthorizeCache.zoneAuthorizeSwitch();
            boolean switchFromDB = zoneAuthorizeCache.zoneAuthorizeSwitchWithoutCache();
            if (switchFromCache != switchFromDB) {
                LOG.warn("xxxxxxxx refresh key");
                zoneAuthorizeCache.refreshKey();
            }

            //Map<Class<? extends TreeNode>, Long> versionMap = zoneAuthorizeCache.versionMap();
            Map<Class<? extends TreeNode>, Long> versionMapWithoutCache = zoneAuthorizeCache.versionMapWithoutCache();
            if (!versionMapWithoutCache.equals(tempMap)) {
                tempMap = versionMapWithoutCache;
                LOG.warn("xxxxxxxx clear zone cache and refresh key");
                zoneAuthorizeCache.updateVersionMap(versionMapWithoutCache);
                //zoneAuthorizeCache.updateTree();
                LocalCache.tree = zoneAuthorizeCache.tree();
                zoneAuthorizeCache.refreshKey();
            }
        } catch (Throwable e) {
            LOG.error("catch exception: ", e);
            LOG.warn("xxxxxxxx clear zone cache and refresh key");
            try {
                //zoneAuthorizeCache.tree();
                zoneAuthorizeCache.versionMap();
                Map<Class<? extends TreeNode>, Long> versionMapWithoutCache = zoneAuthorizeCache.versionMapWithoutCache();
                zoneAuthorizeCache.updateVersionMap(versionMapWithoutCache);
                //zoneAuthorizeCache.updateTree();
                LocalCache.tree = zoneAuthorizeCache.tree();
                zoneAuthorizeCache.refreshKey();
            } catch (Throwable e1) {
                LOG.error("catch exception: ", e1);
            }
        }
    }
}
