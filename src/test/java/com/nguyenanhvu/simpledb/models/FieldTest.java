package com.nguyenanhvu.simpledb.models;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;

public class FieldTest {
	
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
	public void exceptionTest() {
		Exception e = new IncorrectDataTypeException("aaa");
		Assertions.assertTrue("aaa".contentEquals(e.getMessage()));
	}
	
	@Test
	public void lengthTest() {
		Assertions.assertEquals(1, new Field("a", (byte) 0, Field.Type.BOOLEAN).getLength());
		Assertions.assertEquals(4, new Field("a", (byte) 0, Field.Type.FLOAT).getLength());
		Assertions.assertEquals(4, new Field("a", (byte) 0, Field.Type.INT).getLength());
		Assertions.assertEquals(3, new Field("a", (byte) 3, Field.Type.STRING).getLength());
	}
	
	@Test
	public void parseTest() {
		Assertions.assertFalse((boolean) new Field("a", (byte) 1, Field.Type.BOOLEAN)
				.parse(this.addRand(new byte[] {Integer.valueOf(0).byteValue()}, 8)));
		
		Assertions.assertTrue((boolean) new Field("a", (byte) 1, Field.Type.BOOLEAN)
				.parse(this.addRand(new byte[] {Integer.valueOf(1).byteValue()}, 8)));
		
		Assertions.assertEquals(4.0f, (float) new Field("a", (byte) 0, Field.Type.FLOAT)
				.parse(this.addRand(ByteBuffer.allocate(4).putFloat(4.0f).array(), 8)));
		
		Assertions.assertNull(new Field("a", (byte) 0, Field.Type.FLOAT)
				.parse(this.addRand(new byte[4], 8)));
		
		Assertions.assertEquals(4, (int) new Field("a", (byte) 0, Field.Type.INT)
				.parse(this.addRand(ByteBuffer.allocate(4).putInt(4).array(), 8)));
		
		Assertions.assertNull(new Field("a", (byte) 0, Field.Type.INT)
				.parse(this.addRand(new byte[4], 8)));
		
		String str = "testString";
		
		Assertions.assertEquals(str, (String) new Field("a", (byte) str.length(), Field.Type.STRING)
				.parse(this.addRand(str.getBytes(), 8)));
		
		Assertions.assertNull(new Field("a", (byte) 4, Field.Type.STRING)
				.parse(this.addRand(new byte[4], 8)));
		
	}
	
	@Test
	public void getBytesTest() throws IncorrectDataTypeException {
		Field f;
		//Boolean
		f = new Field("a", (byte) 1, Field.Type.BOOLEAN);
		Assertions.assertTrue(Arrays.equals(new byte[] {0}, f.getByte(false)));
		Assertions.assertTrue(Arrays.equals(new byte[] {1}, f.getByte(true)));
		Assertions.assertTrue(Arrays.equals(new byte[1], f.getByte(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new Field("a", (byte) 1, Field.Type.BOOLEAN).getByte(0);});
		
		//Float
		f = new Field("a", (byte) 1, Field.Type.FLOAT);
		Assertions.assertTrue(Arrays.equals(ByteBuffer.allocate(4).putFloat(4.0f).array(), f.getByte(4.0f)));
		Assertions.assertTrue(Arrays.equals(new byte[4], f.getByte(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new Field("a", (byte) 1, Field.Type.FLOAT).getByte(0);});
		
		//Integer
		f = new Field("a", (byte) 1, Field.Type.INT);
		Assertions.assertTrue(Arrays.equals(ByteBuffer.allocate(4).putInt(4).array(), f.getByte(4)));
		Assertions.assertTrue(Arrays.equals(new byte[4], f.getByte(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new Field("a", (byte) 1, Field.Type.INT).getByte(4.0f);});
		
		//String
		String str = "testString";
		f = new Field("a", (byte) str.length(), Field.Type.STRING);
		Assertions.assertTrue(Arrays.equals(str.getBytes(), f.getByte(str)));
		Assertions.assertTrue(Arrays.equals(Arrays.copyOf(str.substring(2).getBytes(), str.length()), 
				f.getByte(str.substring(2))));
		Assertions.assertTrue(Arrays.equals((str).getBytes(), f.getByte(str + "  ")));
		Assertions.assertTrue(Arrays.equals(new byte[str.length()], f.getByte(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new Field("a", (byte) 1, Field.Type.STRING).getByte(4.0f);});
	}
}
