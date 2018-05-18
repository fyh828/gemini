package gemini;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

public class AlignmentTest {
	
	@Test
	public void maxBigraphMatchingSimpleTest() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		g.addVertex(1);  
		g.addVertex(2);  
		g.addVertex(3);  
		g.addVertex(4);  
		
        DefaultWeightedEdge edge1 = new DefaultWeightedEdge();
        g.addEdge(1,3,edge1);
        g.setEdgeWeight(edge1, 0.3);
       
        DefaultWeightedEdge edge2 = new DefaultWeightedEdge();
        g.addEdge(2,3,edge2);
        g.setEdgeWeight(edge2, 0.56);
        
        DefaultWeightedEdge edge3 = new DefaultWeightedEdge();
        g.addEdge(2,4,edge3);   
        g.setEdgeWeight(edge3, 0.3);
        
        
        Set<Integer> left = new HashSet<>();
        Set<Integer> right = new HashSet<>();
 
        left.add(1);
        left.add(2);
        right.add(3);
        right.add(4);
        
        MaximumWeightBipartiteMatching<Integer, DefaultWeightedEdge> b = new MaximumWeightBipartiteMatching<Integer, DefaultWeightedEdge>(g, left, right);
        Set<DefaultWeightedEdge> matching = b.getMatching().getEdges();
        Set<DefaultWeightedEdge> solution = new HashSet<>(Arrays.asList(edge1,edge3));
        assertEquals(matching,solution);
        
        g.setEdgeWeight(edge2, 0.61);
        Set<DefaultWeightedEdge> matching2 = b.getMatching().getEdges();
        Set<DefaultWeightedEdge> solution2 = new HashSet<>(Arrays.asList(edge2));
        assertEquals(matching2,solution2);
	}
	
	
	@Test
	public void maxMatchingWithTwoAnnotation() {
		//String[] args = {"-xmlfile1","src/test/resources/testFile.xml","-xmlfile2","src/test/resources/testFile.xml","-visualize="};	
		Annotation[] th = new Annotation[2];
		Annotation[] tr = new Annotation[2];
		th[0] = new Annotation("T1","TEST",1,51,"TH_T1");
		th[1] = new Annotation("T2","TEST",51,101,"TH_T2");
		tr[0] = new Annotation("T1","TEST",27,77,"TR_T1");
		tr[1] = new Annotation("T2","TEST",81,131,"TR_T2");
		
		float score1 = Main.score(th,tr,"weightedprecision", "maxMatching", "weightedTypeMatching", false);
		float score2 = Main.score(th,tr,"weightedprecision", "greedyMatching", "weightedTypeMatching", false);
		System.out.println("Score1 : " + score1);
		System.out.println("***********************");
		System.out.println("Score2 : " + score2);

	}
	
	@Test
	public void alignmentWithTwoDifferentTypeAnnotation() {
		Annotation[] th = new Annotation[2];
		Annotation[] tr = new Annotation[2];
		th[0] = new Annotation("T1","TEST",1,51,"TH_T1");
		th[1] = new Annotation("T2","TEST",51,101,"TH_T2");
		tr[0] = new Annotation("T1","TEST2",1,51,"TR_T1");
		tr[1] = new Annotation("T2","TEST2",51,101,"TR_T2");
		
		float score1 = Main.score(th,tr,"weightedprecision", "maxMatching", "weightedTypeMatching", true);
		System.out.println("==================================");
		float score2 = Main.score(th,tr,"weightedprecision", "greedyMatching", "weightedTypeMatching", true);
		System.out.println("***********************");
		System.out.println("Score1 : " + score1);
		System.out.println("***********************");
		System.out.println("Score2 : " + score2);
	}

	
}
