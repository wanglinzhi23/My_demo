package intellif.oauth;

import intellif.aspects.InterfaceAspect;
import intellif.audit.EntityAuditListener;
import intellif.consts.GlobalConsts;
import intellif.dao.AllowIpRangeDao;
import intellif.dao.ApiResourceDao;
import intellif.dao.AuditLogDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.UserApiLimitDao;
import intellif.dao.UserDao;
import intellif.lire.UserOnlineThread;
import intellif.utils.CurUserInfoUtil;
import intellif.database.entity.AllowIpRange;
import intellif.database.entity.ApiResourceInfo;
import intellif.database.entity.OnLineUserInfo;
import intellif.database.entity.UserApiLimitInfo;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;





import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**
 * inject accessing control logic for 3rd client into
 * {@link OAuth2AuthenticationProcessingFilter}, return 403 if no privilege to access
 * resource (RESTFUL API)
 * 
 * Note: this class can't put into spring container for only allow one instance 
 * of OAuth2AuthenticationManager
 * @author simon_zhang
 *
 */

public class APIAccessControlAuthenticator extends OAuth2AuthenticationManager implements Filter {

	private static Log logger = LogFactory.getLog(APIAccessControlAuthenticator.class);
	
	private static Logger LOG = LogManager.getLogger(APIAccessControlAuthenticator.class);
	private static Logger log = LogManager.getLogger(InterfaceAspect.class);
	
	// SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
	SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");

	private UserDao userRepository;
	
	private ApiResourceDao resourceRepository;
	
	
	private PoliceStationDao policestationDao;
	
   	private  AuditLogDao auditLogRepository;
	
	
	private AbstractHandlerMethodMapping<RequestMappingInfo> apiMapping;
	
	private UserApiLimitDao limitiRepository;
	
	private AllowIpRangeDao ipRangeDao;
	
	
	
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;		
		String requestURI = httpRequest.getRequestURI(); //ex. /api/intellif/task

		if (!checkIpAllow(httpRequest)) {
			httpResponse.sendError(405, "no privilege to this browser ip:" + httpRequest.getRemoteAddr());
			return;
		}
		
		for (String forbidApi : OAuth2Settings.getBlackList()) {
		    if (!forbidApi.equals("") && requestURI.endsWith(forbidApi) ) {
		        httpResponse.sendError(403, "API is in balck list:" + requestURI);
		        return;
		    }
		}

		//7.19   用来捕捉每个用户的登录时间   及详细信息
		if(SecurityContextHolder.getContext().getAuthentication()!=null){/*

			if (CurUserInfoUtil.getUserInfo() != null && CurUserInfoUtil.getUserInfo().getPoliceStationId() == null)
				httpResponse.sendError(1001, "没有访问权限，您的账号当前不属于任何单位。");

	      	OnLineUserInfo onlineuser=UserOnlineThread.onlineusersinfo.get(SecurityContextHolder.getContext().getAuthentication().getName());

		  if(onlineuser==null){
			  
    	    onlineuser=new OnLineUserInfo();
    	    
    	     UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 String ip=CurUserInfoUtil.getIP();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String jobtitle=userinfo.getPost();
			 String stationname = "";
			 if (policeStationId != null) {
				 stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 }
			 String userName=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String owner =userinfo.getLogin();
			
	    	onlineuser.setId(uid);  //id就等于user的id
	    	onlineuser.setIp(ip);
	    	onlineuser.setPost(jobtitle);
	    	if (policeStationId != null)
				onlineuser.setPoliceStationId(policeStationId);
	    	onlineuser.setAccounttype(accounttype);
	    	onlineuser.setName(userName);
        	onlineuser.setTime(bartDateFormat.format(new Date()));
        	onlineuser.setOwner(owner);

        	
        	//记录登录的操作日志表
        	LOG.info("EntityAuditListener->touchForCreate->Auditable userinfo!!!");
   
			AuditLogInfo log = new AuditLogInfo();
			log.setOwner(owner);
			log.setOperation("log in");
			log.setObject("");  //没有表咯
			log.setObjectId(uid);
			log.setObject_status(11);  
	    	log.setTitle(log.getOwner() + "登录啦,"+userName+","+stationname+","+accounttype+","+ip+","+jobtitle); //登陆这一种操作 我还得把ip和post以及stationid记一下 因为今日登陆列表需要获取
			log.setMessage(accounttype+owner+"登录了系统");
			auditLogRepository.save(log);
        	
        	
    	    
		  }
		  
		    
        	
		  UserOnlineThread.onlineusersinfo.put(SecurityContextHolder.getContext().getAuthentication().getName(),onlineuser);
		  UserOnlineThread.onlineusers.put(SecurityContextHolder.getContext().getAuthentication().getName(),(new Date()).getTime());
		  UserOnlineThread.visitedusers.put(SecurityContextHolder.getContext().getAuthentication().getName(),(new Date()).getTime()+","+false);

		*/}
		
		
		
		
		for (String allowRequest : OAuth2Settings.getWhiteList()) {
		    if (requestURI.contains(allowRequest)) {
		        filterChain.doFilter(httpRequest, response);
		    	
		        return;
		    }
		}

