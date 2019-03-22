package test.org.hotwheel.validator;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户信息
 * <p>
 * Created by wangfeng on 2017/6/13.
 *
 * @version 1.0.0
 */
public class User {

    private int age;

    @NotNull
    @Size(min = 2)
    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return username + ", " + password;
    }

    @NotNull
    @Min(value = 8, message = "年龄不能小于8岁")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
