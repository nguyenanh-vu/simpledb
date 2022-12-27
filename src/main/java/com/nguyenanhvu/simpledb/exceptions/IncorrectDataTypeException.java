package com.nguyenanhvu.simpledb.exceptions;

public class IncorrectDataTypeException extends Exception {
	private static final long serialVersionUID = -8315446668311174055L;

	public IncorrectDataTypeException(Class<?> expected, Class<?> given) {
		super(String.format("Expected %s, %s given", expected.getName(), given.getName()));
	} 
}