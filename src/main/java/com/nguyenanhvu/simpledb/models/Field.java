package com.nguyenanhvu.simpledb.models;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;

import lombok.Getter;

@Getter
public class Field {
	public enum Type {
		INT,
		FLOAT,
		BOOLEAN,
		STRING
	}
	private static Logger log = LoggerFactory.getLogger(Field.class);
	private String name;
	private Byte size = 1;
	private Type type;
	private Integer length;
	private Map<String, List<Integer>> map = new HashMap<>();
	
	public Field(String name, Byte size, Type type) {
		this.name = name;
		this.type = type;
		if (type.equals(Type.STRING)) {
			this.size = size;
		}
		this.length = length();
	}
	
	public Integer length() {
		if (this.type.equals(Type.STRING)) {
			return (int) this.size;
		} else if (this.type.equals(Type.BOOLEAN)) {
			return 1;
		} else {
			return 4;
		}
	}
	
	public Object parse(byte[] buffer) {
		if (this.type != Type.BOOLEAN && Arrays.equals(
				Arrays.copyOf(buffer, this.length), 
				new byte[this.length])) {
			return null;
		} else {
			switch (this.type) {
				case INT:
					return Integer.valueOf(ByteBuffer.wrap(buffer).getInt()); 
				case FLOAT:
					return Float.valueOf(ByteBuffer.wrap(buffer).getFloat());
				case BOOLEAN:
					return buffer[0] != 0;
				case STRING:
					return new String(ByteBuffer.wrap(Arrays.copyOf(buffer, this.length))
							.array(), StandardCharsets.UTF_8);
			}
			return null;
		}
	}
	
	public byte[] getByte(Object o) throws IncorrectDataTypeException {
		if (o == null) {
			return new byte[this.length];
		} else {
			switch (this.type) {
				case BOOLEAN:
					if (o instanceof Boolean) {
						return new byte[] 
								{(byte) ((boolean) o ? 1 : 0)}; 
					} else {
						throw new IncorrectDataTypeException("Boolean", o.getClass().getName());
					}
				case FLOAT:
					if (o instanceof Float) {
						return ByteBuffer.allocate(4).putFloat((float) o).array();
					} else {
						throw new IncorrectDataTypeException("Float", o.getClass().getName());
					}
				case INT:
					if (o instanceof Integer) {
						return ByteBuffer.allocate(4).putInt((int) o).array();
					} else {
						throw new IncorrectDataTypeException("Integer", o.getClass().getName());
					}
				case STRING:
					if (o instanceof String) {
						String s = (String) o;
						if (s.length() > this.length) {
							log.warn(String.format("String too long, expected %s, given %s, string was cropped", 
									this.length, s.length()));
						}
						return Arrays.copyOf(s.getBytes(), this.length);	
					} else {
						throw new IncorrectDataTypeException("String", o.getClass().getName());
					}
			}
			return null;
		}
	}
}
