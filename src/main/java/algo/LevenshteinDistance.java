package algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gemini.Annotation;

public class LevenshteinDistance {
	
	public static String repairString(String fileTH, String fileTR) {
		List<Annotation> auxTH = new ArrayList<>();
		List<Annotation> auxTR = new ArrayList<>();

		String[] wordsTH = separateSentence(fileTH, auxTH);
		String[] wordsTR = separateSentence(fileTR, auxTR);
		return levenshteinDistance(wordsTH,wordsTR,auxTH);
	}
	
	private static String backtrace(String[] wordsTH, String[] wordsTR, List<Annotation> auxTH, int[][] trace, int[][] moves) {
		int i=trace[0].length-1, j=trace.length-1;
		List<String> result = new ArrayList<>();
		//System.out.println(auxTH);
		while(j!=0 || i!=0) {
			switch(moves[j][i]) {
			case 1: 
				result.add(wordsTR[j-1]);
				if(trace[j-1][i-1] != trace[j][i]) removeAnnotation(auxTH,i);
				j--;i--; break;
			case 2:
				result.add(wordsTR[j-1]);
				removeAnnotation(auxTH,i);
				j--; break;
			case 4: removeAnnotation(auxTH,i); i--;break;
			default: print2DArray(moves); throw new IllegalArgumentException("j="+j+"   i="+i+"  value:"+moves[j][i]);
		}
		}
		//System.out.println(auxTH);
		Collections.reverse(result);
		return restoreSentence(result.toArray(new String[result.size()]), auxTH);
		
	}

	private static void removeAnnotation(List<Annotation> aux, int pos) {
		for(int i=0;i<aux.size();i++) {
			if(aux.get(i).getStart() <= pos && aux.get(i).getEnd() >= pos)
				aux.remove(i);
		}
	}

	private static String levenshteinDistance(String[] wordsTH, String[] wordsTR, List<Annotation> auxTH) {
		int lenTH = wordsTH.length;
		int lenTR = wordsTR.length;
	    int[][] result = new int[lenTR+1][lenTH+1];
	    int[][] moves = new int[lenTR+1][lenTH+1];
	    for (int i = 0; i < lenTH+1; i++) {result[0][i] = i; moves[0][i] = 4;}
	    for (int j = 0; j < lenTR+1; j++) {result[j][0] = j; moves[j][0] = 2;}
	    for (int j = 1; j < lenTR+1; j++) {
	    		for(int i = 1; i < lenTH+1; i++) { 
	        		int match = (wordsTH[i-1].equals(wordsTR[j-1])) ? 0 : 1;
	            int cost_replace = result[j-1][i-1] + match;
	            int cost_insert  = result[j-1][i] + 1;
	            int cost_delete  = result[j][i-1] + 1;
	            result[j][i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	            // left-upper: 1, upper:2, left:4.
	            if(result[j][i] == cost_replace) moves[j][i]=1;
	            else if(result[j][i] == cost_insert) moves[j][i]=2;
	            else if(result[j][i] == cost_delete) moves[j][i]=4; 
	        }
	    }
	    //System.out.println(backtrace(wordsTH,wordsTR,auxTH,result,moves));
	    return backtrace(wordsTH,wordsTR,auxTH,result,moves);
	}

	private static String[] separateSentence(String file, List<Annotation> aux) {
		String[] res = file.split(" ");
		StringBuilder sb = new StringBuilder();
		int start = 0;
		for(int i=0;i<res.length;i++) {
			sb.setLength(0);
			for(int count=0; count<res[i].length(); count++) {
				if(res[i].charAt(count) == '<' && res[i].charAt(count+1) != '/') {
					start = count + 1;
					do{
						count++;
					}while(res[i].charAt(count) != '>');
					//System.out.println(res[i].charAt(start)+" >>> "+res[i].charAt(count-1));
					aux.add(new Annotation(null,res[i].substring(start,count),i+1,-1,null));
				}
				//System.out.println(aux);
				else if(res[i].charAt(count) == '<' && res[i].charAt(count+1) == '/') {
					count++;
					start = count + 1;
					do{
						count++;
					}while(res[i].charAt(count) != '>');
					//System.err.println(res[i].substring(start,count));
					for(int find=aux.size()-1; find>=0; find--) {
						//System.err.println(aux.get(find).getType() == res[i].substring(start,count));
						if(aux.get(find).getType().equals(res[i].substring(start,count)) && aux.get(find).getEnd() == -1) {
							aux.get(find).setEnd(i+1);
							break;
						}
					}
				}
				else {
					sb.append(res[i].charAt(count));
				}
			}
			res[i] = sb.toString();
		}
		return res;
	}
	
	private static String restoreSentence(String[] words, List<Annotation> aux) {
		StringBuilder sb = new StringBuilder();
		Collections.reverse(aux);
		for(int i=0;i<words.length;i++) {
			for(Annotation ann:aux) {
				if(ann.getStart() == i+1) {
					words[i] = "<" + ann.getType() + ">" + words[i];
				}
				if(ann.getEnd() == i+1) {
					words[i] = words[i] + "</" + ann.getType() + ">";
				}
			}
			sb.append(words[i]);
			sb.append(' ');
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	public static void main(String[] args) {
		//int[][] aa = new int[8][5];
		String textTH = "This <testHard><test>is</test> <test2>an</test2> apple</testHard> tree.";
		String textTR = "<test>PThis</test> is <test3>a apple</test3> tree.";
		//System.out.println(repairString(origin));
		List<Annotation> auxTH = new ArrayList<>();
		List<Annotation> auxTR = new ArrayList<>();
		/*
		for(String s:separateSentence(textTH, auxTH))
			System.out.println(s);
		System.err.println(auxTH);
		*/
		String[] wordsTH = separateSentence(textTH, auxTH);
		String[] wordsTR = separateSentence(textTR, auxTR);
		//System.out.println(restoreSentence(wordsTH,auxTH));
		for(String s:wordsTH) System.out.print(s+' ');
		System.out.println();
		for(String s:wordsTR) System.out.print(s+' ');
		System.out.println();
		
		System.err.println(textTH);
		System.err.println(textTR);
		System.err.println(repairString(textTH, textTR));
	}
	
	public static void print2DArray(int[][] a) {
		for (int[] x : a)
		{
		   for (int y : x)
		   {
		        System.out.print(y + " ");
		   }
		   System.out.println();
		}
	}
}
