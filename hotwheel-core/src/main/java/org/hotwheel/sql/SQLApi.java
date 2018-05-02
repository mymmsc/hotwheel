/**
 * @(#)SQLApi.java 6.3.9 09/10/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.hotwheel.sql;

import org.hotwheel.assembly.Api;
import org.hotwheel.context.ApiInitialContextFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of JDBC helper methods. This class is thread safe.
 * MyMMSC SQL Mapper Framework for Java
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public final class SQLApi {
    /** JNDI ContextFactory */
    public static final String JNDI_CONTEXT_FACTORY = "java.naming.factory.initial";
    /** JNDI prefix used in a J2EE container */
    public static final String CONTAINER_PREFIX = "java:comp/env/";

    /**
     * Logger class.
     */
    private static final Logger logger = LoggerFactory.getLogger(SQLApi.class);
    // ///////////////////////////////////////////////////////////////////////////////////////

    /**
     * Close a <code>Connection</code>, avoid closing if null.
     *
     * @param conn Connection to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null.
     *
     * @param rs ResultSet to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null.
     *
     * @param stmt Statement to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }

    /**
     * Close a <code>Connection</code>, avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void closeQuietly(Connection conn) {
        try {
            close(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Connection</code>, <code>Statement</code> and
     * <code>ResultSet</code>. Avoid closing if null and hide any SQLExceptions
     * that occur.
     *
     * @param conn Connection to close.
     * @param stmt Statement to close.
     * @param rs   ResultSet to close.
     */
    public static void closeQuietly(Connection conn, Statement stmt,
                                    ResultSet rs) {
        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(stmt);
            } finally {
                closeQuietly(conn);
            }
        }

    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(ResultSet rs) {
        try {
            close(rs);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param stmt Statement to close.
     */
    public static void closeQuietly(Statement stmt) {
        try {
            close(stmt);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Commits a <code>Connection</code> then closes it, avoid closing if null.
     *
     * @param conn Connection to close.
     * @throws SQLException if a database access error occurs
     */
    public static void commitAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Commits a <code>Connection</code> then closes it, avoid closing if null
     * and hide any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void commitAndCloseQuietly(Connection conn) {
        try {
            commitAndClose(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Loads and registers a database driver class. If this succeeds, it returns
     * true, else it returns false.
     *
     * @param driverClassName of driver to load
     * @return boolean <code>true</code> if the driver was found, otherwise
     * <code>false</code>
     */
    public static boolean loadDriver(String driverClassName) {
        try {
            Class.forName(driverClassName).newInstance();
            return true;
        } catch (ClassNotFoundException e) {
            return false;

        } catch (IllegalAccessException e) {
            // Constructor is private, OK for DriverManager contract
            return true;

        } catch (InstantiationException e) {
            return false;

        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Print the stack trace for a SQLException to STDERR.
     *
     * @param e SQLException to print stack trace of
     */
    public static void printStackTrace(SQLException e) {
        printStackTrace(e, new PrintWriter(System.err));
    }

    /**
     * Print the stack trace for a SQLException to a specified PrintWriter.
     *
     * @param e  SQLException to print stack trace of
     * @param pw PrintWriter to print to
     */
    public static void printStackTrace(SQLException e, PrintWriter pw) {
        SQLException next = e;
        while (next != null) {
            next.printStackTrace(pw);
            next = next.getNextException();
            if (next != null) {
                pw.println("Next SQLException:");
            }
        }
    }

    /**
     * Print warnings on a Connection to STDERR.
     *
     * @param conn Connection to print warnings from
     */
    public static void printWarnings(Connection conn) {
        printWarnings(conn, new PrintWriter(System.err));
    }

    /**
     * Print warnings on a Connection to a specified PrintWriter.
     *
     * @param conn Connection to print warnings from
     * @param pw   PrintWriter to print to
     */
    public static void printWarnings(Connection conn, PrintWriter pw) {
        if (conn != null) {
            try {
                printStackTrace(conn.getWarnings(), pw);
            } catch (SQLException e) {
                printStackTrace(e, pw);
            }
        }
    }

    /**
     * Rollback any changes made on the given connection.
     *
     * @param conn Connection to rollback. A null value is legal.
     * @throws SQLException if a database access error occurs
     */
    public static void rollback(Connection conn) throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    /**
     * Performs a rollback on the <code>Connection</code> then closes it, avoid
     * closing if null.
     *
     * @param conn Connection to rollback. A null value is legal.
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    public static void rollbackAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.rollback();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Performs a rollback on the <code>Connection</code> then closes it, avoid
     * closing if null and hide any SQLExceptions that occur.
     *
     * @param conn Connection to rollback. A null value is legal.
     * @since DbUtils 1.1
     */
    public static void rollbackAndCloseQuietly(Connection conn) {
        try {
            rollbackAndClose(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * @param conn
     */
    public static void rollbackQuietly(Connection conn) {
        try {
            rollback(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

    private static final Hashtable jndiEnv = new Hashtable();
    private static final HashMap<String, javax.sql.DataSource> mapDataSource = new HashMap<String, javax.sql.DataSource>();

    static {
        //initialContextFactory();
        //initialNamingManager();
    }

    /**
     * 初始化javax.naming.Context环境变量
     * tomcat 不允许在运行时动态绑定
     */
    private static void initialContextFactory() {
        String cf = System.getProperty(JNDI_CONTEXT_FACTORY);
        if(!Api.isEmpty(cf)) {
            jndiEnv.put(JNDI_CONTEXT_FACTORY, cf);
        } else {
            cf = "org.hotwheel.context.ApiInitialContextFactoryBuilder";
            jndiEnv.put(JNDI_CONTEXT_FACTORY, cf);
        }
    }

    private static void initialNamingManager() {
        if (!NamingManager.hasInitialContextFactoryBuilder()) {
            try {
                NamingManager
                        .setInitialContextFactoryBuilder(new ApiInitialContextFactoryBuilder());
            } catch (NamingException e) {
                logger.error("setInitialContextFactoryBuilder failed.", e);
            }
        }
    }


    private static javax.sql.DataSource getDataSource(String jndiName) {
        javax.sql.DataSource dataSource = mapDataSource.get(jndiName);
        if (dataSource == null) {
            try {
                javax.naming.Context ctx = new javax.naming.InitialContext(jndiEnv);
                dataSource = (javax.sql.DataSource) ctx.lookup(CONTAINER_PREFIX + jndiName);
                mapDataSource.put(jndiName, dataSource);
            } catch (NamingException e) {
                logger.error("get DataSource failed.", e);
            }
        }
        return dataSource;
    }


    public static void setDataSource(String jndiName, javax.sql.DataSource dataSource) {
        if (dataSource != null) {
            try {
                javax.naming.Context ctx = new javax.naming.InitialContext(jndiEnv);
                ctx.bind(CONTAINER_PREFIX + jndiName, dataSource);
            } catch (NamingException e) {
                logger.warn("server not support bind DataSource");
            } finally {
                mapDataSource.put(jndiName, dataSource);
            }
        }
    }

    public static Connection getConnection(String jndiName) {
        Connection conn = null;
        javax.sql.DataSource dataSource = getDataSource(jndiName);
        if (dataSource != null) {
            try {
                conn = dataSource.getConnection();
            } catch (SQLException e) {
                logger.error("get connection failed.", e);
            }
        }
        return conn;
    }

    /**
     * 设定参数
     *
     * @param pstmt
     * @param args
     * @throws SQLException
     */
    private static void setTypes(PreparedStatement pstmt, Object... args)
            throws SQLException {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                pstmt.setNull(i + 1, Types.OTHER);
            } else if (args[i] instanceof Blob) {
                pstmt.setBlob(i + 1, (Blob) args[i]);
            } else if (args[i] instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) args[i]);
            } else if (args[i] instanceof Byte) {
                pstmt.setByte(i + 1, (Byte) args[i]);
            } else if (args[i] instanceof Clob) {
                pstmt.setClob(i + 1, (Clob) args[i]);
            } else if (args[i] instanceof Date) {
                pstmt.setDate(i + 1, (Date) args[i]);
            } else if (args[i] instanceof Double) {
                pstmt.setDouble(i + 1, (Double) args[i]);
            } else if (args[i] instanceof Float) {
                pstmt.setFloat(i + 1, (Float) args[i]);
            } else if (args[i] instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) args[i]);
            } else if (args[i] instanceof Long) {
                pstmt.setLong(i + 1, (Long) args[i]);
            } else if (args[i] instanceof Short) {
                pstmt.setShort(i + 1, (Short) args[i]);
            } else if (args[i] instanceof String) {
                pstmt.setString(i + 1, (String) args[i]);
            } else if (args[i] instanceof Time) {
                pstmt.setTime(i + 1, (Time) args[i]);
            } else if (args[i] instanceof Timestamp) {
                pstmt.setTimestamp(i + 1, (Timestamp) args[i]);
            } else if (args[i] instanceof URL) {
                pstmt.setURL(i + 1, (URL) args[i]);
            } else {
                pstmt.setObject(i + 1, args[i]);
            }
        }
    }

    /**
     * 获得一条记录, 并返回一个bean实例
     *
     * @param <T>             泛型
     * @param rs              ResultSet对象
     * @param clazz           类
     * @param charsetDatabase 数据表字段字符集
     * @param charsetApp      应用程序编码
     * @return 返回一个数据实例, 对于基础数据类型, 则不是对象, 如int.
     */
    public static <T> T valueOf(ResultSet rs, Class<T> clazz,
                                String charsetDatabase, String charsetApp) {
        T obj = null;
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            String fieldName = null;
            // 将每一个字段取出进行赋值
            for (int i = 1; i <= colCount; i++) {
                fieldName = meta.getColumnLabel(i);
                String value = rs.getString(i);
                if (value != null) {
                    value = Api.iconv(value, charsetDatabase,
                            charsetApp);
                } else {
                    value = "";
                }
                if (colCount <= 1 && Api.isBaseType(clazz)) {
                    obj = Api.valueOf(clazz, value);
                } else {
                    if (obj == null) {
                        obj = clazz.newInstance();// 构造业务对象实体
                    }
                    Api.setValue(obj, fieldName, value);
                }
            }
        } catch (SQLException e) {
            logger.error("valueOf failed.", e);
        } catch (InstantiationException e) {
            logger.error("valueOf failed.", e);
        } catch (IllegalAccessException e) {
            logger.error("valueOf failed.", e);
        }
        return obj;
    }

    /**
     * 使用默认的字符集utf-8, 进行数据读取
     *
     * @param rs
     * @param clazz
     * @return
     */
    public static <T> T valueOf(ResultSet rs, Class<T> clazz) {
        return valueOf(rs, clazz, "utf-8", "utf-8");
    }

    /**
     * 获得记录集
     *
     * @param <T>
     * @param rs
     * @param clazz
     * @param charsetDatabase
     * @param charsetApp
     * @return List
     */
    public static <T> List<T> getRows(ResultSet rs, Class<T> clazz,
                                      String charsetDatabase, String charsetApp) {
        List<T> list = new ArrayList<T>();
        try {
            while (rs.next()) {// 对每一条记录进行操作
                T obj = valueOf(rs, clazz, charsetDatabase, charsetApp);
                list.add(obj);
            }
        } catch (SQLException e) {
            logger.error("get Rows failed.", e);
        }

        return list;
    }

    /**
     * 获得记录集, 默认输入输出均为utf-8
     *
     * @param <T>
     * @param rs
     * @param clazz
     * @return
     */
    public static <T> List<T> getRows(ResultSet rs, Class<T> clazz) {
        return getRows(rs, clazz, "utf-8", "utf-8");
    }

    /**
     * 获得一组记录
     *
     * @param jndiName 数据源名
     * @param clazz    Java Bean类
     * @param sql      SQL语句
     * @param args     可变参数
     * @return
     */
    public static <T> List<T> getRows(String jndiName, Class<T> clazz, String sql, Object... args) {
        List<T> result = null;
        Connection conn = getConnection(jndiName);
        if (conn != null) {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = conn.prepareStatement(sql);
                setTypes(pstmt, args);
                rs = pstmt.executeQuery();
                result = getRows(rs, clazz);
            } catch (SQLException e) {
                logger.error("get record list failed.", e);
            } finally {
                closeQuietly(rs);
                closeQuietly(pstmt);
                closeQuietly(conn);
            }
        }

        return result;
    }

    /**
     * 获得记录集, 不关闭Connection
     *
     * @param <T>
     * @param conn
     * @param clazz
     * @param sql
     * @param args
     * @return
     */
    public static <T> List<T> getRows(Connection conn, Class<T> clazz,
                                      String sql, Object... args) {
        List<T> list = new ArrayList<T>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            setTypes(pstmt, args);
            rs = pstmt.executeQuery();
            list = getRows(rs, clazz);
        } catch (SQLException e) {
            logger.error("get record list failed.", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(pstmt);
        }

        return list;
    }

    /**
     * 执行SQL查询语句, 封装记录字段到T, 支持事务
     *
     * @param <T>
     * @param conn
     * @param clazz
     * @param sql
     * @param args
     * @return 返回对象
     */
    public static <T> T getOneRow(Connection conn, Class<T> clazz, String sql,
                                  Object... args) {
        T obj = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            setTypes(pstmt, args);
            rs = pstmt.executeQuery();

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            if (rs.next()) {// 对每一条记录进行操作
                if (colCount <= 1 && Api.isBaseType(clazz)) {
                    String value = rs.getString(1);
                    obj = Api.valueOf(clazz, value);
                } else {
                    obj = valueOf(rs, clazz, "utf-8", "utf-8");
                }
            } else if (Api.isBaseType(clazz)) {
                obj = Api.valueOf(clazz, null);
            } else {
                obj = null;
            }
        } catch (SQLException e) {
            logger.error("get one record failed.", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(pstmt);
        }

        return obj;
    }

    /**
     * 获得一条记录
     *
     * @param jndiName 数据源名
     * @param clazz    Java Bean类
     * @param sql      SQL语句
     * @param args     可变参数
     * @return
     */
    public static <T> T getOneRow(String jndiName, Class<T> clazz, String sql, Object... args) {
        T result = null;
        Connection conn = getConnection(jndiName);
        if (conn != null) {
            try {
                result = getOneRow(conn, clazz, sql, args);
            } finally {
                closeQuietly(conn);
            }
        }

        return result;
    }

    /**
     * 执行SQL语句, 支持INSERT/UPDATE/DELETE
     *
     * @param conn
     * @param sql
     * @param args
     * @return
     */
    public static int execute(Connection conn, String sql, Object... args) {
        // -1 为失败
        int nRet = -1;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            setTypes(pstmt, args);
            nRet = pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("", e);
        } finally {
            closeQuietly(pstmt);
            //closeQuietly(conn);
        }

        return nRet;
    }

    /**
     * 执行SQL语句
     *
     * @param jndiName 数据源
     * @param sql
     * @param args
     * @return
     */
    public static int execute(String jndiName, String sql, Object... args) {
        int iRet = -1;
        Connection conn = getConnection(jndiName);
        if (conn != null) {
            iRet = execute(conn, sql, args);
            closeQuietly(conn);
        }
        return iRet;
    }

    /**
     * 执行SQL-INSERT语句
     *
     * @param conn
     * @param sql
     * @param args
     * @return
     */
    public static int insert(Connection conn, String sql, Object... args) {
        // -1 为失败
        int nRet = -1;
        Pattern pattern = Pattern.compile("^\\s*insert\\s+into\\s+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            PreparedStatement pstmt = null;
            try {
                pstmt = conn.prepareStatement(sql);
                setTypes(pstmt, args);
                nRet = pstmt.executeUpdate();
                if (nRet > 0) {
                    pstmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        nRet = rs.getInt(1);
                    }
                }
            } catch (SQLException e) {
                logger.error("execute SQL failed.", e);
            } finally {
                closeQuietly(pstmt);
                //closeQuietly(conn);
            }
        }
        return nRet;
    }

    /**
     * 执行SQL-INSERT语句
     *
     * @param jndiName 数据源
     * @param sql
     * @param args
     * @return
     */
    public static int insert(String jndiName, String sql, Object... args) {
        int iRet = -1;
        Connection conn = getConnection(jndiName);
        if (conn != null) {
            iRet = insert(conn, sql, args);
            closeQuietly(conn);
        }
        return iRet;
    }
}
