package org.hotwheel.spring.databinder;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * 参数名多态忽略大小写
 * <p>
 * Created by wangfeng on 15/11/4.
 *
 * @version 5.3.1
 */
public interface ExtendDataBinder {
    public void doExtendBind(MutablePropertyValues mpvs, ServletRequestDataBinder dataBinder);
}
