package com.ibm.bean.test;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PatternOfStringValidator implements ConstraintValidator<PatternOfString, String> {
    private String letterIn;

    @Override
    public void initialize(PatternOfString parameters) {
        this.letterIn = parameters.mustContainLetter();
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        if (string.contains(letterIn)) {
            return true;
        }
        return false;
    }
}
