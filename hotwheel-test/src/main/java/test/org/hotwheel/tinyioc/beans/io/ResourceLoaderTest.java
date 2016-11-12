package test.org.hotwheel.tinyioc.beans.io;

import org.junit.Assert;
import org.junit.Test;
import us.codecraft.tinyioc.beans.io.Resource;
import us.codecraft.tinyioc.beans.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yihua.huang@dianping.com
 */
public class ResourceLoaderTest {

	@Test
	public void test() throws IOException {
		ResourceLoader resourceLoader = new ResourceLoader();
        Resource resource = resourceLoader.getResource("tinyioc.xml");
        InputStream inputStream = resource.getInputStream();
        Assert.assertNotNull(inputStream);
    }
}
