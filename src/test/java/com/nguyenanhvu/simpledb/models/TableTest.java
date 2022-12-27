package com.nguyenanhvu.simpledb.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;
import com.nguyenanhvu.simpledb.field.impl.BooleanField;
import com.nguyenanhvu.simpledb.field.impl.IntegerField;
import com.nguyenanhvu.simpledb.field.impl.StringField;

@Isolated
public class TableTest {
	private byte[] generateRandomArray(int size) {
		byte[] res = new byte[size];
		new Random().nextBytes(res);
		return res;
	}
	
	private byte[] addRand(byte[] buffer, int size) {
		byte[] res = Arrays.copyOf(buffer, buffer.length + size);
		System.arraycopy(this.generateRandomArray(size), 0, res, buffer.length, size);
		return res;
	}
	
	@Test
	public void constructorTest() {
		TableImpl t = new TableImpl("a");
		Assertions.assertEquals("a", t.getName());
		Assertions.assertEquals(0, t.getSize());
		Assertions.assertEquals(0, t.getNumFields());
		Assertions.assertEquals(0, t.getNumIndexingFields());
		Assertions.assertEquals(0L, t.getLatestIndex());
		Assertions.assertTrue(t.getFields().isEmpty());
		Assertions.assertTrue(t.getIndexingFields().isEmpty());
	}
	
	@Test 
	public void setNameTest() {
		TableImpl t = new TableImpl("a");
		Assertions.assertEquals("a", t.getName());
		t = new TableImpl("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		Assertions.assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", t.getName());
		t = new TableImpl("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		Assertions.assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", t.getName());
	}
	
	@Test
	public void addFieldTest() {
		TableImpl t = new TableImpl("a");
		Field<?> f1 = new BooleanField("a");
		Field<?> f2 = new BooleanField("a");
		Field<?> f3 = new IntegerField("a");
		
		t.addField(f1);
		Assertions.assertFalse(t.getFields().isEmpty());
		Assertions.assertTrue(t.getIndexingFields().isEmpty());
		Assertions.assertTrue(t.getFields().contains(f1));
		Assertions.assertTrue(t.getFields().contains(f2));
		Assertions.assertEquals(1, t.getNumFields());
		Assertions.assertEquals(0, t.getNumIndexingFields());
		Assertions.assertEquals(0L, t.getLatestIndex());
		
		t.addField(f2);
		Assertions.assertFalse(t.getFields().isEmpty());
		Assertions.assertTrue(t.getIndexingFields().isEmpty());
		Assertions.assertTrue(t.getFields().contains(f1));
		Assertions.assertTrue(t.getFields().contains(f2));
		Assertions.assertEquals(1, t.getNumFields());
		Assertions.assertEquals(0, t.getNumIndexingFields());
		Assertions.assertEquals(0L, t.getLatestIndex());
		
		t.addField(f3);
		Assertions.assertFalse(t.getFields().isEmpty());
		Assertions.assertTrue(t.getIndexingFields().isEmpty());
		Assertions.assertTrue(t.getFields().contains(f1));
		Assertions.assertTrue(t.getFields().contains(f3));
		Assertions.assertEquals(2, t.getNumFields());
		Assertions.assertEquals(0, t.getNumIndexingFields());
		Assertions.assertEquals(0L, t.getLatestIndex());
	}
	