		String method = httpRequest.getMethod(); //ex. GET POST PUT DELETE
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		
		
		
		if (authentication == null) {
			if (requestURI != null && !requestURI.startsWith(GlobalConsts.RESOURCE_ID_BASE)) {
				filterChain.doFilter(httpRequest, response);
				
				return;
			}
			
			httpResponse.sendError(401, "Please login firstly then get token to access restful api!");
			
			return;
		}
		
		String userName = authentication.getName(); //ex. admin 
	
	
		

        try {
            apiMapping.getHandler(httpRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String apiWithPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    if (apiWithPattern == null) {
	        httpResponse.sendError(404, "not found for api: " + requestURI);
	    }
		
		boolean authorizedUser = authorizedUser(userName, apiWithPattern, method);
		if (!authorizedUser) {
			//pass security checking but failed for access control
			httpResponse.sendError(403, "no privilege to access api:" + requestURI);
		} else {
			filterChain.doFilter(httpRequest, response);
			log.info("getServletPath：{}",httpRequest.getRequestURL()); 
			
		}
	}

	private boolean checkIpAllow(HttpServletRequest httpRequest) {
		// is client behind a proxy
		String clinetIp = httpRequest.getHeader("X-FORWARDED-FOR");
		if (clinetIp == null) {
			clinetIp = httpRequest.getRemoteAddr();
		}
		String url = httpRequest.getRequestURL().toString();
        if(url.indexOf("check/result/") >=0 ){
            LOG.info("red check from jinxin,url:"+url);
            return true;
        }
		List<AllowIpRange> ipRanges = ipRangeDao.findMatchRanges(clinetIp);
		
		if (CollectionUtils.isEmpty(ipRanges)) {
		    LOG.info("filter refuse ip:"+clinetIp);
			return false;
		}
		
		return true;
	}
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info(filterConfig);
	}

	/**
	 * check restful resource with userName, api, method, then return error
	 * message when no accessing privilege
	 * 
	 * TODO use cache to speed up!!!!!
	 * 
	 * @param userName
	 * @param restfulApi
	 * @param httpMethod
	 * @return
	 */
	protected boolean authorizedUser(String userName, String restfulApi, String httpMethod) {
		
		UserInfo userInfo = userRepository.findByLogin(userName);

		Long roleId = userInfo.getRoleId();
		
		if(roleId <= 0) {
			return false;
		}
		
		//can find a fulfilled api resource ?
		ApiResourceInfo resource = resourceRepository.findResources(roleId, restfulApi, httpMethod);
		if (resource == null) {
			return false;
		}
		
		UserApiLimitInfo limitInfo = limitiRepository.findApiLimitation(userInfo.getId(), resource.getId());
		if (limitInfo == null) {
			return true; //no api accessing times limitation for this user
		}
		
		boolean fulfillLimit =  limitInfo.getLimitMethod().apiLimitHanler(limitInfo);
		
		if (fulfillLimit) {
			limitInfo.setCallCount(limitInfo.getCallCount() + 1);
		} else {
			limitInfo.setDenyCount(limitInfo.getDenyCount() + 1);
		}
		
		limitiRepository.save(limitInfo);
		
		return fulfillLimit;
	}

	public void setUserRepository(UserDao userRepository) {
		this.userRepository = userRepository;
	}

	public void setResourceRepository(ApiResourceDao resourceRepository) {
		this.resourceRepository = resourceRepository;
	}
	
	public void setPoliceStationDao(PoliceStationDao policestationDao) {
		this.policestationDao = policestationDao;
	}

	public void setAuditLogRepository(AuditLogDao auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}
	
	
	public void setApiMapping(AbstractHandlerMethodMapping<RequestMappingInfo> apiMapping) {
		this.apiMapping = apiMapping;
	}
	
	public void setLimitiRepository(UserApiLimitDao limitiRepository) {
		this.limitiRepository = limitiRepository;
	}
	
	public void setIpRangeDao(AllowIpRangeDao ipRangeDao) {
	    this.ipRangeDao = ipRangeDao;
    }
}