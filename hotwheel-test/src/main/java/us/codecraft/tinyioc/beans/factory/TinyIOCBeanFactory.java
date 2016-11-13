package us.codecraft.tinyioc.beans.factory;

/**
 * bean的容器
 * @author yihua.huang@dianping.com
 */
public interface TinyIOCBeanFactory {

    Object getBean(String name) throws Exception;

}
