package gemini;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jdom2.JDOMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainArgumentsTests {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}
	
	@Test
	public void runWithoutArguments() throws JDOMException, IOException {
		String[] args1 = new String[0];
		Main.main(args1);
	}
	
	@Test
	public void runWithInvalidArguments() throws JDOMException, IOException {
		String[] args1 = {"aaa"};
		String[] args2 = {"-zzzzz","111"};
		String[] args3 = {"!@#$%^&*œ∑´®†¥¨˙∫√ƒ¨§®åß§ƒYUGU≤≥…≤µ˜∫√¨¥","w(ﾟДﾟ)w啊"};
		String[] args4 = {"","src/test/resources/testFile.xml"};
		
		Main.main(args1);
		Main.main(args2);
		Main.main(args3);
		Main.main(args4);
	}
	
	@Test
	public void runWithArgumentFileWithoutFollowingAFile() throws JDOMException, IOException {
		String[] args1 = {"-bratfile1"};
		String[] args2 = {"-xmlfile2"};
		String[] args3 = {"-bratfile2","-verbose"};
		
		try {
			Main.main(args1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Missing brat file 1 "));
		}

		try {
			Main.main(args2);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Missing xml file 2 "));
		}

		try {
			Main.main(args3);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Missing brat file 2 "));
		}
	}
	
	@Test
	public void runWithASingleFile() throws JDOMException, IOException {
		String[] args1 = {"-bratfile1","src/test/resources/testFile.ann"};
		String[] args2 = {"-xmlfile2","src/test/resources/testFile.xml"};
		
		try {
			Main.main(args1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Second file is missing"));
		}
		
		try {
			Main.main(args2);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("First file is missing"));
		}
	}
	
	@Test
	public void runWithTheSameFile() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/testFile.xml","-xmlfile2","src/test/resources/testFile.xml"};
		String[] args2 = {"-bratfile1","src/test/resources/testFile.ann","-bratfile2","src/test/resources/testFile.ann"};
		Main.main(args1);
		Main.main(args2);
	}
	
	@Test
	public void runWithTwoFile1File() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/testFile.xml","-xmlfile1","src/test/resources/testFile.xml"};
		String[] args2 = {"-bratfile2","src/test/resources/testFile.ann","-bratfile2","src/test/resources/testFile.ann"};
		
		try {
			Main.main(args1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Second file is missing"));
		}
		
		try {
			Main.main(args2);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("First file is missing"));
		}
	}
	
	@Test
	public void runWithInvalidFormatFile() throws JDOMException, IOException {
		String[] args1 = {"-bratfile1","src/test/resources/testFile.xml","-bratfile2","src/test/resources/testFile.ann"};
		String[] args2 = {"-xmlfile1","src/test/resources/testFile.xml","-xmlfile2","src/test/resources/testFile.ann"};
		String[] args3 = {"-xmlfile1","src/test/resources/testFile.odt","-xmlfile2","src/test/resources/testFile.odt"};
		String[] args4 = {"-bratfile1","src/test/resources/testFile.odt","-bratfile2","src/test/resources/testFile.odt"};
		
		try {
			Main.main(args1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("File extension"));
		}
		
		try {
			Main.main(args2);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("File extension"));
		}
		
		try {
			Main.main(args3);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("File extension"));
		}
		
		try {
			Main.main(args4);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("File extension"));
		}
	}
	
	@Test
	public void runWithAFileDoesNotExist() throws JDOMException, IOException {
		String[] args1 = {"-bratfile1","src/test/resources/thisfiledoesnotexist.ann","-bratfile2","src/test/resources/testFile.ann"};
		String[] args2 = {"-xmlfile1","src/test/resources/testFile.xml","-xmlfile2","src/test/resources/thisfiledoesnotexist.xml"};
		
		try {
			Main.main(args1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("file doesn't exist"));
		}
		try {
			Main.main(args2);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("file doesn't exist"));
		}
	}
	
	@Test
	public void runWithBrokenXMLFile() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/formatErrorXML2.xml","-xmlfile2","src/test/resources/testFile.xml"};
		String[] args2 = {"-xmlfile1","src/test/resources/testFile.xml","-xmlfile2","src/test/resources/formatErrorXML3.xml"};
		
		try {
			Main.main(args1);
			fail();
		} catch (JDOMException e) {}
		
		try {
			Main.main(args2);
			fail();
		} catch (JDOMException e) {}
	}
	
	@Test
	public void runWithBrokenBratFile() throws JDOMException, IOException {
		String[] args1 = {"-bratfile1","src/test/resources/formatErrorBrat1.ann","-bratfile2","src/test/resources/testFile.ann"};
		String[] args2 = {"-bratfile1","src/test/resources/formatErrorBrat2.ann","-bratfile2","src/test/resources/testFile.ann"};
		String[] args3 = {"-bratfile1","src/test/resources/testFile.ann","-bratfile2","src/test/resources/formatErrorBrat3.ann"};
		String[] args4 = {"-bratfile1","src/test/resources/testFile.ann","-bratfile2","src/test/resources/formatErrorBrat4.ann"};
		
		try {
			Main.main(args1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Brat file broken"));
		}
		
		try {
			Main.main(args2);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Brat file broken"));
		}
		
		try {
			Main.main(args3);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Brat file broken"));
		}
		
		try {
			Main.main(args4);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Brat file broken"));
		}
	}
	
	@Test
	public void runWith1BratAnd1XMLFile() throws JDOMException, IOException {
		String[] args1 = {"-bratfile1","src/test/resources/testFile.ann","-xmlfile1","src/test/resources/testFile.xml"};
		String[] args2 = {"-bratfile1","src/test/resources/testFile.ann","-xmlfile2","src/test/resources/testFile.xml"};
		String[] args3 = {"-xmlfile1","src/test/resources/testFile.xml","-bratfile1","src/test/resources/testFile.ann"};
		String[] args4 = {"-xmlfile1","src/test/resources/testFile.xml","-bratfile2","src/test/resources/testFile.ann"};
		
		try {
			Main.main(args1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Second file is missing"));
		}

		try {
			Main.main(args3);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Second file is missing"));
		}
		
		Main.main(args2);
		Main.main(args4);
	}
	
	@Test
	public void runWith3or4Files() throws JDOMException, IOException {
		String[] args1 = {"-bratfile1","src/test/resources/testFile.ann","-bratfile2","src/test/resources/testFile.ann","-xmlfile1","src/test/resources/testFile.xml"};
		String[] args2 = {"-xmlfile1","src/test/resources/testFile.xml","-xmlfile2","src/test/resources/testFile.xml",
				"-bratfile1","src/test/resources/testFile.ann","-bratfile2","src/test/resources/testFile.ann"};
		
		Main.main(args1);
		Main.main(args2);
	}
	
}
