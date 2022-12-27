package com.nguyenanhvu.simpledb.table;

import java.util.List;
import java.util.Map;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;

public interface Table {
	public String getName();
	
	public List<Field<?>> getFields();
	
	public Map<Field<?>, Long> getIndexingFields();
	
	public Integer getSize();
	
	public Integer getNumFields();
	
	public Integer getNumIndexingFields();
	
	public Long getLatestIndex();
	
	public void setLatestIndex(Long l);
	
	public void addField(Field<?> f);
	
	public void setIndexing(Field<?> f, boolean b) ;
	
	public byte[] getBytes(Map<String, Object> record) throws IncorrectDataTypeException ;
	
	public Map<String, Object> parse(byte[] buffer) ;
}
