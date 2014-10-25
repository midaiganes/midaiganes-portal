package ee.midaiganes.beans;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.google.inject.Provider;
import com.mysql.jdbc.Driver;

public class PortalDataSourceProvider implements Provider<DataSource> {
    private final String jdbcUrl;
    private final String jdbcUsername;
    private final String jdbcPassword;

    @Inject
    public PortalDataSourceProvider(@Named("jdbc.url") String jdbcUrl, @Named("jdbc.username") String jdbcUsername, @Named("jdbc.password") String jdbcPassword) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }

    @Override
    public DataSource get() {
        final org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(Driver.class.getName());
        ds.setUrl(jdbcUrl);
        ds.setUsername(jdbcUsername);
        ds.setPassword(jdbcPassword);
        ds.setMaxActive(66);// TODO
        ds.setMaxIdle(66);// TODO
        ds.setMinIdle(1);
        ds.setInitialSize(1);
        ds.setMaxWait(1000);
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(true);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(5000);
        ds.setValidationInterval(30000);
        ds.setValidationQuery("SELECT 1");
        ds.setValidationQueryTimeout(1);
        ds.setJmxEnabled(true);
        ds.setLogAbandoned(true);
        ds.setSuspectTimeout(5);
        return new LazyConnectionDataSourceProxy(ds);
    }
}
