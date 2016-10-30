package org.hotwheelframework.utils;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.hotwheelframework.context.Person;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 测试反射
 * Created by wangfeng on 2016/10/30.
 */
public class TestReflect {
    private final static String className = Person.class.getName();

    private static void testJdk() {
        try {
            Class<?> p_class = Class.forName(className);
            Person p = (Person) p_class.newInstance();
            Method m_setName = p_class.getMethod("setName", String.class);
            Method m_setAge = p_class.getMethod("setAge", int.class);
            Method m_sayHello = p_class.getMethod("sayHello");
            m_setAge.invoke(p, 23);
            m_setName.invoke(p, "脏三");
            m_sayHello.invoke(p);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void testCglib() {
        try{
            Class<?> p_class = Class.forName(className);
            FastClass fastClass = FastClass.create(p_class);
            Person p = (Person) fastClass.newInstance();
            FastMethod m_setAge = fastClass.getMethod("setAge", new Class[]{int.class});
            FastMethod m_setName = fastClass.getMethod("setName", new Class[]{String.class});
            FastMethod m_sayHello = fastClass.getMethod("sayHello", null);
            m_setAge.invoke(p, new Integer[]{23});
            m_setName.invoke(p, new String[]{"脏三"});
            m_sayHello.invoke(p, null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            //
        }
    }

    public static void main(String[] args) {
        long count = 100000;

        long tm = System.currentTimeMillis();
        //java自带反射
        for (int i = 0; i < count; i++) {
            testJdk();
        }
        System.out.println("jdk  用时:" + (System.currentTimeMillis() - tm) + "毫秒");

        // cglib高效反射FastClass
        tm = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            testCglib();
        }
        System.out.println("cglib用时:" + (System.currentTimeMillis() - tm) + "毫秒");
    }
}
