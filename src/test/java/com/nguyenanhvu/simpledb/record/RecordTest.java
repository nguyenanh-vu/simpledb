package com.nguyenanhvu.simpledb.record;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.Field;
import com.nguyenanhvu.simpledb.field.impl.BooleanField;
import com.nguyenanhvu.simpledb.field.impl.IntegerField;
import com.nguyenanhvu.simpledb.record.impl.RecordImpl;

@Isolated
public class RecordTest {
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
		Record r = new RecordImpl();
		Assertions.assertEquals(0, r.getNumFields());
		Assertions.assertEquals(0, r.getSize());
		Assertions.assertTrue(r.getFields().isEmpty());
	}
	
	@Test
	public void addFieldTest() {
		Record r = new RecordImpl();
		Field<?> f1, f2;
		f1 = new IntegerField("a");
		f2 = new BooleanField("b");
		
		Assertions.assertEquals(2, r.addFields(Arrays.asList(f1, f2)));
		Assertions.assertEquals(0, r.addField(f1));
		Assertions.assertEquals(2, r.getNumFields());
		Assertions.assertEquals(5, r.getSize());
		Assertions.assertFalse(r.getFields().isEmpty());
		Assertions.assertTrue(r.getFields().contains(f1));
		Assertions.assertEquals(0, r.getFields().indexOf(f1));
	}
	
	@Test
	public void equalsTest() {
		Record r1 = new RecordImpl();
		Record r2 = new RecordImpl();
		Record r3 = new RecordImpl();
		Field<?> f1, f2;
		List<Field<?>> f = new ArrayList<>();
		f1 = new IntegerField("a");
		f2 = new BooleanField("b");
		f.add(f2);
		
		r1.addFields(Arrays.asList(f1, f2));
		r2.addFields(Arrays.asList(f1, f2));
		r3.addField(f2);
		
		Assertions.assertNotEquals(r1, 1);
		
		Assertions.assertEquals(r1, r1);
		Assertions.assertEquals(r1.hashCode(), r1.hashCode());
		Assertions.assertEquals(r1, r2);
		Assertions.assertEquals(r1.hashCode(), r2.hashCode());
		Assertions.assertEquals(r2, r1);
		Assertions.assertNotEquals(r1, r3);
		Assertions.assertNotEquals(r1.hashCode(), r3.hashCode());
		Assertions.assertEquals(Objects.hashCode(f), r3.hashCode());
		f.set(0, f1);
		f.add(f2);
		Assertions.assertEquals(Objects.hashCode(f), r1.hashCode());
	}
	
	@Test
	public void getBytesHeaderTest() {
		Record r = new RecordImpl();
		Field<?> f1, f2;
		byte[] b;
		f1 = new IntegerField("a");
		f2 = new BooleanField("b");
		r.addFields(Arrays.asList(f1, f2));
		
		b = r.getBytes();
		Assertions.assertEquals(64, b.length);
		Assertions.assertTrue(Arrays.equals(f1.getBytes(), Arrays.copyOf(b, 32)));
		Assertions.assertTrue(Arrays.equals(f2.getBytes(), Arrays.copyOfRange(b, 32, 64)));
		
		Assertions.assertEquals(r, new RecordImpl(this.addRand(b, 8)));
	}
	
	@Test
	public void getBytesTest() throws IncorrectDataTypeException {
		Record r = new RecordImpl();
		Field<?> f1, f2;
		byte[] b;
		f1 = new IntegerField("a");
		f2 = new BooleanField("b");
		r.addFields(Arrays.asList(f1, f2));
		Map<String, Object> map = new HashMap<>();
		map.put("a", "a");
		Assertions.assertThrows(IncorrectDataTypeException.class, () -> {r.getBytes(map);});
		map.put("a", 5);
		b = r.getBytes(map);
		Assertions.assertEquals(5, b.length);
		Assertions.assertEquals(5, ByteBuffer.wrap(Arrays.copyOf(b, 4)).getInt());
		Assertions.assertEquals(0, b[4]);
		map.put("b", true);
		b = r.getBytes(map);
		Assertions.assertEquals(5, b.length);
		Assertions.assertEquals(5, ByteBuffer.wrap(Arrays.copyOf(b, 4)).getInt());
		Assertions.assertEquals(1, b[4]);
	}
	
	@Test
	public void parseTest() throws IncorrectDataTypeException {
		Record r = new RecordImpl();
		Field<?> f1, f2;
		byte[] b;
		f1 = new IntegerField("a");
		f2 = new BooleanField("b");
		r.addFields(Arrays.asList(f1, f2));
		Map<String, Object> map = new HashMap<>();
		map.put("a", 5);
		map.put("b", true);
		b = r.getBytes(map);
		
		Assertions.assertEquals(map, r.parse(this.addRand(b, 8)));
	}
}
