package ee.midaiganes.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ee.midaiganes.util.TimeProviderUtil;

public class ServiceMethodInterceptor implements MethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ServiceMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String method = invocation.getMethod().getDeclaringClass().getAnnotation(Service.class).serviceInterface().getSimpleName() + '.' + invocation.getMethod().getName()
                + " took ";
        StringBuilder sb = new StringBuilder(256);
        toJson(sb, invocation.getArguments()[0]);

        long start = TimeProviderUtil.currentTimeMillis();
        Object result = null;
        try {
            result = invocation.proceed();
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