package com.nguyenanhvu.simpledb.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class Table {
	private String name;
	private List<Field> fields = new ArrayList<>();
	private Map<Field, Long> indexingFields = new HashMap<>();
	private Integer size = 0;
	private Integer numFields = 0;
	private Integer numIndexingFields = 0;
	@Setter
	private Long latestIndex = 0L;
	private static Logger log = LoggerFactory.getLogger(Field.class);
	
	public void setName(String name) {
		if (name == null) {
			this.name = null;
		} else {
			if (name.length() > 32) {
				this.name = name.substring(0, 32).trim();
				log.warn(String.format("Table name too long, expected 32, %d given, string was cropped to %s", 
						name.length(), name.substring(0, 32)));
			} else {
				this.name = name.trim();
			}
		}
	}
	
	public void addField(Field f) {
		if (!this.fields.contains(f)) {
			this.fields.add(f);
			this.numFields ++;
			this.size += f.getLength();
		}
	}
	
	public void setIndexing(Field f, boolean b) {
		if (this.fields.contains(f)) {
			if (b && !this.indexingFields.containsKey(f)) {
				this.indexingFields.put(f, this.latestIndex);
				this.latestIndex ++;
				this.numIndexingFields ++;
			} else if (!b && this.indexingFields.containsKey(f)) {
				this.indexingFields.remove(f);
				this.numIndexingFields --;
			}
		}
	}
	
	public byte[] getBytes(Map<String, Object> record) throws IncorrectDataTypeException {
		byte[] res = new byte[this.size];
		int i = 0;
		for (Field f : this.fields) {
			System.arraycopy(f.getByte(record.get(f.getName())), 0, res, i, f.getLength());
			i += f.getLength();
		}
		return res;
	}
	
	public Map<String, Object> parse(byte[] buffer) {
		Map<String, Object> res = new HashMap<>();
		byte[] b = Arrays.copyOf(buffer, this.size);
		int i = 0;
		for (Field f : this.fields) {
			res.put(f.getName(), f.parse(Arrays.copyOfRange(b, i, i + f.getLength())));
			i += f.getLength();
		}
		return res;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.fields, this.indexingFields);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Table) {
			Table o = (Table) obj;
			return (this.name == null || o.name == null 
					? (this.name == null && o.name == null) 
							: this.name.contentEquals(o.name))
					&& this.indexingFields.equals(o.indexingFields)
					&& this.fields.equals(o.fields);
		} else {
			return false;
		}
	}
}
