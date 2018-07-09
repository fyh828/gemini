package gemini;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import algo.LevenshteinDistance;
import tei.TEITools;

import java.util.TreeMap;

public class TextUtils {
	public static String restoreXML(String origin, Annotation[] annFile) {
		if(annFile.length == 0) return origin;
		if(!checkAnnotation(origin,annFile)) throw new IllegalArgumentException("Some Label in brat file doesn't match origin text. "); 
		
		StringBuilder sb = new StringBuilder(origin);
		Map<Integer,String> tags = new TreeMap<>();
		for(Annotation ann:annFile) {
			if(!tags.containsKey(ann.getStart()))
				tags.put(ann.getStart(), "<"+ann.getType()+">");
			else{
				tags.put(ann.getStart(), tags.get(ann.getStart())+"<"+ann.getType()+">");
			}

			if(!tags.containsKey(ann.getEnd()))
				tags.put(ann.getEnd(), "</"+ann.getType()+">");
			else{
				tags.put(ann.getEnd(), "</"+ann.getType()+">"+tags.get(ann.getEnd()));
			}
		}
		//System.out.println(tags);
		int nbLettersAdded = 0;
		for(Entry<Integer,String> e: tags.entrySet()) {
			sb.insert(e.getKey()+nbLettersAdded, e.getValue());
			nbLettersAdded += e.getValue().length();
		}
		return sb.toString();
	}
	
	public static boolean checkAnnotation(String text, Annotation[] annFile) {
		for(Annotation ann:annFile) {
			if(!text.substring(ann.getStart(), ann.getEnd()).equals(ann.getLabel())) {
				System.err.println(ann+" : doesn't match origin text! ORIGIN = " + text.substring(ann.getStart(), ann.getEnd()));
				return false;
			}
		}
		return true;
	}
	
	public static void sortAnnotation(Annotation[] annFile) {
		Arrays.sort(annFile,
				(Comparator<? super Annotation>) (Annotation a1, Annotation a2) -> a1.getStart() - a2.getStart());
	}
	
	public static void generateXMLfromBrat(String path_txtFile, String path_bratFile) throws IOException {
		String txtFile = new String(Files.readAllBytes(Paths.get(path_txtFile)), StandardCharsets.UTF_8);
		Annotation[] bratFile = Main.loadAnnotations(path_bratFile);
		File file = new File(path_txtFile.substring( 0, path_txtFile.length() - 4 ) + ".xml");
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		// TODO: correct special characters, cf : https://stackoverflow.com/questions/12524908/how-to-escape-in-xml
		writer.append("<xml>"+restoreXML(txtFile, bratFile)+"</xml>");
		writer.flush();
		writer.close();
	}
	
	public static void main(String args[]) throws IOException {
		//TODO: add JUnit test to check if value in annotation doesn't compare to origin text
		//TODO: test sort with many cases.

		if(args.length == 2)
			generateXMLfromBrat(args[0],args[1]);

	}
	
	public static void createRepairedFile(String path_th,String path_tr,String mode) throws IOException {
		String file_tr = new String(Files.readAllBytes(Paths.get(path_tr)), StandardCharsets.UTF_8);
		String file_th = new String(Files.readAllBytes(Paths.get(path_th)), StandardCharsets.UTF_8);
		String res = repair(file_th,file_tr,mode);
		if(!res.endsWith("</xml>")) {
			if(res.endsWith("\r\n"))
				res = res.substring(0, res.length()-2);
			else if(res.endsWith("\n"))
				res = res.substring(0, res.length()-1);
			res+="</xml>";
		}
		FileWriter writer = new FileWriter(new File(path_th.substring(0, path_th.length()-4)+"_corr.xml"));
		writer.append(res);
		writer.flush();
		writer.close();
	}
	
	private static String repair(String file_th, String file_tr, String mode) {
		
		StringBuilder sb = new StringBuilder();
		int index_th = 0, index_tr = 0;
		int index_diff;
		
		for(;;) {
			index_diff = TEITools.indexOfDifferenceWithTags(file_th.substring(index_th),file_tr.substring(index_tr));
			if(index_diff == -1) return sb.append(file_th.substring(index_th)).toString();
			sb.append(file_th.substring(index_th,index_th+index_diff));
			
			index_th += index_diff;
			index_tr += index_diff;
			if(index_th >= file_th.length() || index_tr >= file_tr.length()) return sb.toString();
			//System.out.println(index_th + "  ===="+file_th.substring(index_th,index_th+20)+"  =>>>> Index TR  =>>> "+index_tr +">>>=========> " +file_tr.substring(index_tr,index_tr+20));
			// skip tags
			if(file_th.charAt(index_th) == '<' || file_tr.charAt(index_tr) == '<') {	
				if(file_th.charAt(index_th) == '<') {
					do {
						sb.append(file_th.charAt(index_th));
						index_th++;
					}while(file_th.charAt(index_th) != '>');
					sb.append(file_th.charAt(index_th));
					index_th++;
				}
				
				if(file_tr.charAt(index_tr) == '<') {
					do {
						index_tr++;
					}while(file_tr.charAt(index_tr) != '>');
					index_tr++;
				}
				continue;
			}
			String sentenceTH,sentenceTR;
			if(mode.equals("-part")) {
				sentenceTH = getSentence(file_th, index_th);
				sentenceTR = getSentence(file_tr, index_tr);
			}
			else if(mode.equals("-all")) {
				sentenceTH = file_th.substring(index_th);
				sentenceTR = file_tr.substring(index_tr);
			}
			else
				throw new IllegalArgumentException("Unknown mode: "+mode);
			
			String repaired = LevenshteinDistance.repairString(sentenceTH,sentenceTR);
			sb.append(repaired);
			index_th += sentenceTH.length();
			index_tr += sentenceTR.length();	
		}
	}
	
	private static String getSentence(String text, int position) {
		StringBuilder res = new StringBuilder();
		while(true) {
			if(position >= text.length())
				return res.toString();
			else if(text.charAt(position) == '.')
				return res.append('.').toString();
			res.append(text.charAt(position));
			position++;
		}
	}

}
