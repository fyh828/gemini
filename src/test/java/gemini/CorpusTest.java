package gemini;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.junit.Test;

public class CorpusTest {
	
	//@Test
	public void simpleTest1() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/nameTest1.xml","-xmlfile2","src/test/resources/nameTest2.xml","weightedTypeMatching","-verbose"};
		//String[] args1 = {"-xmlfile1","src/test/resources/nameTest1.xml","-bratfile2","src/test/resources/nameTest1.ann","-verbose"};
		Main.main(args1);
		Annotation[] th = Main.loadAnnotations("src/test/resources/nameTest1.ann");
		Annotation[] tr = Main.loadAnnotations("src/test/resources/nameTest2.ann");
		assertEquals(Main.score(th, tr, "weightedprecision", "maxMatching", "weightedTypeMatching", true),(float)10/16/2,1e-15);
		assertEquals(Main.score(th, tr, "weightedrecall", "maxMatching", "weightedTypeMatching", true),(float)10/16,1e-15);
		assertEquals(Main.score(th, tr, "weightedrecall", "maxMatching", "strictTypeMatching", true),(float)10/16,1e-15);
	}
	
	//@Test
	public void simpleTest2() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/nameTest3.xml","-xmlfile2","src/test/resources/nameTest4.xml","weightedTypeMatching","-verbose"};
		Main.main(args1);
		Annotation[] th = Main.loadAnnotations("src/test/resources/nameTest3.ann");
		Annotation[] tr = Main.loadAnnotations("src/test/resources/nameTest4.ann");
		assertEquals(Main.score(th, tr, "weightedprecision", "maxMatching", "weightedTypeMatching", true),(float)(9.0/16+5.0/19)*14/35/2,1e-15);
	}
	
	//@Test
	public void simpleTest3() throws JDOMException, IOException {
		Annotation[] th = new Annotation[3];
		Annotation[] tr = new Annotation[1];
		th[0] = new Annotation("T1","Date",1,21,"TH_T1");
		th[1] = new Annotation("T2","Date",21,71,"TH_T2");
		th[2] = new Annotation("T3","Date",10,51,"TH_T3");
		tr[0] = new Annotation("T1","Date",1,101,"TR_T1");
		float score1 = Main.score(th,tr,"weightedprecision", "maxMatching", "weightedTypeMatching", false);
		float score2 = Main.score(th,tr,"weightedprecision", "greedyMatching", "weightedTypeMatching", false);
		System.out.println("Score1 : " + score1);
		System.out.println("Score2 : " + score2);

	}
	
	//@Test
	public void chineseNameTest1() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/nom_ZH_TH.xml","-xmlfile2","src/test/resources/nom_ZH_TH.xml","greedyMatching","-verbose"};
		Main.main(args1);
	}
	
	//@Test
	public void chineseNameTest2() throws JDOMException, IOException {
		String[] args1 = {"-bratfile1","src/test/resources/nom_ZH_TR.ann","-bratfile2","src/test/resources/nom_ZH_TR.ann","-verbose"};
		Main.main(args1);
	}
	
	//@Test
	public void chineseNameTest() throws JDOMException, IOException {
		String[] args1 = {"-xmlfile1","src/test/resources/nom_ZH_TH.xml","-bratfile2","src/test/resources/nom_ZH_TR.ann","-verbose"};
		Main.main(args1);
	}
}
