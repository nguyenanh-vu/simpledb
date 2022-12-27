package com.nguyenanhvu.simpledb.field;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.nguyenanhvu.simpledb.AppTest;
import com.nguyenanhvu.simpledb.exceptions.IncorrectDataTypeException;
import com.nguyenanhvu.simpledb.field.impl.BooleanField;
import com.nguyenanhvu.simpledb.field.impl.DateTimeField;
import com.nguyenanhvu.simpledb.field.impl.DoubleField;
import com.nguyenanhvu.simpledb.field.impl.FloatField;
import com.nguyenanhvu.simpledb.field.impl.IntegerField;
import com.nguyenanhvu.simpledb.field.impl.LongField;
import com.nguyenanhvu.simpledb.field.impl.StringField;

@Isolated
public class FieldTest {
	private static ByteArrayOutputStream outContent;
    private static PrintStream printStream;
	
	@BeforeAll
    public static void init() {
		if (AppTest.outContent == null) {
			FieldTest.outContent = new ByteArrayOutputStream();
			FieldTest.printStream = new PrintStream(FieldTest.outContent);
			System.setOut(FieldTest.printStream);
		} else {
			FieldTest.outContent = AppTest.outContent;
		}
	}
		
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
		Assertions.assertEquals("a", new FloatField("a").getName());
		Assertions.assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaa", 
				new FloatField("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").getName());
		Assertions.assertTrue(FieldTest.outContent.toString().contains("WARN"));
		Assertions.assertEquals(1, new BooleanField("a").size());
		Assertions.assertEquals(8, new DateTimeField("a").size());
		Assertions.assertEquals(4, new FloatField("a").size());
		Assertions.assertEquals(4, new IntegerField("a").size());
		Assertions.assertEquals(8, new StringField("a", 8).size());
		Assertions.assertEquals(8, new LongField("a").size());
		Assertions.assertEquals(8, new DoubleField("a").size());
	}
	
	@Test
	public void getBytesHeaderTest() {
		Field<?> f = new StringField("a", 8);
		Field<?> f2;
		byte[] b = f.getBytes();
		Assertions.assertEquals(32, b.length);
		Assertions.assertEquals("a", new String(Arrays.copyOf(b, 32)).trim());
		Assertions.assertEquals(5, b[27]);
		Assertions.assertEquals(8, Integer.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(b, 28, 32)).getInt()));
		Assertions.assertEquals(f, Field.getField(b));
		
		for (byte i = 1; i < 8; i++) {
			f = Field.getField(i, "a");
			f2 = Field.getField(f.getBytes());
			Assertions.assertEquals(f.getName(), f2.getName());
			Assertions.assertEquals(f, f2);
		}
	}
	
	@Test
	public void equalsTest() {
		Field<?> f1, f2, f3, f4, f5;
		
		for (byte i = 1; i < 8; i++) {
			byte j = (i == 7) ? (byte) 1 : (byte) (i + 1);
			f1 = Field.getField(i, "a", 0);
			f2 = Field.getField(i, "a", 0);
			f3 = Field.getField(i, "b", 0);
			f4 = Field.getField(i, "a", 5);
			f5 = Field.getField(j, "a", 0);
			
			Assertions.assertEquals(f1, f1);
			Assertions.assertEquals(f1.hashCode(), f1.hashCode());
			if (i == 5) {
				Assertions.assertEquals(Objects.hash("a", i, 0), f1.hashCode());
				Assertions.assertEquals(Objects.hash("a", i, 0), f2.hashCode());
				Assertions.assertEquals(Objects.hash("b", i, 0), f3.hashCode());
				Assertions.assertEquals(Objects.hash("a", i, 5), f4.hashCode());
			} else {
				Assertions.assertEquals(Objects.hash("a", i), f1.hashCode());
				Assertions.assertEquals(Objects.hash("a", i), f2.hashCode());
				Assertions.assertEquals(Objects.hash("b", i), f3.hashCode());
				Assertions.assertEquals(Objects.hash("a", i), f4.hashCode());
			}
			
			Assertions.assertEquals(f1, f2);
			Assertions.assertEquals(f1.hashCode(), f2.hashCode());
			
			Assertions.assertNotEquals(f1, f3);
			Assertions.assertNotEquals(f1.hashCode(), f3.hashCode());
			
			Assertions.assertNotEquals(f1, f5);
			Assertions.assertNotEquals(f1.hashCode(), f5.hashCode());
			
			if (i == 5) {
				Assertions.assertNotEquals(f1, f4);
				Assertions.assertNotEquals(f1.hashCode(), f4.hashCode());
				Assertions.assertEquals(Objects.hash("a", i, 5), f4.hashCode());
			} else {
				Assertions.assertEquals(f1, f4);
				Assertions.assertEquals(f1.hashCode(), f4.hashCode());
				Assertions.assertEquals(Objects.hash("a", i), f4.hashCode());
			}
		}
	}
	
	@Test
	public void parseTest() {
		Assertions.assertFalse((boolean) new BooleanField("a")
				.parse(this.addRand(new byte[] {Integer.valueOf(0).byteValue()}, 8)));
		
		Assertions.assertTrue((boolean) new BooleanField("a")
				.parse(this.addRand(new byte[] {Integer.valueOf(1).byteValue()}, 8)));
		
		Assertions.assertEquals(4.0f, (float) new FloatField("a")
				.parse(this.addRand(ByteBuffer.allocate(4).putFloat(4.0f).array(), 8)));
		
		Assertions.assertNull(new FloatField("a")
				.parse(this.addRand(new byte[4], 8)));
		
		Assertions.assertEquals(4, (int) new IntegerField("a")
				.parse(this.addRand(ByteBuffer.allocate(4).putInt(4).array(), 8)));
		
		Assertions.assertNull(new IntegerField("a")
				.parse(this.addRand(new byte[4], 8)));
		
		String str = "testString";
		
		Assertions.assertEquals(str, (String) new StringField("a", str.length())
				.parse(this.addRand(str.getBytes(), 8)));
		
		Assertions.assertNull(new StringField("a", 4)
				.parse(this.addRand(new byte[4], 8)));
		
		Assertions.assertEquals(LocalDateTime.parse("2022-12-27T10:49:00"), 
				(LocalDateTime) new DateTimeField("a")
				.parse(new byte[] {0, 15, 102, 12, 27, 10, 49, 0}));
		
		Assertions.assertNull(new DateTimeField("a")
				.parse(this.addRand(new byte[8], 8)));
		
		Assertions.assertEquals(4.0d, (double) new DoubleField("a")
				.parse(this.addRand(ByteBuffer.allocate(8).putDouble(4.0d).array(), 8)));
		
		Assertions.assertNull(new DoubleField("a")
				.parse(this.addRand(new byte[8], 8)));
		
		Assertions.assertEquals(4L, (long) new LongField("a")
				.parse(this.addRand(ByteBuffer.allocate(8).putLong(4L).array(), 8)));
		
		Assertions.assertNull(new LongField("a")
				.parse(this.addRand(new byte[8], 8)));
		
	}
	
	@Test
	public void getBytesTest() throws IncorrectDataTypeException {
		Field<?> f;
		//Boolean
		f = new BooleanField("a");
		Assertions.assertTrue(Arrays.equals(new byte[] {0}, f.getBytes(false)));
		Assertions.assertTrue(Arrays.equals(new byte[] {1}, f.getBytes(true)));
		Assertions.assertTrue(Arrays.equals(new byte[1], f.getBytes(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new BooleanField("a").getBytes(0);});
		
		//Float
		f = new FloatField("a");
		Assertions.assertTrue(Arrays.equals(ByteBuffer.allocate(4).putFloat(4.0f).array(), f.getBytes(4.0f)));
		Assertions.assertTrue(Arrays.equals(new byte[4], f.getBytes(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new FloatField("a").getBytes(0);});
		
		//Integer
		f = new IntegerField("a");
		Assertions.assertTrue(Arrays.equals(ByteBuffer.allocate(4).putInt(4).array(), f.getBytes(4)));
		Assertions.assertTrue(Arrays.equals(new byte[4], f.getBytes(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new IntegerField("a").getBytes(4.0f);});
		
		//String
		String str = "testString";
		f = new StringField("a", str.length());
		Assertions.assertTrue(Arrays.equals(str.getBytes(), f.getBytes(str)));
		Assertions.assertTrue(Arrays.equals(Arrays.copyOf(str.substring(2).getBytes(), str.length()), 
				f.getBytes(str.substring(2))));
		Assertions.assertTrue(Arrays.equals((str).getBytes(), f.getBytes(str + "  ")));
		Assertions.assertTrue(FieldTest.outContent.toString()
				.contains("WARN: String too long, expected 10, 12 given, string was cropped"));
		Assertions.assertTrue(Arrays.equals(new byte[str.length()], f.getBytes(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new StringField("a", 1).getBytes(4.0f);});
		
		//Date
		f = new DateTimeField("a");
		Assertions.assertTrue(Arrays.equals(new byte[] {0, 15, 102, 12, 27, 10, 49, 0}, 
				f.getBytes(LocalDateTime.parse("2022-12-27T10:49:00"))));
		Assertions.assertTrue(Arrays.equals(new byte[8], f.getBytes(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new DateTimeField("a").getBytes(4.0f);});
		
		//Double
		f = new DoubleField("a");
		Assertions.assertTrue(Arrays.equals(ByteBuffer.allocate(8).putDouble(4.0d).array(), f.getBytes(4.0d)));
		Assertions.assertTrue(Arrays.equals(new byte[8], f.getBytes(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new DoubleField("a").getBytes(0);});
		
		//Integer
		f = new LongField("a");
		Assertions.assertTrue(Arrays.equals(ByteBuffer.allocate(8).putLong(4L).array(), f.getBytes(4L)));
		Assertions.assertTrue(Arrays.equals(new byte[8], f.getBytes(null)));
		Assertions.assertThrows(IncorrectDataTypeException.class, 
				() -> {new LongField("a").getBytes(4.0f);});
	}
}
