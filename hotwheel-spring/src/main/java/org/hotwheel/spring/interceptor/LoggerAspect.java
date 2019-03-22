package org.hotwheel.spring.interceptor;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * AOP 日志记录器
 *
 * @author wangfeng
 * @version 5.4.0
 * @since 2018/4/5
 */
//@Aspect
//@Component
public class LoggerAspect {
    private final static Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    private boolean debug = false;
    private String traceIdName = "traceId";

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getTraceIdName() {
        return traceIdName;
    }

    public void setTraceIdName(String traceIdName) {
        this.traceIdName = traceIdName;
    }

    /**
     * 在核心业务执行前执行，不能阻止核心业务的调用。
     *
     * @param joinPoint
     */
    private void doBefore(JoinPoint joinPoint) {
        //logger.info("-----doBefore().invoke-----");
        //logger.info(" 此处意在执行核心业务逻辑前，做一些安全性的判断等等");
        //logger.info(" 可通过joinPoint来获取所需要的内容");
        //logger.info("-----End of doBefore()------");
    }

    /**
     * 手动控制调用核心业务逻辑，以及调用前和调用后的处理,
     * <p>
     * 注意：当核心业务抛异常后，立即退出，转向After Advice
     * 执行完毕After Advice，再转到Throwing Advice
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    private Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        //logger.info("-----doAround().invoke-----");
        //logger.info(" 此处可以做类似于Before Advice的事情");

        //调用核心逻辑
        Object retVal = pjp.proceed();

        //logger.info(" 此处可以做类似于After Advice的事情");
        //logger.info("-----End of doAround()------");
        return retVal;
    }

    /**
     * 核心业务逻辑退出后（包括正常执行结束和异常退出），执行此Advice
     *
     * @param joinPoint
     */
    private void doAfter(JoinPoint joinPoint) {
        //logger.info("-----doAfter().invoke-----");
        //logger.info(" 此处意在执行核心业务逻辑之后，做一些日志记录操作等等");
        //logger.info(" 可通过joinPoint来获取所需要的内容");
        //logger.info("-----End of doAfter()------");
    }

    /**
     * 核心业务逻辑调用正常退出后，不管是否有返回值，正常退出后，均执行此Advice
     *
     * @param joinPoint
     */
    //@AfterReturning(value = "execution(* com.jiedaibao.dsmp.proxy.controller..*.*(..))", returning = "returnVal")
    private void doReturn(JoinPoint joinPoint, Object returnVal) {
        //logger.info("-----doReturn().invoke-----");
        //logger.info(" 此处可以对返回值做进一步处理");
        //logger.info(" 可通过joinPoint来获取所需要的内容");
        doLog(joinPoint, returnVal, null);
        //logger.info("-----End of doReturn()------");
    }

    /**
     * 核心业务逻辑调用异常退出后，执行此Advice，处理错误信息
     *
     * @param joinPoint
     * @param ex
     */
    private void doThrowing(JoinPoint joinPoint, Throwable ex) {
        //logger.info("-----doThrowing().invoke-----");
        doLog(joinPoint, null, ex);
        //logger.info("-----End of doThrowing()------");
    }

    private void doLog(JoinPoint joinPoint, Object returnVal, Throwable ex) {
        if (returnVal == null && ex == null) {
            return;
        }
        /**
         * 1.获取request信息
         * 2.根据request获取session
         * 3.从session中取出登录用户信息
         */
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        HttpServletResponse response = sra.getResponse();
        // 从session中获取用户信息
        HttpSession session = request.getSession();
        /*
        String loginInfo = (String) session.getAttribute("username");
        if(loginInfo != null && !"".equals(loginInfo)){
            userName = operLoginModel.getLogin_Name();
        }else{
            userName = "用户未登录" ;
        }
        // 获取输入参数
        inputParamMap = request.getParameterMap();
        // 获取请求地址
        requestPath = request.getRequestURI();

        // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
        outputParamMap = new HashMap<String, Object>();
        */
        String uri = request.getRequestURI();
        String traceId = response.getHeader(traceIdName);
        Map<String, String[]> params = request.getParameterMap();

        String message = null;
        String field = "body";
        if (returnVal != null) {
            message = JSON.toJSONString(returnVal);
        } else if (ex != null) {
            message = ex.getMessage();
            field = "exception";
        }

        logger.info("uri={},traceId[{}]={},params=[{}],{}=[{}]", uri, traceIdName, traceId, JSON.toJSONString(params), field, message);
    }
}
