package intellif.aspects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import intellif.audit.AuditServiceItf;
import intellif.dto.JsonObject;
import intellif.utils.CurUserInfoUtil;

@Aspect
@Component
public class InterfaceAspect {

	private static Logger LOG = LogManager.getLogger(InterfaceAspect.class);

	@Autowired
	private AuditServiceItf auditServiceitf;

	// @Pointcut("execution(* intellif.controllers..*(..)) and
	// @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	@Pointcut("execution(intellif.dto.JsonObject intellif..*(..)) and @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void pointCut() {

	}

	@Around("pointCut()") // 指定拦截器规则；也可以直接把“execution(* com.xjj.........)”写进这里
	public Object Interceptor(ProceedingJoinPoint pjp) {

		// 在日志中记录每个接口被每个用户调用情况
		long beginTime = System.currentTimeMillis();
		Object result = null;

		MethodSignature signature = (MethodSignature) pjp.getSignature();

		if (signature.toShortString().contains("UserLonggangController")) {
			LOG.info("用户调用接口请求记录 -- -- interfacename ：{}", "UserLonggangController");
			try {
				result = pjp.proceed();
			} catch (Throwable e) {
				LOG.error(pjp.getSignature() + ", catch exception: ", e);
			}

			long costMs = System.currentTimeMillis() - beginTime;
			LOG.info("{}interface call time，：{}ms", signature.toShortString(), costMs);

			return result;
		}
        try{
		String user = CurUserInfoUtil.getUserInfo().getLogin();
		LOG.info("用户调用接口请求记录 --- useraccount：{} -- interfacename ：{}", user, signature.toShortString());
        }catch(ClassCastException e){
        System.out.println("切面记录用户转化异常");	
        }

		try {
			result = pjp.proceed();
		} catch (NullPointerException | IllegalArgumentException e) {
			LOG.error(pjp.getSignature() + ", catch exception: ", e);
			String msg = StringUtils.isBlank(e.getMessage()) ? "系统繁忙，请稍候再试！" : e.getMessage();
			int code = 1001;
			try {
				if (msg.contains("---")) {
					String[] s = msg.split("---");
					code = Integer.parseInt(s[1]);
					msg = s[0];
				}
			} catch (Throwable t) {
				code = 1001;
			}
			return new JsonObject(msg, code);
		} catch (Throwable e) {
			LOG.error(pjp.getSignature() + ", catch exception: ", e);
			return new JsonObject("系统繁忙，请稍候再试！", 1001);
		}

		long costMs = System.currentTimeMillis() - beginTime;
		LOG.info("{}interface call time，：{}ms", signature.toShortString(), costMs);

		return result;
	}

}
