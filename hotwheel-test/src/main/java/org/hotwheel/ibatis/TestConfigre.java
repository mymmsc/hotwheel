package org.hotwheel.ibatis;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.SynchronizedCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by wangfeng on 2016/11/1.
 * @since 1.0
 */
public class TestConfigre {
    private final static String projectPath = "/Users/wangfeng/projects/mymmsc/hotwheel/hotwheel-test";
    private final static String resourcePath = projectPath + "/src/main/resources";

    private final static String xmlMybatisConfig = resourcePath +  "/mybatis/hotwheel-mybatis.xml";

    public static void main(String[] args) {
        //InputStream inputStream = null;
        FileInputStream inputStream = null;
        try {
            File file = new File(xmlMybatisConfig);
            inputStream = new FileInputStream(file);
            XMLConfigBuilder builder = new XMLConfigBuilder(inputStream);
            Configuration config = builder.parse();
            Environment environment = config.getEnvironment();

            Transaction transaction = new JdbcTransaction(environment.getDataSource(), null, false);
            final Executor executor = config.newExecutor(transaction);
            final Cache countryCache =
                    new SynchronizedCache(//同步缓存
                            new SerializedCache(//序列化缓存
                                    new LoggingCache(//日志缓存
                                            new LruCache(//最少使用缓存
                                                    new PerpetualCache("country_cache")//持久缓存
                                            ))));


            //类型处理注册器
            //自己写TypeHandler的时候可以参考该注册器中已经存在的大量实现
            final TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
            MappedStatement ms = config.getMappedStatement("getDirtyAndErrorData");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
