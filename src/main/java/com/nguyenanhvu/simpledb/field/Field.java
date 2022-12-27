package com.nguyenanhvu.simpledb.field;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.impl.BooleanField;
import com.nguyenanhvu.simpledb.field.impl.DateTimeField;
import com.nguyenanhvu.simpledb.field.impl.DoubleField;
import com.nguyenanhvu.simpledb.field.impl.FloatField;
import com.nguyenanhvu.simpledb.field.impl.IntegerField;
import com.nguyenanhvu.simpledb.field.impl.LongField;
import com.nguyenanhvu.simpledb.field.impl.StringField;

public interface Field<T> {
	
	static final Map<Class<?>, Integer> ID = new HashMap<Class<?>, Integer>() {
		private static final long serialVersionUID = 4032142109454296439L; {
		put(Boolean.class, 1);
		put(LocalDateTime.class, 2);
		put(Float.class, 3);
		put(Integer.class, 4);
		put(String.class, 5);
		put(Long.class, 6);
		put(Double.class, 7);
	}};
	
	public String getName();
	
	public Integer size();
	
	public Class<?> accepted();
	
	public byte[] getBytes(Object o) throws IncorrectDataTypeException;
	
	public T parse(byte[] buffer);
	
	default public boolean emptyBuffer(byte[] buffer) {
		return Arrays.equals(Arrays.copyOf(buffer, this.size()), new byte[this.size()]);
	}
	
	default public String acceptedName(String name) {
		if (name.trim().length() > 27) {
			LoggerFactory.getLogger(this.getClass()).warn("");
			return name.trim().substring(0, 27);
		} else {
			return name.trim();
		}
	}
	
	default public byte[] getBytes() {
		byte[] res = new byte[32];
		System.arraycopy(Arrays.copyOf(this.getName().getBytes(), 27), 0, res, 0, 27);	
		System.arraycopy(ByteBuffer.allocate(4).putInt(this.size()).array(), 0, res, 28, 4);
		res[27] = Field.ID.get(this.accepted()).byteValue();
		return res;
	}
	
	public static Field<?> getField(byte type, String name) {
		return getField(type, name, 0);
	}
	
	public static Field<?> getField(byte type, String name, Integer size) {
		switch (type) {
		case 1:
			return new BooleanField(name);
		case 2:
			return new DateTimeField(name);
		case 3:
			return new FloatField(name);
		case 4:
			return new IntegerField(name);
		case 5:
			return new StringField(name, size);
		case 6:
			return new LongField(name);
		case 7:
			return new DoubleField(name);
		default:
			return null;
		}
	}
	
	public static Field<?> getField(byte[] buffer) {
		byte[] b = Arrays.copyOf(buffer, 32);
		String name = new String(Arrays.copyOf(b, 27)).trim();
		Integer size = (b[27] == 5) ? Integer.valueOf(ByteBuffer
						.wrap(Arrays.copyOfRange(b, 28, 32)).getInt()) : 0;
		return getField(b[27], name, size);
	}
}
