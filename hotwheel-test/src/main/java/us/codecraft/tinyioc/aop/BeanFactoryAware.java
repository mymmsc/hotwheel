package us.codecraft.tinyioc.aop;

import us.codecraft.tinyioc.beans.factory.TinyIOCBeanFactory;

/**
 * @author yihua.huang@dianping.com
 */
public interface BeanFactoryAware {

    void setBeanFactory(TinyIOCBeanFactory beanFactory) throws Exception;
}
