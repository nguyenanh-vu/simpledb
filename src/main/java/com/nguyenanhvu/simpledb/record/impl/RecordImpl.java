package com.nguyenanhvu.simpledb.record.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;
import com.nguyenanhvu.simpledb.record.Record;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecordImpl implements Record{
	private List<Field<?>> fields = new ArrayList<>();
	private Integer size = 0;
	
	public RecordImpl(byte[] buffer) {
		for (int i = 32; i < buffer.length; i += 32) {
			this.addField(Field.getField(Arrays.copyOfRange(buffer, i - 32, i)));
		}
	}
	
	@Override
	public int addField(Field<?> f) {
		if (!this.fields.contains(f)) {
			this.fields.add(f);
			this.size += f.size();
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int addFields(List<Field<?>> fields) {
		int res = 0;
		for (Field<?> f : fields) {
			res += this.addField(f);
		}
		return res;
	}

	@Override
	public Integer getNumFields() {
		return this.fields.size();
	}

	@Override
	public byte[] getBytes() {
		byte[] res = new byte[32 * this.fields.size()];
		int i = 0;
		for (Field<?> f : this.fields) {
			System.arraycopy(f.getBytes(), 0, res, i, 32);
			i += 32;
		}
		return res;
	}
	
	public byte[] getBytes(Map<String, Object> record) throws IncorrectDataTypeException {
		byte[] res = new byte[this.size];
		int i = 0;
		for (Field<?> f : this.fields) {
			System.arraycopy(f.getBytes(record.get(f.getName())), 0, res, i, f.size());
			i += f.size();
		}
		return res;
	}
	
	public Map<String, Object> parse(byte[] buffer) {
		Map<String, Object> res = new HashMap<>();
		byte[] b = Arrays.copyOf(buffer, this.size);
		int i = 0;
		for (Field<?> f : this.fields) {
			res.put(f.getName(), f.parse(Arrays.copyOfRange(b, i, i + f.size())));
			i += f.size();
		}
		return res;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(this.fields);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Record) {
			Record o = (Record) obj;
			return this.fields.equals(o.getFields());
		} else {
			return false;
		}
	}
}
