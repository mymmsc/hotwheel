package org.hotwheel;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.context.ContextLoader;
import org.hotwheel.core.HotWheelVersion;
import org.hotwheel.dao.ermas.IOverdueErrorDao;
import test.org.hotwheel.tinyioc.HelloWorldService;
import us.codecraft.tinyioc.beans.BeanDefinition;
import us.codecraft.tinyioc.beans.factory.AbstractBeanFactory;
import us.codecraft.tinyioc.beans.factory.AutowireCapableBeanFactory;
import us.codecraft.tinyioc.beans.io.ResourceLoader;
import us.codecraft.tinyioc.beans.xml.XmlBeanDefinitionReader;

import java.util.List;
import java.util.Map;

/**
 * Created by wangfeng on 2016/11/12.
 */
public class TestIoC {

    public static void main(String... args) throws Exception {
        //ContextLoader cl = new ContextLoader();
        ContextLoader contextLoader = new ContextLoader();
        //Thread.currentThread().setContextClassLoader(contextLoader);
        IOverdueErrorDao overdueErrorDao = new IOverdueErrorDao() {
            @Override
            public List<String> getDirtyAndErrorData(@Param("limit") int limit) {
                return null;
            }

            @Override
            public List<String> getAllDirtyAndErrorData() {
                return null;
            }

            @Override
            public List<String> getAllLossFriends() {
                return null;
            }

            @Override
            public List<String> getPartialDebtorLossCreditor() {
                return null;
            }
        };
        System.out.println(HotWheelVersion.getVersion());
        System.out.println(overdueErrorDao.getClass().getClassLoader().getClass().getName());

        // 1.读取配置
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("tinyioc.xml");

        // 2.初始化BeanFactory并注册bean
        AbstractBeanFactory beanFactory = new AutowireCapableBeanFactory();
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }

        // 3.初始化bean
        beanFactory.preInstantiateSingletons();

        // 4.获取bean
        HelloWorldService helloWorldService = (HelloWorldService) beanFactory.getBean("helloWorldService");
        helloWorldService.helloWorld();
    }
}
