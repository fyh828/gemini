package gemini;

import java.io.IOException;
import org.jdom2.JDOMException;
import org.junit.Test;

import tei.TEITools;

public class TEITest {
	//@Test
	public void SimpleTEITest() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[auto].xml","-xmlfile2","src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[manuelle].xml"};	
		Main.main(args1);
	}
	
	@Test
	public void SimpleTEITest2() throws JDOMException, IOException {
		String fileTH = "src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[auto].xml";
		String fileTR = "src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[manuelle].xml";
		String type = "w";
		String[] args = {fileTH,fileTR,type};
		TEITools.main(args);
	}
}
