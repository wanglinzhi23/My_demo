package intellif.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.dto.JsonObject;
import intellif.database.entity.UserInfo;

/**
 * 
 * @author yktang Created by V1.1.2
 *
 */

@Aspect
@Component
public class RoleValidationAspect {

	@Autowired
	private UserDao userRepository;
	@Autowired
	private RoleDao roleRepository;

	// 只有超级管理员和管理账户可以进入DataQueryController
//	@Around("execution(* intellif.controllers.DataQueryController.*(..))")
	public Object aroundDataQuery(ProceedingJoinPoint joinPoint) throws Throwable {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userid = Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
		UserInfo userinfo = userRepository.findOne(userid);
		String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
		if (roleName.equals("SUPER_ADMIN") || roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
			return joinPoint.proceed(joinPoint.getArgs());
		} else {
			return new JsonObject("对不起，您没有权限查询！", 1001);
		}
	}

	// 只有超级管理员，管理账户和操作账户可以调用@Around里的方法
//	@Around("execution(* intellif.controllers.AlarmController.createZipResult(..))")
	public Object filterUserOrHigher(ProceedingJoinPoint joinPoint) throws Throwable {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userid = Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
		UserInfo userinfo = userRepository.findOne(userid);
		String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
		if (roleName.equals("SUPER_ADMIN") || roleName.equals("MIDDLE_ADMIN") || roleName.equals("ADMIN") || roleName.equals("USER")) {
			return joinPoint.proceed(joinPoint.getArgs());
		} else {
			return new JsonObject("对不起，您没有权限查询！", 1001);
		}
	}
}
