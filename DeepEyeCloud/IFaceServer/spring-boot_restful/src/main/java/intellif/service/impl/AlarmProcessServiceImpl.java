package intellif.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import intellif.core.tree.itf.TreeNode;
import intellif.database.dao.AlarmProcessDao;
import intellif.database.dao.CommonDao;
import intellif.database.entity.AlarmProcess;
import intellif.database.entity.CameraInfo;
import intellif.dto.AlarmProcessDetail;
import intellif.dto.QueryInfoDto;
import intellif.service.AlarmProcessServiceItf;
import intellif.zoneauthorize.conf.ZoneConfig;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmProcessServiceImpl  extends AbstractCommonServiceImpl<AlarmProcess> implements AlarmProcessServiceItf<AlarmProcess> {

    private static Logger LOG = LogManager.getLogger(AlarmProcessServiceImpl.class);

    @PersistenceContext
    EntityManager entityManager;
    //
    @Autowired
    AlarmProcessDao<AlarmProcess> alarmProcessDao;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return alarmProcessDao;
    }

    @Override
    public List<AlarmProcessDetail> findProcessedAlarmByParams(QueryInfoDto queryInfoDto) {
        String ids = queryInfoDto.getIds();
        List<TreeNode> cameraList = new ArrayList<TreeNode>();
        if (!ids.isEmpty()) {
            for (String idStr : ids.split(",")) {
                long id = Long.parseLong(idStr);
                List<TreeNode> nodeList = zoneAuthorizeService.offspring(
                ZoneConfig.getNodeTypeMap().get(queryInfoDto.getNodeType()), id, CameraInfo.class);       
                cameraList.addAll(nodeList);
            }
        }
        List<Long> cameraIdList = cameraList.stream().map(m -> m.getId()).collect(Collectors.toList());
        String cIds = StringUtils.join(cameraIdList, ",");
        if(StringUtils.isNotBlank(cIds)){
            return alarmProcessDao.findProcessedAlarmByParams(cIds, queryInfoDto.getPage(), queryInfoDto.getPageSize());
        }else{
            return null;
        }
    }

   

}