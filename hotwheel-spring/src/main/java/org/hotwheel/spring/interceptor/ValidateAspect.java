package org.hotwheel.spring.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hotwheel.exception.ActionError;
import org.hotwheel.exception.ApiException;
import org.hotwheel.spring.validator.ParamValid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 验证注解处理类
 * Created by wangfeng on 2017/6/6.
 *
 * @version 1.0.0
 */
@Aspect
@Component
public class ValidateAspect {
    private final static Logger logger = LoggerFactory.getLogger(ValidateAspect.class);

    /**
     * 使用AOP对使用了ValidateGroup的方法进行代理校验
     *
     * @throws Throwable
     */
    @Around("@annotation(org.hotwheel.spring.validator.ValidMethod)")
    public Object validateAround(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean flag = false;
        Object[] args = null;
        Method method = null;
        Object target = null;
        String methodName = null;
        //try {
            methodName = joinPoint.getSignature().getName();
            target = joinPoint.getTarget();
            // 得到拦截的方法
            method = getMethodByClassAndName(target.getClass(), methodName);
            // 方法的参数
            args = joinPoint.getArgs();
            List<Object> params = getParamForValidate(method, args);
            validate(params);
            // 验证通过
            return joinPoint.proceed();
        //} catch (Exception e) {
        //    logger.error("参数验证异常: ", e);
        //    throw new ApiException(MiscError.SC_EVALIDATE, "系统执行参数验证时发生异常");
        //}
    }

    /**
     * 根据类和方法名得到方法
     */
    public Method getMethodByClassAndName(Class c, String methodName) {
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private List<Object> getParamForValidate(Method method, Object[] args) {
        List<Object> list = new ArrayList<>();
        Annotation[][] ans = method.getParameterAnnotations();
        for (int i = 0; i < ans.length; i++) {
            Annotation[] pas = ans[i];
            for (Annotation an : pas) {
                if (an instanceof ParamValid) {
                    list.add(args[i]);
                    break;
                }
            }
        }
        return list;
    }

    private void validate(final List<Object> objs) {
        /**
          // 判断正则
         if (arg instanceof String) {
         if (!((String) arg).matches(validateFiled.regex())) {
         return false;
         }
         } else {
         return false;
         }
         */
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        javax.validation.Validator validator = vf.getValidator();
        for (Object obj : objs) {
            Set<ConstraintViolation<Object>> set = validator.validate(obj);
            if (!set.isEmpty()) {
                StringBuffer sb = new StringBuffer();
                for (ConstraintViolation<Object> cv : set) {
                    if (sb.length() > 1) {
                        sb.append(",");
                    }
                    sb.append(String.format("参数[%s]%s", cv.getPropertyPath(), cv.getMessage()));
                }
                throw new ApiException(ActionError.SC_EVALIDATE, sb.toString());
            }
        }
    }
}
