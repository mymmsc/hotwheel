package org.hotwheel.ibatis.datasource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.hotwheel.assembly.Api;

import java.util.Map;
import java.util.Properties;

/**
 * Created by wangfeng on 2016/11/1.
 * @version 5.5.0
 * @since 1.0
 */
public class HotWheelDataSourceFactory extends UnpooledDataSourceFactory {
    protected org.apache.tomcat.jdbc.pool.DataSource dataSource;

    public HotWheelDataSourceFactory () {
        this.dataSource = new DataSource();
    }

    @Override
    public void setProperties(Properties properties) {
        PoolProperties poolProperties = new PoolProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            Api.setValue(poolProperties, key, value);
        }
        //poolProperties.setDbProperties(properties);
        dataSource.setPoolProperties(poolProperties);
    }

    @Override
    public javax.sql.DataSource getDataSource() {
        return this.dataSource;
    }
}
