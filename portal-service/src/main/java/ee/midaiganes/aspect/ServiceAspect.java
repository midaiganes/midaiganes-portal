package ee.midaiganes.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.util.TimeProviderUtil;

@Aspect(value = "perthis(within(@" + ServiceAspect.ANNOTATION + " *))")
public class ServiceAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceAspect.class);
    public static final String ANNOTATION = "ee.midaiganes.aspect.Service";
    private String serviceInterfaceSimpleName;

    @After("within(@" + ServiceAspect.ANNOTATION + " *) && execution((@" + ServiceAspect.ANNOTATION + " *).new(..)) && this(myObject)")
    public void serviceCreation(Object myObject) {
        @SuppressWarnings("unchecked")
        Class<Object> serviceInterface = (Class<Object>) myObject.getClass().getAnnotation(Service.class).serviceInterface();
        BeanRepositoryUtil.register(serviceInterface, myObject);
        this.serviceInterfaceSimpleName = serviceInterface.getSimpleName();
    }

    @Around("execution(public * ((@" + ServiceAspect.ANNOTATION + " *)+).*(..)) && within(@" + ServiceAspect.ANNOTATION + " *)")
    public Object aroundService(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        String method = this.serviceInterfaceSimpleName + '.' + thisJoinPoint.getSignature().getName() + " took ";
        StringBuilder sb = new StringBuilder(256);
        toJson(sb, thisJoinPoint.getArgs()[0]);

        long start = TimeProviderUtil.currentTimeMillis();
        Object result = null;
        try {
            result = thisJoinPoint.proceed();
            return result;
        } catch (Throwable t) {
            result = t;
            throw t;
        } finally {
            sb.insert(0, method + (TimeProviderUtil.currentTimeMillis() - start) + "ms\n");
            sb.append('\n');
            toJson(sb, result);
            log.info(sb.toString());
        }
    }

    private void toJson(StringBuilder sb, Object o) {
        if (o instanceof Throwable) {
            Throwable t = (Throwable) o;
            sb.append(t.getClass()).append(": ").append(t.getMessage());
            StackTraceElement[] stes = t.getStackTrace();
            if (stes != null) {
                int i = 0;
                for (; i < 5 && i < stes.length; i++) {
                    sb.append("\n\t").append(stes[i].toString());
                }
                if (i == 5 && stes.length > 5) {
                    sb.append("\n\t...");
                }
            }
        } else {
            sb.append(o.getClass().getSimpleName()).append(" ");
            new Gson().toJson(o, sb);
        }
    }
}
