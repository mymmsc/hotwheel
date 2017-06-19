package test.org.hotwheel.validator;

import org.hotwheel.spring.validator.ValidatorContext;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 用户验证
 *
 * Created by wangfeng on 2017/6/13.
 * @see <url>https://www.ibm.com/developerworks/cn/java/j-lo-beanvalid/index.html</url>
 * @version 1.0.0
 */
public class UserValidator extends ValidatorContext<User> {

    @Override
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "username", null, "Username is empty.");
        User user = (User) obj;
        if (null == user.getPassword() || "".equals(user.getPassword())) {
            errors.rejectValue("password", null, "Password is empty.");
        }
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        javax.validation.Validator validator = vf.getValidator();
        Set<ConstraintViolation<User>> set = validator.validate(user);
        if (!set.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            for (ConstraintViolation<User> cv : set) {
                if (sb.length() > 1) {
                    sb.append(",");
                }
                sb.append(String.format("参数[%s]%s", cv.getPropertyPath(), cv.getMessage()));
            }
            //throw new ApiException(MiscError.SC_EVALIDATE, sb.toString());
        }
    }
}
