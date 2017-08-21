package org.hotwheel.mybatis.interceptor;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.hotwheel.assembly.Api;
import org.hotwheel.sql.SQLApi;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * mybatis 结果集类字段映射
 * Created by wangfeng on 2017/7/27.
 * @version 1.0.6
 */
@Intercepts({@Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class})})
public class RecordSetPlugin implements Interceptor {
    public Object intercept(Invocation invocation) throws Throwable {
        Object obj = null;
        ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
        Statement stmt = (Statement)invocation.getArgs()[0];
        //通过java反射获得mappedStatement属性值
        MappedStatement ms = (MappedStatement) Api.getValue(resultSetHandler, "mappedStatement");

        List<ResultMap> rms = ms.getResultMaps();
        ResultMap rm = rms != null && rms.size() > 0 ? rms.get(0) : null;
        if (rm != null) {
            Class<?> clazz = rm.getType();
            ResultSet rs = stmt.getResultSet();
            List<?> tmpList = null;
            try {
                // 保留返回list的特征, 是否优化调整待定 [wangfeng on 2017/8/21 09:55]
                tmpList = SQLApi.getRows(rs, clazz);
                obj = tmpList;
            } finally {
                SQLApi.closeQuietly(rs);
                SQLApi.closeQuietly(stmt);
            }
        } else {
            obj = invocation.proceed();
        }
        return obj;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }
}