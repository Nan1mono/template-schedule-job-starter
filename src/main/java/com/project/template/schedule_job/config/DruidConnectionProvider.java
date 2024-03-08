package com.project.template.schedule_job.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.quartz.SchedulerException;
import org.quartz.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
@Setter
public class DruidConnectionProvider implements ConnectionProvider {

    //JDBC驱动
    private String driver;
    //JDBC连接串
    private String url;
    //数据库用户名
    private String user;
    //数据库用户密码
    private String password;
    //数据库最大连接数
    private int maxConnections;
    //数据库SQL查询每次连接返回执行到连接池，以确保它仍然是有效的。
    private String validationQuery;

    private boolean validateOnCheckout;

    private int idleConnectionValidationSeconds;

    private String maxCachedStatementsPerConnection;

    private String discardIdleConnectionsSeconds;

    private static final int DEFAULT_DB_MAX_CONNECTIONS = 10;

    private static final int DEFAULT_DB_MAX_CACHED_STATEMENTS_PER_CONNECTION = 120;

    private DruidDataSource datasource;

    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    public void shutdown() throws SQLException {
        datasource.close();
    }

    public void initialize() throws SQLException {
        if (this.url == null) {
            throw new SQLException("DBPool could not be created: DB URL cannot be null");
        }

        if (this.driver == null) {
            throw new SQLException("DBPool driver could not be created: DB driver class name cannot be null!");
        }

        if (this.maxConnections < 0) {
            throw new SQLException("DBPool maxConnectins could not be created: Max connections must be greater than zero!");
        }

        datasource = new DruidDataSource();
        try {
            datasource.setDriverClassName(this.driver);
        } catch (Exception e) {
            try {
                throw new SchedulerException("Problem setting driver class name on datasource: " + e.getMessage(), e);
            } catch (SchedulerException ignored) {
            }
        }

        datasource.setUrl(this.url);
        datasource.setUsername(this.user);
        datasource.setPassword(this.password);
        datasource.setMaxActive(this.maxConnections);
        datasource.setMinIdle(1);
        datasource.setMaxWait(0);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(DEFAULT_DB_MAX_CONNECTIONS);

        if (this.validationQuery != null) {
            datasource.setValidationQuery(this.validationQuery);
            if (!this.validateOnCheckout)
                datasource.setTestOnReturn(true);
            else
                datasource.setTestOnBorrow(true);
            datasource.setValidationQueryTimeout(this.idleConnectionValidationSeconds);
        }
    }
}
