package gemini;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AnnotationTest {
	
	@Test
	public void oneTypeAnnoTest(){
		Annotation[] th = new Annotation[3];
		th[0] = new Annotation("T1","Date",1,21,"TH_T1");
		th[1] = new Annotation("T2","Date",21,71,"TH_T2");
		th[2] = new Annotation("T3","Date",10,51,"TH_T3");
		Annotation[] newTh = Main.oneTypeAnnotations(th, "Date");
		assertEquals(newTh.length,1);
		assertEquals(newTh[0],new Annotation("T1","Date",1,71,"TH_T1"));

	}
	
	@Test
	public void loadAnnotationsDisorder(){
		Annotation[] anns = Main.loadAnnotations("src/test/resources/orderTest1.ann");
		assertEquals(anns.length,2);
		anns = Main.loadAnnotations("src/test/resources/Date_ZH_TR.ann");
		assertEquals(anns.length,164);
	}
	

}
