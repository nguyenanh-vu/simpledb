package com.nguyenanhvu.simpledb.models;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
		INTEGER,
		FLOAT,
		BOOLEAN,
		STRING,
		DATETIME
	}
	private static Logger log = LoggerFactory.getLogger(Field.class);
	private String name;
	private Type type;
	private Integer length;
	private Map<String, List<Integer>> map = new HashMap<>();
	
	public Field(Integer length, Field.Type type) {
		this.type = type;
		this.length = Field.length(length, type);
	}
	
	public Field(String name, Integer length, Field.Type type) {
		this.name = name;
		this.type = type;
		this.length = Field.length(length, type);
	}
	
	private static Integer length(Integer length, Field.Type type) {
		switch (type) {
		case STRING:
			return length;
		case BOOLEAN:
			return 1;
		case FLOAT:
			return 4;
		case INTEGER:
			return 4;
		case DATETIME:
			return 8;
		default:
			return 0;
		}
	}
	
	public Object parse(byte[] buffer) {
		if (this.type != Type.BOOLEAN && Arrays.equals(
				Arrays.copyOf(buffer, this.length), 
				new byte[this.length])) {
			return null;
		} else {
			switch (this.type) {
				case INTEGER:
					return Integer.valueOf(ByteBuffer.wrap(buffer).getInt()); 
				case FLOAT:
					return Float.valueOf(ByteBuffer.wrap(buffer).getFloat());
				case BOOLEAN:
					return buffer[0] != 0;
				case STRING:
					return new String(ByteBuffer.wrap(Arrays.copyOf(buffer, this.length))
							.array(), StandardCharsets.UTF_8);
				case DATETIME:
					byte[] b = Arrays.copyOf(buffer, 8);
					return LocalDateTime.of(((int) b[1])*128 + (int) b[2], (int) b[3], (int) b[4], 
							(int) b[5], (int) b[6], (int) b[7]);
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
				case INTEGER:
					if (o instanceof Integer) {
						return ByteBuffer.allocate(4).putInt((int) o).array();
					} else {
						throw new IncorrectDataTypeException("Integer", o.getClass().getName());
					}
				case STRING:
					if (o instanceof String) {
						String s = (String) o;
						if (s.length() > this.length) {
							log.warn(String.format("String too long, expected %s, %s given, string was cropped", 
									this.length, s.length()));
						}
						return Arrays.copyOf(s.getBytes(), this.length);	
					} else {
						throw new IncorrectDataTypeException("String", o.getClass().getName());
					}
				case DATETIME:
					if (o instanceof LocalDateTime) {
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
						throw new IncorrectDataTypeException("LocalDateTime", o.getClass().getName());
					}
			}
			return null;
		}
	}
}
