package test.org.hotwheel.tinyioc.context;

import org.junit.Test;
import test.org.hotwheel.tinyioc.HelloWorldService;
import us.codecraft.tinyioc.context.TinyIoCApplicationContext;
import us.codecraft.tinyioc.context.TinyIoCClassPathXmlApplicationContext;

/**
 * @author yihua.huang@dianping.com
 */
public class ApplicationContextTest {

    @Test
    public void test() throws Exception {
        TinyIoCApplicationContext applicationContext = new TinyIoCClassPathXmlApplicationContext("tinyioc.xml");
        HelloWorldService helloWorldService = (HelloWorldService) applicationContext.getBean("helloWorldService");
        helloWorldService.helloWorld();
    }

    @Test
    public void testPostBeanProcessor() throws Exception {
        TinyIoCApplicationContext applicationContext = new TinyIoCClassPathXmlApplicationContext("tinyioc-postbeanprocessor.xml");
        HelloWorldService helloWorldService = (HelloWorldService) applicationContext.getBean("helloWorldService");
        helloWorldService.helloWorld();
    }
}
