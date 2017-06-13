package com.ibm.bean.test;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotEmptyValidator2 implements ConstraintValidator<NotEmpty2, String>{
	public void initialize(NotEmpty2 parameters) {
	}

	public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
		return true;
	}
}
