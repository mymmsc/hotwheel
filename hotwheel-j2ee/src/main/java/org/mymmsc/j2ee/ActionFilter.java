/**
 * @(#)ActionFilter.java 6.3.9 09/10/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee;

// log4j
//import org.apache.log4j.xml.DOMConfigurator;
// log4j2
//import org.apache.logging.log4j.core.config.ConfigurationSource;
//import org.apache.logging.log4j.core.config.Configurator;

import org.apache.ibatis.session.SqlSession;
import org.hotwheel.Environment;
import org.hotwheel.adapter.BaseObject;
import org.hotwheel.assembly.Api;
import org.hotwheel.assembly.XmlParser;
import org.hotwheel.beans.factory.annotation.Autowired;
import org.hotwheel.category.Encoding;
import org.hotwheel.context.template.Templator;
import org.hotwheel.context.template.VariableNotDefinedException;
import org.hotwheel.ibatis.builder.SqlApplicationContext;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.j2ee.HotWheel;
import org.hotwheel.j2ee.scheduler.AbstractTask;
import org.hotwheel.j2ee.util.manifests.ServletMfs;
import org.hotwheel.json.JsonAdapter;
import org.hotwheel.protocol.Http11Status;
import org.hotwheel.redis.RedisApi;
import org.hotwheel.util.manifests.Manifests;
import org.mymmsc.j2ee.annotation.Controller;
import org.mymmsc.j2ee.annotation.RequestMapping;
import org.mymmsc.j2ee.annotation.ResponseBody;
import org.mymmsc.j2ee.annotation.WebAction;
import org.mymmsc.j2ee.http.Category;
import org.mymmsc.j2ee.http.HttpCookie;
import org.mymmsc.j2ee.http.HttpParameter;
import org.mymmsc.j2ee.util.PackageUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * FastAction过滤器
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.13 13/11/06
 * @remark 替代Struts产品
 * @since mymmsc-struts 6.3.9
 * @since mymmsc-struts 6.9.29 去掉对action扩展名的支持
 * @since 6.11.0
 * @since hotwheel-j2ee 2.1.8
 */
@WebFilter(filterName = "MyMMSC-HotWheel(J2EE)-Filter", urlPatterns = {"*.cgi"}, asyncSupported = true)
public class ActionFilter extends BaseObject implements Filter {
    /**
     * 是否已经初始化
     */
    private volatile static boolean isInit = false;
    private static Manifests manifests = null;
    private static String appVersion = "Unknown";
    private static String project = null;
    private static String webPath = null;
    private static XmlParser xmlParser = null;
    private static RedisApi redisApi = null;
    private Map<String, Object> map = null;
    private ServletContext context = null;
    private String expire = null;
    private static SqlApplicationContext applicationContext;

    static {
        //
    }

    public ActionFilter() {
        super();
    }

    /**
     * 修复URL请求路径
     *
     * @param uri
     * @return
     */
    private String fixUri(String uri) {
        StringBuffer sb = new StringBuffer();
        String[] as = uri.split("[/]+");
        if (as.length > 2) {
            for (int i = 2; i < as.length; i++) {
                if (sb.length() > 0) {
                    sb.append("/");
                }
                sb.append(as[i]);
            }
        } else if (as.length == 1) {
            sb.append(as[0]);
        } else {
            sb.append("/");
        }

        return sb.toString();
    }

    private String actionForInfo(String request) {
        String clazz = null;
        String uri = fixUri(request);
        String type = uri.endsWith("." + APS.ACTION_EXT) ? APS.ACTION_EXT : APS.CGI_EXT;
        if (type != null) {
            int iEnd = uri.lastIndexOf("." + type);
            if (iEnd > 0) {
                clazz = uri.substring(0, iEnd);
            }
        }
        if (clazz != null) {
            clazz = clazz.trim();
        }
        return clazz;
    }

    private String actionForInfo(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return actionForInfo(requestUri);
    }

