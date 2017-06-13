package com.ibm.bean.test;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PatternOfStringValidator.class)
public @interface PatternOfString {
	String mustContainLetter();
	String message() default "this pattern may not be right";
	Class<?>[] groups() default { };
	Class<? extends Payload>[] payload() default {};
	
	@Target({ METHOD, FIELD, ANNOTATION_TYPE})
	@Retention(RUNTIME)
	@interface List {
		PatternOfString[] value();
	}
}
