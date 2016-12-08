package org.hotwheel.utils;

import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.hotwheel.reflect.FieldAccess;
import org.mymmsc.api.assembly.Api;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Created by wangfeng on 16/8/18.
 */
public class TestPerformance {
    public static void main(String[] args)
    {
        MyBean obj = new MyBean();
        FieldAccess access = FieldAccess.get(MyBean.class);
        String name = (String)access.get(obj, "name");
        //FieldAccess access = FieldAccess.get
        int times = 10000000;
        TestBean(times);//=15
        TestCglib(times);//=516
        TestBeanMap(times);//=256
        TestReflection(times);// =11359
        TestReflection2(times);// =11359
    }

    public static void TestBean(int times)
    {
        System.out.println("TestBean----------");
        MyBean bean = new MyBean();

        Date start = Calendar.getInstance().getTime();
        for (int i = 0; i < times; i++)
        {
            bean.setName("helloworld");
            Object v = bean.getName();
        }

        Date end = Calendar.getInstance().getTime();

        System.out.println(end.getTime() - start.getTime());
    }

    public static void TestCglib(int times)
    {
        System.out.println("TestCglib----------");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MyBean.class);
        enhancer.setCallback(new TestMethodInterceptorImpl());
        MyBean my = (MyBean) enhancer.create();

        Date start = Calendar.getInstance().getTime();
        for (int i = 0; i < times; i++)
        {
            my.setName("helloworld");
            Object v = my.getName();
        }

        Date end = Calendar.getInstance().getTime();

        System.out.println(end.getTime() - start.getTime());
    }

    private static void set(BeanMap map, Object obj,String key, String value) {
        //String sRet = null;

        for (String tmpKey : (Set<String>)map.keySet()) {
            if(tmpKey.equalsIgnoreCase(key)) {
                //sRet = map.get(tmpKey);
                map.put(obj, tmpKey, value);
                break;
            }
        }

        //return sRet;
    }

    public static void TestBeanMap(int times)
    {
        System.out.println("TestBeanMap----------");
        MyBean bean = new MyBean();
        BeanMap map = BeanMap.create(bean);

        Date start = Calendar.getInstance().getTime();
        for (int i = 0; i < times; i++)
        {
            //map.put(bean, "name", "helloworld");
            set(map, bean, "Name", "hwewqwe");
            Object v = bean.getName();
        }
        Date end = Calendar.getInstance().getTime();

        System.out.println(end.getTime() - start.getTime());
    }

    public static void TestReflection(int times)
    {
        System.out.println("TestReflection----------");
        MyBean bean = new MyBean();
        Class c = MyBean.class;
        try
        {
            Method get = c.getDeclaredMethod("getName", null);
            Method set = c.getDeclaredMethod("setName", String.class);
            Date start = Calendar.getInstance().getTime();
            for (int i = 0; i < times; i++)
            {
                set.invoke(bean, "helloworld");
                Object v = get.invoke(bean, null);
            }
            Date end = Calendar.getInstance().getTime();

            System.out.println(end.getTime() - start.getTime());
        } catch (Exception ex)
        {

        }
    }

    public static void TestReflection2(int times)
    {
        System.out.println("TestReflection2(Api)----------");
        MyBean bean = new MyBean();
        Class c = MyBean.class;
        try
        {
            Method get = c.getDeclaredMethod("getName", null);
            Method set = c.getDeclaredMethod("setName", String.class);
            Date start = Calendar.getInstance().getTime();
            for (int i = 0; i < times; i++)
            {
                //set.invoke(bean, "helloworld");
                Api.setValue(bean, "name", "helloworld" + i);
                Object v = get.invoke(bean, null);
            }
            Date end = Calendar.getInstance().getTime();

            System.out.println(end.getTime() - start.getTime());
        } catch (Exception ex)
        {

        }
    }
}

class TestMethodInterceptorImpl implements MethodInterceptor
{
    public Object intercept(Object obj, Method method, Object[] args,
                            MethodProxy proxy) throws Throwable
    {
        return proxy.invokeSuper(obj, args);
    }
}

