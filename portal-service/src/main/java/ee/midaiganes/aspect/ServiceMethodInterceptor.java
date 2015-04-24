package ee.midaiganes.aspect;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ee.midaiganes.util.TimeProviderUtil;

public class ServiceMethodInterceptor implements MethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ServiceMethodInterceptor.class);
    private final Gson gson;

    public ServiceMethodInterceptor() {
        this.gson = new Gson();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String method = getServiceAndMethodPart(invocation);
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

    private String getServiceAndMethodPart(MethodInvocation invocation) {
        return getServiceAndMethodPartFromMethod(invocation.getMethod());
    }

    private String getServiceAndMethodPartFromMethod(Method method) {
        return getServiceSimpleName(method) + '.' + method.getName() + " took ";
    }

    private String getServiceSimpleName(Method method) {
        return method.getDeclaringClass().getAnnotation(Service.class).service().getSimpleName();
    }

    private void toJson(StringBuilder sb, Object o) {
        if (o instanceof Throwable) {
            appendThrowable((Throwable) o, sb);
        } else {
            appendRegularObject(sb, o);
        }
    }

    private void appendRegularObject(StringBuilder sb, Object o) {
        sb.append(getObjectClassSimpleName(o)).append(" ");
        gson.toJson(o, sb);
    }

    private String getObjectClassSimpleName(Object o) {
        return o.getClass().getSimpleName();
    }

    private void appendThrowable(Throwable t, StringBuilder sb) {
        sb.append(t.getClass()).append(": ").append(t.getMessage());
        StackTraceElement[] stes = t.getStackTrace();
        if (stes != null) {
            appendStackTraceElements(stes, sb);
        }
    }

    private void appendStackTraceElements(StackTraceElement[] stes, StringBuilder sb) {
        int i = 0;
        for (; i < 5 && i < stes.length; i++) {
            sb.append("\n\t").append(stes[i].toString());
        }
        if (i == 5 && stes.length > 5) {
            sb.append("\n\t...");
        }
    }
}
