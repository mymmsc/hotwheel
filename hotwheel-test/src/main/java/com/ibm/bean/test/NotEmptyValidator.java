package com.ibm.bean.test;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotEmptyValidator implements ConstraintValidator<NotEmpty, String>{
	public void initialize(NotEmpty parameters) {
	}

	public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
		if (string == null) return false;
		else if(string.length()<1) return false;
		else return true;
	}
}
