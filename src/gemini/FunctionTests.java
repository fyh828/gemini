package gemini;

import static org.junit.Assert.*;

import org.junit.Test;

public class FunctionTests {
	@Test
	public void firstTest() {
		assertTrue("a".equals("a"));
		assertEquals("a","a");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNull() {
	    String s = null;
	    s.chars();
	}
}
