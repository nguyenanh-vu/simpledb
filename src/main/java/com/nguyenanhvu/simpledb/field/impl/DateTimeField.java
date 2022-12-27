package com.nguyenanhvu.simpledb.field.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

import lombok.Getter;
import lombok.NonNull;

public class DateTimeField implements Field<LocalDateTime> {

	@NonNull
	@Getter
	private String name;

	public DateTimeField(String name) {
		this.name = this.acceptedName(name);
	}
	
	@Override
	public Integer size() {
		return 8;
	}

	@Override
	public Class<?> accepted() {
		return LocalDateTime.class;
	}
	@Override
	public byte[] getBytes(Object o) throws IncorrectDataTypeException {
		if (o == null) {
			return new byte[8];
		} else if (o instanceof LocalDateTime) {
			byte[] res = new byte[8];
			LocalDateTime ts = (LocalDateTime) o;
			res[1] = (byte) (ts.getYear() / 128);
			res[2] = (byte) (ts.getYear() % 128);
			res[3] = (byte) ts.getMonthValue();
			res[4] = (byte) ts.getDayOfMonth();
			res[5] = (byte) ts.getHour();
			res[6] = (byte) ts.getMinute();
			res[7] = (byte) ts.getSecond();
			return res;
		} else {
			throw new IncorrectDataTypeException(this.getClass(), o.getClass());
		}
	}

	@Override
	public LocalDateTime parse(byte[] buffer) {
		if (this.emptyBuffer(buffer)) {
			return null;
		} else {
			byte[] b = Arrays.copyOf(buffer, 8);
			return LocalDateTime.of(((int) b[1])*128 + (int) b[2], (int) b[3], (int) b[4], 
					(int) b[5], (int) b[6], (int) b[7]);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DateTimeField) {
			return this.name.contentEquals(((DateTimeField) obj).name);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, Field.ID.get(this.accepted()));
	}
}
