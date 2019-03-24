package org.hotwheel.reflect;

import org.hotwheel.util.Assert;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class PackageUtilTest {

    private Class<?> testClass = PackageUtil.class;
    private Method[] methods = null;

    @org.junit.Before
    public void setUp() throws Exception {
        methods = testClass.getDeclaredMethods();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        //
    }

    @org.junit.Test
    public void getClassName() {
        Assert.hasText(testClass.getName());
    }

    @org.junit.Test
    public void getClassName1() {
        //
    }

    @org.junit.Test
    public void getClassNameByFile() {
    }

    @org.junit.Test
    public void getMethodParamNames() {
        for (Method m : methods) {
            System.out.println("method: " + m.getName() + "==>");
            String[] pm = PackageUtil.getMethodParamNames(m);
            Assert.notEmpty(pm);
            for (String name: pm) {
                System.out.println(name);
            }
        }
    }

    @org.junit.Test
    public void getMethodParamNames1() {
    }
}