package tei;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import gemini.Annotation;
import gemini.Main;
import gemini.Visualization;

public class TEITools {
	
	public static void main(String[] args) throws JDOMException, IOException {
		
		if(args.length < 3) {
			System.out.println(" Usage: TEITools [path_to_TEI_File_1] [path_to_TEI_File_2] [Type_you_want_to_compare] [Attribute 1,2,3...(optional)]");
			System.out.println(" You can add '-visualize=' before the attribute to visualize the result of this attribute in a HTML file. ");
			return;
		}
		if(!new File(args[0]).isFile() || !new File(args[1]).isFile()) {
			System.out.println(" Can't open the file ");
			return;
		}
		
		String fileTH = args[0];
		String fileTR = args[1];
		String type = args[2];
		
		List<String> attributes = null;
		List<String> visualize = new ArrayList<>();
		
		if(args.length == 3) {
			System.out.println(" Compare with default attributes: lemma, type, subtype ... ");
			attributes = new ArrayList<>(Arrays.asList("lemma","type","subtype","n","xml:id"));
		}
		else {
			attributes = new ArrayList<>();
			for(int i=3;i<args.length;i++) {
				if(args[i].startsWith("-visualize=")) {
					visualize.add(args[i].substring(11));
					attributes.add(args[i].substring(11));
				}
				else
					attributes.add(args[i]);
			}
			attributes = attributes.stream().distinct().collect(Collectors.toList());
			visualize = visualize.stream().distinct().collect(Collectors.toList());
		}
		
		SAXBuilder sxb = new SAXBuilder();
		Document docTH = sxb.build(new File(fileTH)); 
		Document docTR = sxb.build(new File(fileTR)); 
		compareTEI(docTH, docTR, type, attributes);
		
		if(visualize.size() > 0) {
			Visualization vis = new Visualization(docTH,docTR);
			for(String attributeType : visualize)
				vis.displayTEI(type, attributeType);
		}
	}

	private static void compareTEI(Document docTH, Document docTR, String type, List<String> attributes) throws IOException {
		
		if(!checkTwoTEIFilesSame(docTH,docTR))
			throw new IllegalArgumentException(" Two TEI origin text are different! ");
		
		
		Annotation[] annTH = singleTypeAnnotationFromTEI(docTH, type);
		Annotation[] annTR = singleTypeAnnotationFromTEI(docTR, type);
		if(annTH.length == 0  && annTR.length == 0) { 
			System.err.println(" There isn't any annotation with type "+type);
			return;
		}
		//for(Annotation ann:annTH) System.out.println(ann);
		System.out.println("Computing all similarity scores with type \""+type+"\" : ");
        System.out.println("Similarity score (weak precision) : " + Main.score(annTH, annTR, "weakprecision", "maxMatching", "strictTypeMatching", false, false)
        + "\nSimilarity score (strict precision) : " + Main.score(annTH, annTR, "strictprecision", "maxMatching", "strictTypeMatching", false, false)
        + "\nSimilarity score (weighted precision) : " + Main.score(annTH, annTR, "weightedprecision", "maxMatching", "strictTypeMatching", false, false)
        + "\nSimilarity score (weak recall) : " + Main.score(annTH, annTR, "weakrecall", "maxMatching", "strictTypeMatching", false, false)
        + "\nSimilarity score (strict recall) : " + Main.score(annTH, annTR, "strictrecall", "maxMatching", "strictTypeMatching", false, false)
        + "\nSimilarity score (weighted recall) : " + Main.score(annTH, annTR, "weightedrecall", "maxMatching", "strictTypeMatching", false, false)
        + "\nSimilarity score (weak F-measure) : " + Main.score(annTH, annTR, "weakF-measure", "maxMatching", "strictTypeMatching", false, false)
        + "\nSimilarity score (strict F-measure) : " + Main.score(annTH, annTR, "strictF-measure", "maxMatching", "strictTypeMatching", false, false)
        + "\nSimilarity score (weighted F-measure) : " + Main.score(annTH, annTR, "weightedF-measure", "maxMatching", "strictTypeMatching", false, false));
        System.out.println("==============================================");
        computeAttributeScore(annTH,annTR,type,attributes);
	}

	public static void computeAttributeScore(Annotation[] annTH, Annotation[] annTR, String type, List<String> attributes){
		boolean attr1_empty,attr2_empty;
		int nbMatches,countTH,countTR;
		
		for(String attribute:attributes) {
			nbMatches = 0;
			countTH = 0;
			countTR = 0;
			for(int i=0;i<annTH.length;i++) {
				attr1_empty = annTH[i].getAttribute(attribute) == null || annTH[i].getAttribute(attribute).equals("");
				attr2_empty = annTR[i].getAttribute(attribute) == null || annTR[i].getAttribute(attribute).equals("");
				//System.out.println(annTH[i].getAttribute(type));
				if(attr1_empty && attr2_empty)
					continue;
				if(Objects.equals(annTH[i].getAttribute(attribute),annTR[i].getAttribute(attribute)))
					nbMatches++;
				//else {
				// print the attribute who doesn't match
				//	System.out.println(" *** 1 :"+ w1.getAttributeValue(attribute) +" **** 2 : "+w2.getAttributeValue(attribute));
				//}
				if(!attr1_empty)
					countTH++;
				if(!attr2_empty)
					countTR++;
			
			}
			if(countTH!=0) {
				//scores.add((float)nbMatches/count);
				System.out.println(type+"."+attribute+" precision : "+ (float)nbMatches/countTH);
			}
			if(countTR!=0) {
				//scores.add((float)nbMatches/count);
				System.out.println(type+"."+attribute+" recall : "+ (float)nbMatches/countTR);
			}
		}
		//return scores;
	}
	  
