package gemini;

import java.io.File;
import java.io.IOException;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import tei.TEITools;

public class TEITest {
	//@Test
	public void SimpleTEITest() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[auto].xml","-xmlfile2","src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[manuelle].xml"};	
		Main.main(args1);
	}
	
	//@Test
	public void SimpleTEITest2() throws JDOMException, IOException {
		String fileTH = "src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[auto].xml";
		String fileTR = "src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[manuelle].xml";
		String type = "w";
		String attribute1 = "type";
		String attribute2 = "-visualize=subtype";
		String attribute3 = "-visualize=aaa";
		String[] args = {fileTH,fileTR,type,attribute1,attribute2,attribute3};
		//String[] args = {fileTH,fileTR,type,attribute3};
		TEITools.main(args);
	}
	
	//@Test
	public void TEITestVisualization() throws JDOMException, IOException {
		String fileTH = "src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[auto].xml";
		String fileTR = "src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[manuelle].xml";
		SAXBuilder sxb = new SAXBuilder();
		Visualization vis = new Visualization(sxb.build(new File(fileTH)),sxb.build(new File(fileTR)));
		vis.displayTEI("w","type");
		vis.displayTEI("w","subtype");
		vis.displayTEI("geogName","type");
		vis.displayTEI("geogName","aaa");
		vis.displayTEI("placeName","id");
	}
	
	//@Test
	public void SimpleTEITest3() throws JDOMException, IOException {
		String fileTH = "src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[auto].xml";
		String fileTR = "src/test/resources/TEI/1e_jour_de_pralognan_au_refuge_de_la_lei_[manuelle].xml";
		String type = "w";
		String attribute1 = "type";
		String attribute2 = "-visualize=subtype";
		String attribute3 = "-visualize=aaa";
		String[] args = {"-TEI",fileTH,fileTR,type,attribute1,attribute2,attribute3};
		//String[] args = {fileTH,fileTR,type,attribute3};
		Main.main(args);
	}
	
	//@Test
	public void TEICorpusTest1() throws JDOMException, IOException {
		String fileTH = "src/test/resources/TEI/La_Republique_dUtopie1.xml";
		String fileTR = "src/test/resources/TEI/La_Republique_dUtopie2.xml";
		String type = "div";
		String attribute1 = "n";
		String attribute2 = "-visualize=type";
		String[] args = {"-TEI",fileTH,fileTR,type,attribute1,attribute2};
		Main.main(args);
	}
}
