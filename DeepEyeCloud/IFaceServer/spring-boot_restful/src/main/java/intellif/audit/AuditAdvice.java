package intellif.audit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;

/**
 * Created by yangboz on 12/2/15.
 */
@Aspect
public class AuditAdvice {
    private static Logger LOG = LogManager.getLogger(AuditAdvice.class);
    @Autowired
    private AuditServiceItf auditService;

    @Around("@annotation(auditAnnotation)")
    public Object
    audit(ProceedingJoinPoint point, Auditable auditAnnotation) throws Throwable {
        LOG.info("Auditing... point:" + point.toString() + ",auditable:" + auditAnnotation.toString());
        boolean ok = false;
        try {
            Object o = point.proceed();
            ok = true;
            return o;
        } finally {
            if (ok) {
                //TODO:VO with toJSONString and convert it back with AuditableItf.
//                ObjectMapper mapper = new ObjectMapper();
//                AuditableItf auditableItf = mapper.readValue(point.getArgs(), InfoBase.class);
                auditService.audit(MessageFormat.format(auditAnnotation.message(), point.getArgs()), null);
            }
        }
    }
}
