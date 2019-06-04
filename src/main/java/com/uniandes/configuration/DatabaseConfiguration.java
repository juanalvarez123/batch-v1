package com.uniandes.configuration;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;

@Order(2)
@Configuration
@EnableAutoConfiguration
@PropertySource({"classpath:datasource.properties"})
public class DatabaseConfiguration {

    @Value("${custom.datasource.tomcat.url}")
    private String url;

    @Value("${custom.datasource.tomcat.username}")
    private String username;

    @Value("${custom.datasource.tomcat.password}")
    private String password;

    @Value("${custom.datasource.tomcat.driverclassName}")
    private String className;

    @Value("${custom.datasource.tomcat.maxWait}")
    private String maxWait;

    @Value("${custom.datasource.tomcat.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${custom.datasource.tomcat.initial.size}")
    private String initialSize;

    @Value("${custom.datasource.tomcat.maxActive}")
    private String maxActive;

    @Value("${custom.datasource.tomcat.validation.interval}")
    private String validationInterval;

    @Value("${custom.datasource.tomcat.abandoned.timeout}")
    private String abandonedTimeout;

    @Value("${custom.datasource.tomcat.minIdle}")
    private String minIdle;

    @Value("${custom.datasource.tomcat.maxIdle}")
    private String maxIdle;

    @Value("${custom.datasource.tomcat.timebetweeneviction}")
    private String timebetweeneviction;

    @Value("${custom.datasource.tomcat.minevictableidletime}")
    private String minevictableidletime;

    @Bean
    public DataSource dataSource() {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUrl(url);
        poolProperties.setUsername(username);
        poolProperties.setPassword(password);
        poolProperties.setDriverClassName(className);
        poolProperties.setJmxEnabled(true);
        poolProperties.setTestWhileIdle(false);
        poolProperties.setTestOnBorrow(testOnBorrow);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setTestOnReturn(false);
        poolProperties.setValidationInterval(Integer.valueOf(validationInterval));
        poolProperties.setTimeBetweenEvictionRunsMillis(Integer.valueOf(timebetweeneviction));
        poolProperties.setMaxActive(Integer.valueOf(maxActive));
        poolProperties.setInitialSize(Integer.valueOf(initialSize));
        poolProperties.setMaxWait(Integer.valueOf(maxWait));
        poolProperties.setRemoveAbandonedTimeout(Integer.valueOf(abandonedTimeout));
        poolProperties.setMinEvictableIdleTimeMillis(Integer.valueOf(minevictableidletime));
        poolProperties.setMinIdle(Integer.valueOf(minIdle));
        poolProperties.setMaxIdle(Integer.valueOf(maxIdle));
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
            + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        return new DataSource(poolProperties);
    }
}
