package org.hotwheel.beans.factory;

import org.hotwheel.beans.BeansException;

public interface BeanFactory {

    Object getBean(String name) throws BeansException;

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    /**
     * Return the bean instance that uniquely matches the given object type, if any.
     * @param requiredType type the bean must match; can be an interface or superclass.
     */
    <T> T getBean(Class<T> requiredType) throws BeansException;

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     */
    Object getBean(String name, Object... args) throws BeansException;

    /**
     * Does this bean factory contain a bean definition or externally registered singleton
     * instance with the given name?
     */
    boolean containsBean(String name);
}
