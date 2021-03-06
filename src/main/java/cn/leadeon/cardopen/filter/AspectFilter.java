package cn.leadeon.cardopen.filter;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

//@Aspect
//@Component
@Slf4j
public class AspectFilter {

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    private Object result;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void AspectMethod() {
    }

    @Around("AspectMethod()")
    public Object handle(ProceedingJoinPoint proceedingJoinPoint) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            startTime.set(System.currentTimeMillis());
            String exception = null;
            try {
                result = proceedingJoinPoint.proceed();
            } catch (Throwable e) {
                exception = e.getMessage();
            }
            if (exception == null) {
                log.info("--------------------------------------------------------");
                log.info("URL : " + request.getRequestURL().toString());
                log.info("ARGS : " + Arrays.toString(proceedingJoinPoint.getArgs()));
                log.info("RESPONSE : " + result.toString());
                log.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
                log.info("--------------------------------------------------------");
            }
        }
        return result;
    }

}
