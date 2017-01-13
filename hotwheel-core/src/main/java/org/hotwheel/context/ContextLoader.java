package org.hotwheel.context;

import org.hotwheel.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 类加载器
 *
 * Created by wangfeng on 2016/11/12.
 */
public class ContextLoader extends ClassLoader implements InvocationHandler {
    private Set<String> list = new HashSet<>();
    private ClassLoader classLoader;

    public ContextLoader(ClassLoader parent) {
        super(parent);
        classLoader = parent;
        //System.setProperty("java.system.class.loader", ContextLoader.class.getName());
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    public ContextLoader() {
        classLoader = this;
        //System.setProperty("java.system.class.loader", ContextLoader.class.getName());
        Thread.currentThread().setContextClassLoader(classLoader);
        /*
        Map<Thread, StackTraceElement[]> map =  Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
            Thread thread = entry.getKey();
            if (list.contains(thread.getName())) {
                continue;
            }
            list.add(thread.getName());
            StackTraceElement[] stes = entry.getValue();
            System.out.println("Thread =" + thread.getName());
            for (StackTraceElement ste : stes) {
                //System.out.println("StackTraceElement =" + ste.getClassName());
            }
            thread.setContextClassLoader(classLoader);
        }*/
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("loadClass name is " + name);
        return ClassUtils.forName(name, ContextLoader.class.getClassLoader());
        //return ClassUtils.forName(name, classLoader);
        //Connection cn = (Connection) Proxy.newProxyInstance(m_conn.getClass().getClassLoader(), new Class[]{Connection.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("method is " + method.getName());
        return proxy;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("findClass name is " + name);
        return super.findClass(name);
    }


}
