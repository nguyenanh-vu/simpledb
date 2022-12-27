package com.nguyenanhvu.simpledb.field.impl;

import java.nio.ByteBuffer;
import java.util.Objects;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

import lombok.Getter;
import lombok.NonNull;

public class IntegerField implements Field<Integer> {
	@NonNull
	@Getter
	private String name;

	public IntegerField(String name) {
		this.name = this.acceptedName(name);
	}

	@Override
	public Integer size() {
		return 4;
	}

	@Override
	public Class<?> accepted() {
		return Integer.class;
	}

	@Override
	public byte[] getBytes(Object o) throws IncorrectDataTypeException {
		if (o == null) {
			return new byte[4];
		} else if (o instanceof Integer) {
			return ByteBuffer.allocate(4).putInt((int) o).array();
		} else {
			throw new IncorrectDataTypeException(this.getClass(), o.getClass());
		}
	}

	@Override
	public Integer parse(byte[] buffer) {
		if (this.emptyBuffer(buffer)) {
			return null;
		} else {
			return Integer.valueOf(ByteBuffer.wrap(buffer).getInt()); 
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntegerField) {
			return this.name.contentEquals(((IntegerField) obj).name);
		} else {
			return false;
		}
	}	
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, Field.ID.get(this.accepted()));
	}
}