    private Object classForAction(String action) {
        Object clazz = null;
        if (map != null) {
            clazz = map.get(action);
        }
        return clazz;
    }

    private List<SqlSession> actionAutowired(HttpController action) {
        List<SqlSession> sessions = new ArrayList<SqlSession>();
        Class clazz = action.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired != null) {
                Class<?> contextClass = field.getType();
                SqlSession session = applicationContext.getSesseion(contextClass);
                Api.setValue(action, field.getName(), session.getMapper(contextClass));
                sessions.add(session);
            }
        }

        return sessions;
    }

    private void doService(ServletRequest req, ServletResponse resp, FilterChain chain, Object controller)
            throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, XPathExpressionException, ClassNotFoundException,
            ServletException {
        long tm = System.currentTimeMillis();
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpParameter parameter = new HttpParameter(Encoding.Default, Encoding.Default);
        parameter.init(request, response);

        boolean isContext = false;
        Class<?> clazz = null;
        HttpController action = null;
        if (controller instanceof Class) {
            clazz = (Class<?>)controller;
        } else {
            isContext = true;
            ActionContext ac = (ActionContext)controller;
            clazz = ac.clazz;
        }
        action = (HttpController) parameter.valueOf(clazz);
        List<SqlSession> sqlSessionList = actionAutowired(action);
        String xip = request.getHeader("X-Forwarded-For");
        if (xip == null) {
            xip = request.getRemoteAddr();
        }
        action.setClientIp(xip);
        action.setClientPort(request.getRemotePort());
        action.setParameter(parameter);
        action.setBody(parameter.getBody());
        HttpCookie cookie = new HttpCookie(Category.SESSION_TIMEOUT);
        cookie.init(request, response);
        action.setHttpCookie(cookie);
        action.setProject(project);
        action.setWebPath(webPath);
        action.setQuery(request.getQueryString());
        action.setRequest(request.getRequestURI());
        // Action传入HttpServlet对象, 以备特殊需求调用
        action.setServlet(request, response);
        action.setRedis(redisApi);
        action.setContext(context);

        byte[] data = null;
        try {
            Method exec = null;
            if (!isContext) {
                exec = clazz.getMethod("execute", new Class[]{});
                data = (byte[]) exec.invoke(action);
            } else {
                ActionContext actionContext = (ActionContext)controller;
                exec = actionContext.method;
                Object obj = null;
                // 检查请求方法
                if (!actionContext.allowed(request.getMethod())) {
                    ActionStatus as = new ActionStatus();
                    as.set(405, Http11Status.getStatus(405));
                    data = JsonAdapter.get(as, false).getBytes(action.getCharset());
                } else {
                    if (actionContext.paramNames == null) {
                        obj = exec.invoke(action);
                    } else {
                        Object[] params = new Object[actionContext.paramNames.length];
                        Class<?>[] parameterTypes = exec.getParameterTypes();
                        for (int i = 0; i < actionContext.paramNames.length; i++) {
                            String value = parameter.getStr(actionContext.paramNames[i]);
                            params[i] = Api.valueOf(parameterTypes[i], value);
                        }
                        obj = exec.invoke(action, params);
                    }
                    if (!Api.isBaseType(obj)) {
                        if (obj instanceof ActionStatus) {
                            ((ActionStatus)obj).setVersion(appVersion);
                        }
                        data = JsonAdapter.get(obj, false).getBytes(action.getCharset());
                    } else {
                        String str = Api.toString(obj);
                        data = str.getBytes(action.getCharset());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            for (SqlSession session : sqlSessionList) {
                session.close();
            }
        }
        // 重置 字符集
        response.setCharacterEncoding(action.getCharset());
        // 判断是否抓发
        if (action.httpStatus.equalsIgnoreCase(APS.SC_FORWARD)) {
            // 转发, 这里不做任务业务逻辑的处理
            String url = action.getRequest();
            String actionName = actionForInfo(url);
            if (actionName != null) {
                clazz = (Class<?>) classForAction(actionName);
                if (clazz == null) {
                    response.sendError(404);
                    // info("Not found " + actionName);
                } else {
                    doService(request, response, chain, clazz);
                }
            } else if (Api.isFile(webPath + url)) {
                request.getRequestDispatcher(url).forward(req, resp);
            } else {
                chain.doFilter(req, resp);
            }
        } else if (action.httpStatus.equalsIgnoreCase(APS.SC_REDIRECT)) {
            // 旧版业务重定向, 需要做重定向
            if (data != null && data.length > 0) {
                response.sendRedirect(new String(data, action.getCharset()));
            } else {
                // 业务action已经重定向, 这里不做任务业务逻辑的处理
            }
        } else if (data == null || data.length == 0) {
            response.sendError(500);
        } else {
            // 更新HTTP-Response-Header
            Map<String, String> tmpMap = action.header();
            if (tmpMap != null) {
                for (String k : tmpMap.keySet()) {
                    response.setHeader(k, tmpMap.get(k));
                }
            }
            // 设置耗时, 单位毫秒
            tm = System.currentTimeMillis() - tm;
            response.setHeader(HotWheel.vender + "/version", HotWheel.version);
            response.setHeader(HotWheel.vender + "/acrossTime", String.valueOf(tm));
            // 设置Content-Length
            response.setContentLength(data.length);
            ServletOutputStream out = response.getOutputStream();
            out.write(data);
            out.flush();
            out.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        int errno = -1;
        @SuppressWarnings("unused")
        int eBase = 1000;
        String message = null;
        /*
         * if (iFlag < 0) { String tm = Api.toString(new Date(),
		 * "yyyy-MM-dd HH:mm:ss"); if (tm.compareTo(expire) >= 0) { errno =
		 * eBase + 1; message = "应用服务授权已过期"; } else { errno = 0; } } else if
		 * (iFlag == 1) { errno = eBase + 2; message = "应用服务尚未授权"; } else if
		 * (iFlag == 2) { errno = eBase + 2; message = "应用服务授权无效"; } else if
		 * (iFlag == 3) { errno = eBase + 3; message = "应用服务该服务器未授权"; }
		 */
        errno = 0;
        if (errno == -1) {
            chain.doFilter(req, resp);
        } else if (errno == 0) {
            try {
                String actionName = actionForInfo(request);
                if (actionName != null) {
                    Object clazz = classForAction(actionName);
                    if (clazz == null) {
                        response.sendError(404);
                        logger.info("Not found " + actionName);
                    } else {
                        doService(request, response, chain, clazz);
                    }
                } else {
                    chain.doFilter(req, resp);
                }
            } catch (Exception e) {
                logger.warn("", e);
            }
        } else {
            response.setCharacterEncoding("utf-8");
            // response.setContentType("text/xml; charset=utf-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            PrintWriter out = response.getWriter();
            ActionStatus as = new ActionStatus();
            as.set(errno, message);
            String sReturn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<license>" + "  <status>" + errno
                    + "</status>" + "  <description>" + message + "</description>" + "</license>";
            sReturn = JsonAdapter.get(as, true);
            out.println(sReturn);
            out.flush();
            out.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        // 已经初始化, 直接返回
        if (isInit) {
            // return;
        }
        ServletContext servletContext = config.getServletContext();
        ServletMfs servletMfs = new ServletMfs(servletContext);
        try {
            manifests = new Manifests();
            manifests.append(servletMfs);
            appVersion = manifests.get("App-Revision");
        } catch (Exception e) {
            logger.error("#read() META-INF failed: ", e);
        }
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        // 读取工程路径
        // info("初始化struts5...");
        context = config.getServletContext();
        project = context.getContextPath().replaceAll("/", "");
        project = project.replaceAll("\\\\", "");
        webPath = context.getRealPath("/");
        webPath = webPath.replaceAll("\\\\", "/");

        // 日志路径为环境变量${MSF_LOGS}或者
        // 用户主目录下runtime/logs [wangfeng@2012-5-30 上午8:51:12]
        String logsPath = Api.getEnv("MSF_LOGS");
        if (logsPath == null || logsPath.length() < 1) {
            if (!Api.isDirectory(Category.GLOBAL_LOG_PATH)) {
                Api.mkdirs(Category.GLOBAL_LOG_PATH);
            }
            if (Api.isDirectory(Category.GLOBAL_LOG_PATH)) {
                logsPath = Category.GLOBAL_LOG_PATH;
            } else {
                logsPath = Environment.get("user.home");
                if (!logsPath.endsWith("/runtime")) {
                    logsPath += "/runtime";
                }
                logsPath += "/logs";
            }
        }
        logsPath = logsPath.replaceAll("\\\\", "/");
        // 修订log4j.properties文件路径
        String filename = webPath + "WEB-INF/classes/" + Category.Log4jV2Filename;
        Templator tpl;
        /*
        try {
            // 为log4j配置文件准备环境变量
            //System.setProperty(keyHome, dsmpHome);
            // 刷新log4j配置文件
            tpl = new Templator(this.getClass().getResourceAsStream(Category.Log4jTpl));
            htmlSetVariable(tpl, "logs.home", logsPath);
            htmlSetVariable(tpl, "project", project);
            tpl.generateOutput(filename);

            Api.mkdirs(logsPath + '/' + project);
            initLog4jV2(filename);
        } catch (Exception e) {
            logger.error("", e);
        }*/
        String jarFile = webPath + "/WEB-INF/lib/mymmsc-j2ee.jar";
        if (Api.isFile(jarFile)) {
            String fileName = webPath + "/WEB-INF/license.msf";
            if (!Api.isFile(fileName)) {
            } else {
                Properties prop = new Properties();
                File file = new File(fileName);
                FileInputStream input = null;
                try {
                    input = new FileInputStream(file);
                    prop.load(input);
                    String userId = prop.getProperty("userid");
                    String key = prop.getProperty("key");
                    if (key == null) {
                        key = "xxx";
                    } else {
                        key = key.trim();
                    }
                    expire = prop.getProperty("expire");
                    String tmpKey = Api.md5(userId + "mymmsc" + expire + "j2ee" + project);
                    if (tmpKey.equalsIgnoreCase(key)) {
                    } else {
                    }
                } catch (FileNotFoundException e) {
                    logger.error("", e);
                } catch (IOException e) {
                    logger.error("", e);
                } finally {
                    //
                }
            }
        }
        filename = webPath + "WEB-INF/classes/api.xml";
        if (Api.isFile(filename)) {
            try {
                xmlParser = new XmlParser(filename, true);
            } catch (Exception e) {
                logger.error("解析XML失败", e);
                if (xmlParser != null) {
                    xmlParser.close();
                }
            }
        }
        applicationContext = initBatis();
        logger.info("扫描action...");
        if (map == null) {
            map = scanAnnotations();
        }
        logger.info("扫描action...结束");
        // 初始化RedisApi
        if (xmlParser != null) {
            String redisHost = "redis.api.mymmsc.org";
            int redisPort = 6379;
            String redisDatabase = null;
            String redisAuth = null;
            String schema = Api.getEnv("MSF_SCHEMA");
            String exp = "//api/redis";
            if (!Api.isEmpty(schema)) {
                schema = schema.trim();
                exp = String.format("//api[@schema='%s']/redis", schema);
            }
            try {
                NodeList list = xmlParser.query(exp);
                if (list != null && list.getLength() > 0) {
                    org.w3c.dom.Node node = list.item(0);
                    redisHost = xmlParser.valueOf(node, "host");
                    String tmpPort = xmlParser.valueOf(node, "port");
                    redisPort = Api.valueOf(int.class, tmpPort);
                    redisDatabase = xmlParser.valueOf(node, "database");
                    redisAuth = xmlParser.valueOf(node, "auth");
                }
            } catch (XPathExpressionException e) {
                logger.error("", e);
            }
            if (redisHost != null && redisPort > 0) {
                try {
                    int db = Api.valueOf(int.class, redisDatabase);
                    redisApi = RedisApi.getInstance(redisHost, redisPort, db, redisAuth);
                } catch (Exception e) {
                    logger.error("加载redis配置信息异常: ", e);
                }
            }
        }
        // 设置已初始化标示
        isInit = true;
    }

    protected boolean htmlSetVariable(Templator templator, String name, String value) {
        boolean bRet = false;
        try {
            templator.setVariable(name, value);
            bRet = true;
        } catch (VariableNotDefinedException e) {
            logger.error("", e);
        }

        return bRet;
    }

    private String updateUri(String uri) {
        StringBuffer sb = new StringBuffer();
        String[] paths = uri.split("/");
        for (String str : paths) {
            if(!Api.isEmpty(str)) {
                sb.append("/");
                sb.append(str.trim());
            }
        }
        return sb.length() > 0 ? sb.substring(1) : sb.toString();
    }


    @Override
    public void close() {
        //
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        if (xmlParser != null) {
            xmlParser.close();
        }
    }

    //private String getRequestUri()

    private Map scanAnnotations() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        String packageName = webPath + "WEB-INF/classes";
        // packageName = ".";
        List<String> classNames = PackageUtil.getClassNameByFile(packageName, true);
        if (classNames != null) {
            for (String className : classNames) {
                // info(className);
                try {
                    Class<?> clazz = Class.forName(className);
                    Annotation[] anns = null;
                    anns = clazz.getDeclaredAnnotations();
                    if (anns != null && anns.length > 0) {
                        for (Annotation clsAnnotation : anns) {
                            boolean bController = false;
                            if (clsAnnotation instanceof WebAction) {
                                WebAction wa = (WebAction) clsAnnotation;
                                if (wa != null) {
                                    String url = wa.url();
                                    if (url != null) {
                                        url = url.trim();
                                        logger.info("action=[{}], class=[{}]", url, clazz.getName());
                                        map.put(url, clazz);
                                    }
                                }
                            } else if (clsAnnotation instanceof Controller) {
                                // controller
                                bController = true;
                                String requestUri = null;
                                RequestMapping clsRequestMapping = clazz.getAnnotation(RequestMapping.class);
                                if(clsRequestMapping != null) {
                                    requestUri = updateUri(clsRequestMapping.value());
                                    Method[] methods = clazz.getMethods();
                                    for (Method method : methods) {
                                        RequestMapping rm = method.getAnnotation(RequestMapping.class);
                                        ResponseBody rb = method.getAnnotation(ResponseBody.class);

                                        if(rm != null) {
                                            ActionContext ac = new ActionContext();
                                            ac.clazz = clazz;
                                            ac.method = method;
                                            ac.uri = requestUri + "/" + updateUri(rm.value());
                                            if (rb != null) {
                                                ac.responseBody = true;
                                            }
                                            ac.requestMethod = rm.method();
                                            ac.paramNames = PackageUtil.getMethodParamNames(method, webPath + "WEB-INF/classes");
                                            logger.info("request={}, class={}#{}, params={}", ac.uri, ac.clazz.getName(), ac.method.getName(), ac.paramNames);
                                            map.put(ac.uri, ac);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("", e);
                }
            }
        }
        return map;
    }

    private SqlApplicationContext initBatis() {
        SqlApplicationContext applicationContext = null;
        final String exp = "//plugins/mybatis";
        Node node = null;
        try {
            node = xmlParser.queryOne(exp);
            String location = xmlParser.valueOf(node, "resource");
            applicationContext = new SqlApplicationContext(location);
            applicationContext.parse();
            AbstractTask.setApplicationContext(applicationContext);
        } catch (XPathExpressionException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }

        return applicationContext;
    }
}
