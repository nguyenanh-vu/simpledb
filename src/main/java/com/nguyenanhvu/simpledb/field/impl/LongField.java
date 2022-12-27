package com.nguyenanhvu.simpledb.field.impl;

import java.nio.ByteBuffer;
import java.util.Objects;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

import lombok.Getter;
import lombok.NonNull;

public class LongField implements Field<Long> {
	@NonNull
	@Getter
	private String name;

	public LongField(String name) {
		this.name = this.acceptedName(name);
	}

	@Override
	public Integer size() {
		return 8;
	}

	@Override
	public Class<?> accepted() {
		return Long.class;
	}

	@Override
	public byte[] getBytes(Object o) throws IncorrectDataTypeException {
		if (o == null) {
			return new byte[8];
		} else if (o instanceof Long) {
			return ByteBuffer.allocate(8).putLong((long) o).array();
		} else {
			throw new IncorrectDataTypeException(this.getClass(), o.getClass());
		}
	}

	@Override
	public Long parse(byte[] buffer) {
		if (this.emptyBuffer(buffer)) {
			return null;
		} else {
			return Long.valueOf(ByteBuffer.wrap(buffer).getLong()); 
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LongField) {
			return this.name.contentEquals(((LongField) obj).name);
		} else {
			return false;
		}
	}	
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, Field.ID.get(this.accepted()));
	}
}