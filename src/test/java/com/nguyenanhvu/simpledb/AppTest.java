package com.nguyenanhvu.simpledb;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	public static ByteArrayOutputStream outContent;
    private static PrintStream printStream;
	
	@BeforeAll
    public static void init() {
		AppTest.outContent = new ByteArrayOutputStream();
		AppTest.printStream = new PrintStream(AppTest.outContent);
		System.setOut(AppTest.printStream);
    }
	@Test
	public void mainTest() {
		Assertions.assertDoesNotThrow(() -> {App.main(null);});
	}
}
