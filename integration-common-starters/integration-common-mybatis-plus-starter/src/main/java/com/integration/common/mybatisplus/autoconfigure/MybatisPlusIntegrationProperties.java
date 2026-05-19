package com.integration.common.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyBatis-Plus 集成配置项，前缀 {@code integration.mybatis-plus}。
 */
@ConfigurationProperties(prefix = "integration.mybatis-plus")
public class MybatisPlusIntegrationProperties {

    /**
     * 是否注册分页拦截器。
     */
    private boolean paginationEnabled = true;

    /**
     * 分页方言对应的数据库类型。
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * 页码溢出时是否按边界页处理。
     */
    private boolean overflow = false;

    public boolean isPaginationEnabled() {
        return paginationEnabled;
    }

    public void setPaginationEnabled(boolean paginationEnabled) {
        this.paginationEnabled = paginationEnabled;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public boolean isOverflow() {
        return overflow;
    }

    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }
}