	@Test
	public void setIndexingTest() {
		TableImpl t = new TableImpl("a");
		Field<?> f1 = new BooleanField("a");
		Field<?> f2 = new BooleanField("a");
		Field<?> f3 = new IntegerField("a");
		
		t.addField(f1);
		t.addField(f2);
		
		t.setIndexing(f3, false);
		Assertions.assertTrue(t.getIndexingFields().isEmpty());
		Assertions.assertFalse(t.getIndexingFields().containsKey(f3));
		Assertions.assertEquals(1, t.getNumFields());
		Assertions.assertEquals(0, t.getNumIndexingFields());
		Assertions.assertEquals(0L, t.getLatestIndex());
		
		t.addField(f3);
		t.setIndexing(f1, false);
		Assertions.assertTrue(t.getIndexingFields().isEmpty());
		Assertions.assertFalse(t.getIndexingFields().containsKey(f1));
		Assertions.assertEquals(2, t.getNumFields());
		Assertions.assertEquals(0, t.getNumIndexingFields());
		Assertions.assertEquals(0L, t.getLatestIndex());
		
		t.setIndexing(f1, true);
		Assertions.assertFalse(t.getIndexingFields().isEmpty());
		Assertions.assertTrue(t.getIndexingFields().containsKey(f1));
		Assertions.assertEquals(0L, t.getIndexingFields().get(f1));
		Assertions.assertEquals(2, t.getNumFields());
		Assertions.assertEquals(1, t.getNumIndexingFields());
		Assertions.assertEquals(1L, t.getLatestIndex());
		
		t.setIndexing(f2, true);
		Assertions.assertFalse(t.getIndexingFields().isEmpty());
		Assertions.assertTrue(t.getIndexingFields().containsKey(f1));
		Assertions.assertEquals(0L, t.getIndexingFields().get(f1));
		Assertions.assertEquals(2, t.getNumFields());
		Assertions.assertEquals(1, t.getNumIndexingFields());
		Assertions.assertEquals(1L, t.getLatestIndex());
		
		t.setIndexing(f2, false);
		Assertions.assertTrue(t.getIndexingFields().isEmpty());
		Assertions.assertFalse(t.getIndexingFields().containsKey(f1));
		Assertions.assertNull(t.getIndexingFields().get(f1));
		Assertions.assertEquals(2, t.getNumFields());
		Assertions.assertEquals(0, t.getNumIndexingFields());
		Assertions.assertEquals(1L, t.getLatestIndex());
	}
	
	@Test
	public void hashCodeTest() {
		TableImpl t1 = new TableImpl("a");
		TableImpl t2 = new TableImpl("a");
		TableImpl t3 = new TableImpl("a");
		TableImpl t4 = new TableImpl("a");
		Field<?> f1 = new BooleanField("a");
		Field<?> f2 = new IntegerField("a");
		
		t1.addField(f1);
		t1.addField(f2);
		t2.addField(f1);
		t2.addField(f2);
		t3.addField(f1);
		t3.addField(f2);
		t4.addField(f1);
		
		t1.setIndexing(f1, true);
		t2.setIndexing(f1, true);
		t3.setIndexing(f1, true);
		t4.setIndexing(f1, true);
		t3.setIndexing(f2, true);
		
		Assertions.assertEquals(t1.hashCode(), t2.hashCode());
		Assertions.assertNotEquals(t1.hashCode(), t3.hashCode());
		Assertions.assertNotEquals(t1.hashCode(), t4.hashCode());
	}
	
	@Test
	public void testEquals() {
		TableImpl t1 = new TableImpl("a");
		TableImpl t2 = new TableImpl("aa");
		TableImpl t3 = new TableImpl("a");
		TableImpl t4 = new TableImpl("a");
		Field<?> f1 = new BooleanField("a");
		Field<?> f2 = new IntegerField("a");
		
		t1.addField(f1);
		t1.addField(f2);
		t2.addField(f1);
		t2.addField(f2);
		t3.addField(f1);
		t3.addField(f2);
		t4.addField(f1);
		
		t1.setIndexing(f1, true);
		t2.setIndexing(f1, true);
		t3.setIndexing(f1, true);
		t4.setIndexing(f1, true);
		t3.setIndexing(f2, true);
		
		Assertions.assertEquals(t1, t1);
		Assertions.assertNotEquals(t1, t2);
		Assertions.assertNotEquals(t1, t3);
		Assertions.assertNotEquals(t1, t4);
		Assertions.assertNotEquals(t1, f1);
	}
	
	@Test
	public void getBytesTest() throws IncorrectDataTypeException {
		TableImpl t1 = new TableImpl("a");
		Field<?> f1 = new BooleanField("a");
		Field<?> f2 = new StringField("b", 2);
		t1.addField(f1);
		t1.addField(f2);
		
		Map<String,Object> map = new HashMap<>();
		map.put("a", true);
		map.put("b", "aaaaa");
		
		byte[] b = t1.getBytes(map);
		Assertions.assertEquals(3, b.length);
		Assertions.assertEquals("aa", new String(Arrays.copyOfRange(b, 1, 3)));
		Assertions.assertEquals(1, b[0]);
		
		map.put("b", "aa");
		Assertions.assertEquals(map, t1.parse(this.addRand(b, 8)));
		
		map.put("b", true);
		
		Assertions.assertThrows(IncorrectDataTypeException.class, () -> {t1.getBytes(map);});
	}
}
