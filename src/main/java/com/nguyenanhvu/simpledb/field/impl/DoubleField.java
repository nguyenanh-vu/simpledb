package com.nguyenanhvu.simpledb.field.impl;

import java.nio.ByteBuffer;
import java.util.Objects;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

import lombok.Getter;
import lombok.NonNull;

public class DoubleField implements Field<Double> {
	@NonNull
	@Getter
	private String name;

	public DoubleField(String name) {
		this.name = this.acceptedName(name);
	}

	@Override
	public Integer size() {
		return 8;
	}

	@Override
	public Class<?> accepted() {
		return Double.class;
	}

	@Override
	public byte[] getBytes(Object o) throws IncorrectDataTypeException {
		if (o == null) {
			return new byte[8];
		} else if (o instanceof Double) {
			return ByteBuffer.allocate(8).putDouble((double) o).array();
		} else {
			throw new IncorrectDataTypeException(this.getClass(), o.getClass());
		}
	}

	@Override
	public Double parse(byte[] buffer) {
		if (this.emptyBuffer(buffer)) {
			return null;
		} else {
			return Double.valueOf(ByteBuffer.wrap(buffer).getDouble());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DoubleField) {
			return this.name.contentEquals(((DoubleField) obj).name);
		} else {
			return false;
		}
	}	
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, Field.ID.get(this.accepted()));
	}
}
