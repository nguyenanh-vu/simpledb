package com.nguyenanhvu.simpledb.models;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

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
	
	public Field(Integer length, Field.Type type) {
		this.type = type;
		this.length = Field.length(length, type);
	}
	
	public Field(byte[] buffer) {
		byte[] b = Arrays.copyOf(buffer, 32);
		if (!Arrays.equals(new byte[27], Arrays.copyOf(b, 27))) {
			this.name = new String(Arrays.copyOf(b, 27)).trim();	
		}
		switch (b[27]) {
			case 1:
				this.type = Field.Type.BOOLEAN;
				break;
			case 2:
				this.type = Field.Type.DATETIME;
				break;
			case 3:
				this.type = Field.Type.FLOAT;
				break;
			case 4:
				this.type = Field.Type.INTEGER;
				break;
			case 5:
				this.type = Field.Type.STRING;
				break;
			default:
				break;
		}
		this.length = Field.length(Integer.valueOf(ByteBuffer
				.wrap(Arrays.copyOfRange(b, 28, 32)).getInt())
				, this.type);
	}
	
	public Field(String name, Integer length, Field.Type type) {
		if (name != null && name.length() > 27) {
			this.name = name.substring(0, 27).trim();
			log.warn(String.format("Field name too long, expected 27, %d given, string was cropped to %s", 
					name.length(), name.substring(0, 27)));
		} else {
			if (name != null) {
				this.name = name.trim();	
			} else {
				this.name = null;
			}
		}
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
					return new String(Arrays.copyOf(buffer, this.length)).trim();
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
							log.warn(String.format("String too long, expected %d, %d given, string was cropped to %s", 
									this.length, s.length(), s.substring(0, this.length)));
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
	
	public byte[] getBytes() {
		byte[] res = new byte[32];
		if (this.name != null) {
			System.arraycopy(Arrays.copyOf(this.name.getBytes(), 27), 0, res, 0, 27);	
		}
		System.arraycopy(ByteBuffer.allocate(4).putInt(this.length).array(), 0, res, 28, 4);
		byte b;
		switch (this.type) {
			case BOOLEAN:
				b = 1;
				break;
			case DATETIME:
				b = 2;
				break;
			case FLOAT:
				b = 3;
				break;
			case INTEGER:
				b = 4;
				break;
			case STRING:
				b = 5;
				break;
			default:
				b = 0;
				break;
		}
		res[27] = b;
		return res;
	}
	
	private int hash() {
		switch (this.type) {
		case BOOLEAN:
			return 1;
		case DATETIME:
			return 2;
		case FLOAT:
			return 3;
		case INTEGER:
			return 4;
		case STRING:
			return 5;
		default:
			return 0;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.hash(), this.length);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Field) {
			Field o = (Field) obj;
			return (this.name == null || o.name == null 
					? (this.name == null && o.name == null ) 
							: this.name.contentEquals(o.name))
					&& this.length == o.length
					&& this.type.equals(o.type);
		} else {
			return false;
		}
	}
}
