package org.hotwheelframework.context;

/**
 * Created by wangfeng on 2016/10/30.
 */
public class Person {
    private String name;
    private int age;

    public void sayHello() {
        //System.out.println("name=" + name + ", age" + age);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