	public static boolean checkTwoTEIFilesSame(Document d1,Document d2) {
		return normalizeReturn(normalizeSpace(d1.getRootElement().getValue())).equals(normalizeReturn(normalizeSpace(d2.getRootElement().getValue())));
	}
	
    public static Annotation[] singleTypeAnnotationFromTEI(Document doc, String type) {
		Element node = doc.getRootElement().clone();
		String text = node.getValue();
		List<Element> l = Main.getAllChildren(node);
		int count = 0, currentPosition=0, index_start=0;
		
		
		//System.out.println("Text length : " + text.length());
		Collections.reverse(l);
		
		// use a unique separator to localize label
		String separator = "$";
		while(text.contains(separator)) separator += "$";
		Iterator<Element> i = l.iterator();
		while(i.hasNext()) {
			Element ele = i.next();
			String value = ele.getValue();
			String newValue = normalizeSpace(value);
			if(ele.getName().equals(type))
				ele.setText(separator+newValue);
			else {
				ele.setText(newValue);
				i.remove();
			}
		}
		Collections.reverse(l);
		List<Annotation> ann = new ArrayList<>();
		text = normalizeReturn(node.getValue());
		//System.out.println(" New Text length : " + text.length());
		//System.err.println(text.length() - text.replace("\n", "").length());
		for(Element ele:l) {
			String value = normalizeReturn(ele.getValue());
			if(value.replace(separator, "").length() == 0) continue;
			//System.err.println(value.replace(separator, ""));
			index_start = text.indexOf(value);	
			ann.add( new Annotation("T"+count, ele.getName(), 
					currentPosition+index_start-(count)*separator.length(),
					currentPosition+index_start-(count)*separator.length()+value.replace(separator, "").length(),
					value.replace(separator, ""),ele.getAttributes()) );
			//System.err.println(ele.getAttributes());
			count++;
			
			//System.err.println(ele.getName()+" : " + (currentPosition+index_start-count+1) + " - "+ (currentPosition+index_start-count+1+value.replace(separator, "").length())
			//		+ "   == ->   " + normalizeReturn(ele.getValue()));
			
			text = text.substring(index_start+separator.length());		
			currentPosition += (index_start+separator.length());
		}
		return ann.toArray(new Annotation[ann.size()]);
    }
    
	
	
	public static String normalizeSpace(String origin) {
		origin = origin.replaceAll("\r\n","\n");
		origin = origin.replaceAll("\t", "").replaceAll(" ", "");
		origin = origin.replaceAll("^\n+", "");
		origin = origin.replaceAll("\n+", "\n");
		origin = origin.replaceAll("\n\\.\n", ".\n");
		return origin;
	}
	
	public static String normalizeReturn(String origin) {
		origin = origin.replaceAll("\n", " ");
		origin = origin.replaceAll(" (,|\\.) ", "$1 ");
		origin = origin.replaceAll(" (!|\\.)", "$1");
		origin = origin.replaceAll("([a-zA-Z$]),([a-zA-Z$])", "$1, $2");
		origin = origin.replaceAll(" ,([a-zA-Z$])", ", $1");
		origin = origin.replaceAll(" $", "");
		return origin;
	}
	
    public static List<Element> getAllChildrenWithType(Element node, String type) {
		List<Element> l = new ArrayList<>();
		if(node.getName().equals(type))
			l.add(node);
		if(node.getChildren().size() != 0) 
			for(Element e:node.getChildren()) { 
				l.addAll(getAllChildrenWithType(e,type));	
			}
		return l;
	}

	public static Annotation[] removeOverlap(Annotation[] anns) {
		if(anns.length == 0)
			return anns;
		List<Annotation> result = new ArrayList<>();
		result.add(anns[0]);
		for(int i=1;i<anns.length;i++) {
			// Consecutive ?
			if(anns[i].getStart() > result.get(result.size()-1).getEnd()) {
				result.add(anns[i]);
			}
		}
		return result.toArray(new Annotation[result.size()]);
	}
    
    /*  // A tool for debug
	  public static int indexOfDifference(String str1, String str2) {
	      if (str1 == str2) {
	          return -1;
	      }
	      if (str1 == null || str2 == null) {
	          return 0;
	      }
	      int i;
	      for (i = 0; i < str1.length() && i < str2.length(); ++i) {
	          if (str1.charAt(i) != str2.charAt(i)) {
	              break;
	          }
	      }
	      if (i < str2.length() || i < str1.length()) {
	          return i;
	      }
	      return -1;
	  }
	  */
}
