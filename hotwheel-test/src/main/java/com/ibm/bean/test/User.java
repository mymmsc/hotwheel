package com.ibm.bean.test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.Set;

public class User {
    @NotEmpty(message = "firstname may be empty")
    private String firstname;

    @NotEmpty(message = "middlename may be empty", groups = Default.class)
    private String middlename;

    @NotEmpty(message = "lastname may be empty", groups = GroupA.class)
    private String lastname;

    @NotEmpty(message = "country may be empty", groups = GroupB.class)
    private String country;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static void main(String[] args) {
        User user = new User();
        user.setFirstname("firstname");
        user.setMiddlename("midlename");
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        Validator validator = vf.getValidator();
        Set<ConstraintViolation<User>> set = validator.validate(user, Group.class);
        for (ConstraintViolation<User> constraintViolation : set) {
            System.out.println(constraintViolation.getMessage());
        }
    }
}
