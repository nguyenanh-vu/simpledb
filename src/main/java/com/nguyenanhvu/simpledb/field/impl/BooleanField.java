package com.nguyenanhvu.simpledb.field.impl;

import java.util.Objects;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

import lombok.Getter;
import lombok.NonNull;

public class BooleanField implements Field<Boolean> {
	@NonNull
	@Getter
	private String name;
	
	public BooleanField(String name) {
		this.name = this.acceptedName(name);
	}
	
	@Override
	public Integer size() {
		return 1;
	}

	@Override
	public Class<?> accepted() {
		return Boolean.class;
	}

	@Override
	public byte[] getBytes(Object o) throws IncorrectDataTypeException {
		if (o == null) {
			return new byte[] {0};
		} else if (o instanceof Boolean) {
			return new byte[] {(byte) ((boolean) o ? 1 : 0)}; 
		} else {
			throw new IncorrectDataTypeException(this.getClass(), o.getClass());
		}
	}

	@Override
	public Boolean parse(byte[] buffer) {
		return buffer[0] != 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BooleanField) {
			return this.name.contentEquals(((BooleanField) obj).name);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, Field.ID.get(this.accepted()));
	}
}
