package intellif.oauth;

import intellif.dao.UserApiLimitDao;
import intellif.database.entity.UserApiLimitInfo;

import java.util.Date;

/**
 * use for how to handle accessing limitation, now only one type TIME_INTERVAL 
 * Also, it's a strategy pattern for logic handling access times control
 * 
 * 
 * @author simon_zhang
 *
 */
public enum AccessLimitMethod implements ApiLimitMethodHanler{
	TIME_INTERVAL {

		@Override
		public boolean apiLimitHanler(Object payload) {
		    UserApiLimitInfo limitInfo = (UserApiLimitInfo) payload;
			long currentTime = (new Date()).getTime();
			long lastCallTime = limitInfo.getUpdated().getTime();
			//only allow to call when exceed time interval
			return (currentTime - lastCallTime)/1000 > OAuth2Settings.getAccessTimeInterval();
		};
		
	}, WTHITE_LIST {

        @Override
        public boolean apiLimitHanler(Object limitInfo) {
            // TODO Auto-generated method stub
            return false;
        };
	
	}, BLACK_LIST {

        @Override
        public boolean apiLimitHanler(Object limitInfo) {
            // TODO Auto-generated method stub
            return false;
        }
	    
	}

}
