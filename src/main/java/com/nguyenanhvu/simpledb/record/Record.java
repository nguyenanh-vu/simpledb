package com.nguyenanhvu.simpledb.record;

import java.util.List;
import java.util.Map;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

public interface Record {
	public List<Field<?>> getFields();
	
	public Integer getSize();
	
	public Integer getNumFields();
	
	public int addField(Field<?> f);
	
	public int addFields(List<Field<?>> fields);
	
	public byte[] getBytes();
	
	public byte[] getBytes(Map<String, Object> record) throws IncorrectDataTypeException ;
	
	public Map<String, Object> parse(byte[] buffer) ;
}
