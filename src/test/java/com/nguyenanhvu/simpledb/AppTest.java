package com.nguyenanhvu.simpledb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	
   @Test
   public void mainTest() {
	   Assertions.assertDoesNotThrow(() -> {App.main(null);});
   }
}
