package intellif.service.impl;

import intellif.database.dao.CommonDao;
import intellif.database.dao.SearchLogDao;
import intellif.database.entity.SearchLogInfo;
import intellif.service.SearchLogServiceItf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchLogServiceImpl extends AbstractCommonServiceImpl<SearchLogInfo> implements SearchLogServiceItf<SearchLogInfo> {

    private static Logger LOG = LogManager.getLogger(SearchLogServiceImpl.class);

    @Autowired
    private SearchLogDao searchLogDao;
    @Override
    public CommonDao getDao() {
        return searchLogDao;
    }
    

}
