package com.nguyenanhvu.simpledb.field.impl;

import java.util.Arrays;
import java.util.Objects;

import org.slf4j.LoggerFactory;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

import lombok.Getter;
import lombok.NonNull;

public class StringField implements Field<String> {
	@NonNull
	@Getter
	private String name;
	@NonNull
	private Integer size;

	public StringField(String name, Integer size) {
		this.name = this.acceptedName(name);
		this.size = size;
	}

	@Override
	public Integer size() {
		return this.size;
	}

	@Override
	public Class<?> accepted() {
		return String.class;
	}

	@Override
	public byte[] getBytes(Object o) throws IncorrectDataTypeException {
		if (o == null) {
			return new byte[this.size];
		} else if (o instanceof String) {
			String s = (String) o;
			if (s.length() > this.size) {
				LoggerFactory.getLogger(this.getClass())
				.warn(String.format("String too long, expected %d, %d given, string was cropped to %s", 
						this.size, s.length(), s.substring(0, this.size)));
			}
			return Arrays.copyOf(s.getBytes(), this.size);	
		} else {
			throw new IncorrectDataTypeException(this.getClass(), o.getClass());
		}
	}

	@Override
	public String parse(byte[] buffer) {
		if (this.emptyBuffer(buffer)) {
			return null;
		} else {
			return new String(Arrays.copyOf(buffer, this.size)).trim();
		}
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringField) {
			return this.name.contentEquals(((StringField) obj).name) &&
					this.size.equals(((StringField) obj).size);
		} else {
			return false;
		}
	}	
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, Field.ID.get(this.accepted()), this.size);
	}
}
