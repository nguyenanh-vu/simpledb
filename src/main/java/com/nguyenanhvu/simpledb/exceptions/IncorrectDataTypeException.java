package com.nguyenanhvu.simpledb.exceptions;

public class IncorrectDataTypeException extends Exception {
	private static final long serialVersionUID = -8315446668311174055L;

	public IncorrectDataTypeException(String errorMessage) {
		super(errorMessage);
	}
	
	public IncorrectDataTypeException(String expected, String instead) {
		super(String.format("Expected %s, instead %s", expected, instead));
	}
}