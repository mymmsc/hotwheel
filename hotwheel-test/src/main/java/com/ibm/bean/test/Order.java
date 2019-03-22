package com.ibm.bean.test;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class Order {
    @Valid
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public static void main(String[] args) {
        Order order = new Order();
        Person person = new Person();
        order.setPerson(person);
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        Validator validator = vf.getValidator();
        Set<ConstraintViolation<Order>> set = validator.validate(order);
        for (ConstraintViolation<Order> constraintViolation : set) {
            System.out.println(constraintViolation.getMessage());
        }
    }
}
