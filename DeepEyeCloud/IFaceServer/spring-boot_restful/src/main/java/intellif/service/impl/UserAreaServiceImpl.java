/**
 *
 */
package intellif.service.impl;

import intellif.database.dao.CommonDao;
import intellif.database.dao.UserAreaDao;
import intellif.database.entity.UserArea;
import intellif.service.UserAreaServiceItf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class BlackDetailServiceImpl.
 *
 * @author yangboz
 */
@Service
public class UserAreaServiceImpl extends AbstractCommonServiceImpl<UserArea> implements UserAreaServiceItf<UserArea> {

    private static Logger LOG = LogManager.getLogger(UserAreaServiceImpl.class);

    @Autowired
    private UserAreaDao userAreaDao;


    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return userAreaDao;
    }

  

}
