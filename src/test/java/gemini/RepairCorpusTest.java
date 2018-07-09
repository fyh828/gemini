package gemini;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.junit.Test;

public class RepairCorpusTest {
	@Test
	public void simpleTest1() throws JDOMException, IOException {
		String file_tr = "src/test/resources/repair/manuel1.xml";
		String file_th = "src/test/resources/repair/auto1.xml";
		String[] args1 = { "-repair","-all", file_th,file_tr};
		Main.main(args1);
	}
	
	@Test
	public void simpleTest2() throws JDOMException, IOException {
		String file_tr = "src/test/resources/repair/manuel2.xml";
		String file_th = "src/test/resources/repair/auto2.xml";
		String[] args1 = { "-repair", file_th,file_tr};
		Main.main(args1);
	}
	
}
