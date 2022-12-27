package com.nguyenanhvu.simpledb.field.impl;

import java.nio.ByteBuffer;
import java.util.Objects;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

import lombok.Getter;
import lombok.NonNull;

public class FloatField implements Field<Float>{
	@NonNull
	@Getter
	private String name;

	public FloatField(String name) {
		this.name = this.acceptedName(name);
	}
	
	@Override
	public Integer size() {
		return 4;
	}

	@Override
	public Class<?> accepted() {
		return Float.class;
	}

	@Override
	public byte[] getBytes(Object o) throws IncorrectDataTypeException {
		if (o == null) {
			return new byte[4];
		} else if (o instanceof Float) {
			return ByteBuffer.allocate(4).putFloat((float) o).array();
		} else {
			throw new IncorrectDataTypeException(this.getClass(), o.getClass());
		}
	}

	@Override
	public Float parse(byte[] buffer) {
		if (this.emptyBuffer(buffer)) {
			return null;
		} else {
			return Float.valueOf(ByteBuffer.wrap(buffer).getFloat());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FloatField) {
			return this.name.contentEquals(((FloatField) obj).name);
		} else {
			return false;
		}
	}	
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, Field.ID.get(this.accepted()));
	}
}
