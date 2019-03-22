package org.hotwheel.spring.annotation;

import org.hotwheel.spring.databinder.ExtendDataBinder;
import org.hotwheel.spring.databinder.impl.DefaultExtendDataBinder;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 参数名多态忽略大小写
 * <p>
 * Created by wangfeng on 15/11/4.
 *
 * @version 5.3.1
 */
public class AliasRequestParameterNameProcessor extends ServletModelAttributeMethodProcessor {

    private ExtendDataBinder extendDataBinder = null;

    public AliasRequestParameterNameProcessor(boolean annotationNotRequired) {
        this(annotationNotRequired, null);
    }

    public AliasRequestParameterNameProcessor(boolean annotationNotRequired,
                                              ExtendDataBinder extendDataBinder) {
        super(annotationNotRequired);

        this.extendDataBinder = extendDataBinder;

        if (this.extendDataBinder == null) {
            this.extendDataBinder = new DefaultExtendDataBinder();
        }
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
        bind(servletRequest, servletBinder);
    }

    private void bind(ServletRequest request, ServletRequestDataBinder dataBinder) {
        MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
        MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
        if (multipartRequest != null) {
            bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        }

        String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVars = (Map<String, String>) request.getAttribute(attr);
        if (uriVars != null) {
            for (Map.Entry<String, String> entry : uriVars.entrySet()) {
                if (mpvs.contains(entry.getKey())) {
                    logger.warn("Skipping URI variable '" + entry.getKey()
                            + "' since the request contains a bind value with the same name.");
                } else {
                    mpvs.addPropertyValue(entry.getKey(), entry.getValue());
                }
            }
        }

        this.extendDataBinder.doExtendBind(mpvs, dataBinder);

        dataBinder.bind(mpvs);
    }


    protected void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues mpvs) {
        for (Map.Entry<String, List<MultipartFile>> entry : multipartFiles.entrySet()) {
            String key = entry.getKey();
            List<MultipartFile> values = entry.getValue();
            if (values.size() == 1) {
                MultipartFile value = values.get(0);
                if (!value.isEmpty()) {
                    mpvs.add(key, value);
                }
            } else {
                mpvs.add(key, values);
            }
        }
    }
}
