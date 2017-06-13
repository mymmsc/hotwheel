package com.ibm.bean.test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class Dog implements Animal {
	private String name;
	private String ownername;
	
	private String type;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwnerName() {
		return ownername;
	}
	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}
	public void setType(String type) {
		this.type = type;
	}
	@NotEmpty(message = "type of the dog may be empty")
	public String getType() {
		return type;
	}
	
	public static void main(String[] args) {
		Dog dog = new Dog();
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		Validator validator = vf.getValidator();
		Set<ConstraintViolation<Dog>> set = validator.validate(dog,Animal.class);
		for (ConstraintViolation<Dog> constraintViolation : set) {
			System.out.println(constraintViolation.getMessage());
		}
	}

}
