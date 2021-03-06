package gemini;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jdom2.JDOMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CorpusTest {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	//@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	//@After
	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}

	//@Test
	public void matchingAnnoScoreTest() {
		Annotation[] th = new Annotation[2];
		th[0] = new Annotation("T1", "Date", 1, 21, "TH_T1");
		th[1] = new Annotation("T2", "Date", 31, 71, "TH_T2");
		Annotation[] tr = new Annotation[2];
		tr[0] = new Annotation("T1", "Date", 1, 41, "TR_T1");
		tr[1] = new Annotation("T2", "Date", 41, 61, "TR_T2");

		assertEquals((Main.matchingAnnotationScore(th, tr, th[0], tr[0], "weightedprecision", "weightedTypeMatching")),
				5.0 / 7 / 2, 1e-7);
	}

	//@Test
	public void simpleScoreTest1() throws JDOMException, IOException {
		// String[] args1 =
		// {"-xmlfile1","src/test/resources/nameTest1.xml","-xmlfile2","src/test/resources/nameTest2.xml","weightedTypeMatching","-verbose"};
		// Main.main(args1);
		Annotation[] th = Main.loadAnnotations("src/test/resources/nameTest1.ann");
		Annotation[] tr = Main.loadAnnotations("src/test/resources/nameTest2.ann");
		assertEquals(Main.score(th, tr, "weightedprecision", "maxMatching", "weightedTypeMatching", true, false),
				(float) 10 / 16 / 2, 1e-15);
		assertEquals(Main.score(th, tr, "weightedrecall", "maxMatching", "weightedTypeMatching", true, false),
				(float) 10 / 16, 1e-15);
		assertEquals(Main.score(th, tr, "weightedrecall", "maxMatching", "strictTypeMatching", true, false),
				(float) 10 / 16, 1e-15);
	}

	////@Test
	public void simpleScoreTest2() throws JDOMException, IOException {
		// String[] args1 =
		// {"-xmlfile1","src/test/resources/nameTest3.xml","-xmlfile2","src/test/resources/nameTest4.xml","weightedTypeMatching","-verbose"};
		// Main.main(args1);
		Annotation[] th = Main.loadAnnotations("src/test/resources/nameTest3.ann");
		Annotation[] tr = Main.loadAnnotations("src/test/resources/nameTest4.ann");
		assertEquals(Main.score(th, tr, "weightedprecision", "maxMatching", "weightedTypeMatching", true, false),
				(float) (9.0 / 16 + 5.0 / 19) * 14 / 35 / 2, 1e-15);
	}

	////@Test
	public void simpleScoreTest3() throws JDOMException, IOException {
		Annotation[] th = new Annotation[3];
		Annotation[] tr = new Annotation[1];
		th[0] = new Annotation("T1", "Date", 1, 21, "TH_T1");
		th[1] = new Annotation("T2", "Date", 21, 71, "TH_T2");
		th[2] = new Annotation("T3", "Date", 11, 51, "TH_T3");
		tr[0] = new Annotation("T1", "Date", 1, 101, "TR_T1");
		float score1 = Main.score(th, tr, "weightedprecision", "maxMatching", "weightedTypeMatching", false, false);
		float score2 = Main.score(th, tr, "weightedprecision", "greedyMatching", "weightedTypeMatching", false, false);
		assertEquals(score1, 0.5 * 0.7 / 3, 1e-7);
		assertEquals(score2, 0.5 * 0.7 / 3, 1e-7);
	}

	// @Test
	public void overlapDateTest() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/DateXML1.xml", "-bratfile2",
				"src/test/resources/DateXML2.ann", "-verbose" };
		Main.main(args1);
	}

	// @Test
	public void visualize1brat1xmlTest() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/nom_ZH_TH.xml", "-bratfile2",
				"src/test/resources/nom_ZH_TH.ann", "-verbose", "-visualize=Personne" };
		Main.main(args1);
	}

	// @Test
	public void chineseNameTest1() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/nom_ZH_TH.xml", "-bratfile2",
				"src/test/resources/nom_ZH_TR.ann", "-verbose", "-visualize=Personne", "-CRLF", "-CSV",
				"weightedTypeMatching" };
		Main.main(args1);
	}

	// @Test
	public void chineseNameTest2() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/test_fin_result.xml", "-bratfile2",
				"src/test/resources/test_fin(brat).ann", "-verbose", "-visualize=Personne", "-CRLF", "-CSV",
				"strictTypeMatching" };
		Main.main(args1);
	}

	// problem de corpus
	// @Test
	public void chineseDateTest() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/Date_ZH_TH.xml", "-bratfile2",
				"src/test/resources/Date_ZH_TR.ann", "-CRLF", "-visualize=Date", "strictTypeMatching" };
		Main.main(args1);
	}
	
	//https://stackoverflow.com/questions/5709232/how-do-i-include-etc-in-xml-attribute-values
	//@Test
	public void cscTest() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/Juin/80jours_csc_csc.xml", "-xmlfile2",
				"src/test/resources/Juin/80jours_csc_csc-pers.xml","maxaMatching","weakprecision","strictTypeMatching","-visualize=date" };
		String[] args2 = { "-xmlfile1", "src/test/resources/Juin/80jours_extrait1_csc_csc.xml", "-xmlfile2",
				"src/test/resources/Juin/80jours_extrait1_csc_csc-pers.xml", "-visualize=date" };
		String[] argsTEI = { "-TEI", "src/test/resources/Juin/80jours_csc_csc.xml", 
				"src/test/resources/Juin/80jours_csc_csc-pers.xml", "persName", /*"-visualize=when"*/ };
		Main.main(args1);
	}
	
	//@Test
	public void calculateByType() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/CorpusAPILGRAMLAB_2011-06_annote_Lidia4.xml", "-xmlfile2",
				"src/test/resources/CorpusAPILGRAMLAB_2011-06_annoteEleni.xml", "-type" };
		String[] args2 = { "-xmlfile1", "src/test/resources/CorpusAPILGRAMLAB_2011-06_annote_Lidia4.xml", "-xmlfile2",
				"src/test/resources/CorpusAPILGRAMLAB_2011-06_annoteEleni.xml","strictTypeMatching" };
		Main.main(args2);
		Main.main(args1);
		
	}
	
	//@Test
	public void calculateByType2() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/repair/mar/ORG_AUT1.xml", "-xmlfile2",
				"src/test/resources/repair/mar/ORG_MAN1A.xml", "-type" };
		String[] args2 = { "-xmlfile1", "src/test/resources/repair/mar/ORG_AUT1.xml", "-xmlfile2",
				"src/test/resources/repair/mar/ORG_MAN1A.xml","strictTypeMatching","weakF-measure","-CSV" };
		Main.main(args2);
		Main.main(args1);
		
	}
	
	//@Test
	public void calculateByType3() throws JDOMException, IOException {
		String[] args1 = { "-xmlfile1", "src/test/resources/Juin/80jours_csc_csc.xml", "-xmlfile2",
				"src/test/resources/Juin/80jours_csc_csc-pers.xml", "-type" };
		String[] args2 = { "-xmlfile1", "src/test/resources/Juin/80jours_csc_csc.xml", "-xmlfile2",
				"src/test/resources/Juin/80jours_csc_csc-pers.xml","strictTypeMatching" };
		//Main.main(args2);
		Main.main(args1);
		
	}
}
