package ee.midaiganes.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

public class DatabaseModule extends AbstractModule {
    private static class TransactionInterceptorProvider implements Provider<TransactionInterceptor> {
        private final DataSource dataSource;

        @Inject
        public TransactionInterceptorProvider(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public TransactionInterceptor get() {
            return new TransactionInterceptor(new DataSourceTransactionManager(dataSource), new AnnotationTransactionAttributeSource());
        }
    }

    private static class MethodInterceptorProxy implements MethodInterceptor {
        private final Provider<? extends MethodInterceptor> provider;

        private MethodInterceptorProxy(Provider<? extends MethodInterceptor> provider) {
            this.provider = provider;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return this.provider.get().invoke(invocation);
        }
    }

    private static class JdbcTemplateProvider implements Provider<JdbcTemplate> {
        private final DataSource dataSource;

        @Inject
        public JdbcTemplateProvider(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public JdbcTemplate get() {
            return new JdbcTemplate(dataSource);
        }
    }

    @Override
    protected void configure() {
        bind(DataSource.class).toProvider(PortalDataSourceProvider.class).in(Singleton.class);

        bind(TransactionInterceptor.class).toProvider(TransactionInterceptorProvider.class).in(Singleton.class);
        MethodInterceptor transactionInterceptor = new MethodInterceptorProxy(getProvider(TransactionInterceptor.class));
        bindInterceptor(Matchers.not(Matchers.annotatedWith(Transactional.class)), new AbstractMatcher<Method>() {
            @Override
            public boolean matches(Method t) {
                return Modifier.isPublic(t.getModifiers()) && t.isAnnotationPresent(Transactional.class);
            }
        }, transactionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Transactional.class), new AbstractMatcher<Method>() {
            @Override
            public boolean matches(Method t) {
                return Modifier.isPublic(t.getModifiers());
            }
        }, transactionInterceptor);

        bind(JdbcTemplate.class).toProvider(JdbcTemplateProvider.class).in(Singleton.class);
    }
}
