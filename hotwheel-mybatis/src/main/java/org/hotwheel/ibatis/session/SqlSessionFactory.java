package org.hotwheel.ibatis.session;

import org.apache.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangfeng on 2016/11/6.
 * @since 2.0.1
 */
public class SqlSessionFactory {
    private Map<String, Configuration> mapConfiguration;
    private Map<Class<?>, Configuration> mapSqlMapper;

    public SqlSessionFactory() {
        //
    }

    public void addDataSource(String name, Configuration configuration) {
        if (mapConfiguration == null) {
            mapConfiguration = new HashMap<>();
        }
        mapConfiguration.put(name, configuration);
    }

    public Configuration getDataSource(String name) {
        Configuration configuration = null;
        if (mapConfiguration != null) {
            configuration = mapConfiguration.get(name);
        }
        return configuration;
    }

    public void addMapper(Class<?> clazz, Configuration configuration) {
        if(mapSqlMapper == null) {
            mapSqlMapper = new HashMap<>();
        }
        mapSqlMapper.put(clazz, configuration);
    }

    public Configuration getMapper(Class<?>clazz) {
        Configuration configuration = null;
        if (mapSqlMapper != null) {
            configuration = mapSqlMapper.get(clazz);
        }
        return configuration;
    }
}
