package intellif.service.impl;

import intellif.database.dao.AreaAndBlackDetailDao;
import intellif.database.dao.CommonDao;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.service.AreaAndBlackDetailServiceItf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaAndBlackDetailServiceImpl extends AbstractCommonServiceImpl<AreaAndBlackDetail> implements AreaAndBlackDetailServiceItf<AreaAndBlackDetail> {

    private static Logger LOG = LogManager.getLogger(AreaAndBlackDetailServiceImpl.class);
    

    @Autowired
    private AreaAndBlackDetailDao areaAndBlackDetailDao;
  


    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return areaAndBlackDetailDao;
    }
  
}
