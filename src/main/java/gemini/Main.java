/*
Copyright 2017-2018 Coline Mignot, Philippe Gambette, Yuheng FENG

This file is part of Gemini.

    Foobar is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Gemini.  If not, see <http://www.gnu.org/licenses/>.
*/

package gemini;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

//import edu.princeton.cs.algs4.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.matching.*;
import org.jdom2.*;
import org.jdom2.input.*;

public class Main {
	private final static Map<TwoAnnotation,Float> scoreboard = new HashMap<>();

    public static void main(String[] arg) throws JDOMException, IOException {

        String bratfile1 = "";
        String bratfile2 = "";
        String xmlfile1 = "";
        String xmlfile2 = "";
        String scoreType = "";
        String alignmentType = "maxMatching"; //"greedyMatching";
        String scoreTypeMatching = "weightedTypeMatching"; //"strictTypeMatching"; // "weakTypeMatching";
        boolean verbose = false;
        boolean createCSVfile = false;
        boolean visualize = false;
        String visualizeType = "";

        // scan the arguments
        for (int i=0 ; i<arg.length ; i++) {
            if (arg[i].equals("-bratfile1")) {
                bratfile1 = arg[i+1];
            }
            else if (arg[i].equals("-xmlfile1")) {
                xmlfile1 = arg[i+1];
            }

            if (arg[i].equals("-bratfile2")) {
                bratfile2 = arg[i+1];
            }
            else if (arg[i].equals("-xmlfile2")) {
                xmlfile2 = arg[i+1];
            }

            if (arg[i].equals("weakprecision") || arg[i].equals("weakrecall") || arg[i].equals("weakF-measure")
                    || arg[i].equals("strictprecision") || arg[i].equals("strictrecall") || arg[i].equals("strictF-measure")
                    || arg[i].equals("weightedprecision") || arg[i].equals("weightedrecall") || arg[i].equals("weightedF-measure")) {
                scoreType = arg[i];
            }

            if (arg[i].equals("weightedTypeMatching") || arg[i].equals("strictTypeMatching")) {
                scoreTypeMatching = arg[i];
            }

            if (arg[i].equals("greedyMatching") || arg[i].equals("maxMatching")) {
                alignmentType = arg[i];
            }

            if (arg[i].equals("--help")) {
                System.out.println("To indicate files to compare :" +
                        "\n -bratfile1   : followed by first file's name (.ann)" +
                        "\n -xmlfile1    : followed by first file's name (.xml)" +
                        "\n -bratfile2   : followed by second file's name (.ann)" +
                        "\n -xmlfile2    : followed by second file's name (.xml)" +
                        "\n" +
                        "\nTo indicate score type:" +
                        "\n weakprecision" +
                        "\n strictprecision" +
                        "\n weightedprecision" +
                        "\n weakrecall" +
                        "\n strictrecall" +
                        "\n weightedrecall" +
                        "\n weakF-measure" +
                        "\n strictF-measure" +
                        "\n weightedF-measure" +
                        "\n" +
                        "\nTo indicate alignment type:" +
                        "\n greedyMatching (default value)" +
                        "\n maxMatching (currently not working)" +
                        "\n" +
                        "\nTo indicate how annotations from different types are taken into account:" +
                        "\n strictTypeMatching (default value):" +
                        "\n   1 if the annotation types are equal, 0 otherwise." +
                        "\n weightedTypeMatching:" + 
                        "\n   between two distinct annotation types, multiply score by the percentage" + 
                        "\n   of intersection between the two annotation types.");
            }

            if (arg[i].equals("-verbose")) {
                verbose = true;
            }
            
            if (arg[i].equals("-CSV")) {
        			createCSVfile = true;
            }
            if (arg[i].equals("-visualiser")){
            		visualize = true;
            		visualizeType = arg[i+1];
            }
        }
        
        if (verbose) {
           System.out.println("Loading the files...");
        }
        
        // load the files and compute the data structures to store annotations
        if ((!bratfile1.equals("") || !xmlfile1.equals("")) && (!bratfile2.equals("") || !xmlfile2.equals(""))) {
            SAXBuilder sxb = new SAXBuilder();
            Annotation[] file1;
            Annotation[] file2;
            Document doc1 = null;
            Document doc2 = null;
            
            if (!bratfile1.equals("")) {
                if(verbose){
                    System.out.println("Loading file 1: "+bratfile1);
                }
                file1 = loadAnnotations(bratfile1);
            } else {
                if(verbose){
                    System.out.println("Loading file 1: "+xmlfile1);
                }
                doc1 = sxb.build(new File(xmlfile1));                
                Annotation[] f1 = annFromXML(doc1.getRootElement(), doc1.getRootElement().getValue());
                file1 = new Annotation[f1.length-1];
                for (int i=1 ; i<f1.length ; i++) {
                    file1[i-1] = f1[i];
                }
                // Save the annotations at the BRAT format
                saveAnnotations( xmlfile1.substring( 0, xmlfile1.length() - 4 ), f1 );                
            }

            if (!bratfile2.equals("")) {
                if(verbose){
                    System.out.println("Loading file 2: "+bratfile2);
                }
                file2 = loadAnnotations(bratfile2);
            } else {
                if(verbose){
                    System.out.println("Loading file 2: "+xmlfile2);
                }
                doc2 = sxb.build(new File(xmlfile2));              
                Annotation[] f2 = annFromXML(doc2.getRootElement(), doc2.getRootElement().getValue());
                file2 = new Annotation[f2.length-1];
                for (int i=1 ; i<f2.length ; i++) {
                    file2[i-1] = f2[i];
                }
                // Save the annotations at the BRAT format
                saveAnnotations( xmlfile2.substring( 0, xmlfile2.length() - 4 ), f2 );
            }
            
            createCSV(file1, file2, (float) 0.01, "weightedF-measure", "weightedTypeMatching",createCSVfile);
            if(visualize) {
            		Visualization vis = new Visualization(doc1,doc2);
            		vis.display(visualizeType);
            }
            long startTime = System.currentTimeMillis();
            
            // compute and display the similarity score
            if (!scoreType.equals("")) {
                if (verbose) {
                    System.out.println("Computing the similarity score...");
                }
                System.out.println("\n\nSimilarity score : " + score(file1, file2, scoreType, alignmentType, scoreTypeMatching, verbose));
            } else {
                if (verbose) {
                    System.out.println("Computing all similarity scores...");
                }            

                System.out.println("\n\nSimilarity score (weak precision) : " + score(file1, file2, "weakprecision", alignmentType, scoreTypeMatching, verbose)
                        + "\nSimilarity score (strict precision) : " + score(file1, file2, "strictprecision", alignmentType, scoreTypeMatching, verbose)
                        + "\nSimilarity score (weighted precision) : " + score(file1, file2, "weightedprecision", alignmentType, scoreTypeMatching, verbose)
                        + "\nSimilarity score (weak recall) : " + score(file1, file2, "weakrecall", alignmentType, scoreTypeMatching, verbose)
                        + "\nSimilarity score (strict recall) : " + score(file1, file2, "strictrecall", alignmentType, scoreTypeMatching, verbose)
                        + "\nSimilarity score (weighted recall) : " + score(file1, file2, "weightedrecall", alignmentType, scoreTypeMatching, verbose)
                        + "\nSimilarity score (weak F-measure) : " + score(file1, file2, "weakF-measure", alignmentType, scoreTypeMatching, verbose)
                        + "\nSimilarity score (strict F-measure) : " + score(file1, file2, "strictF-measure", alignmentType, scoreTypeMatching, verbose)
                        + "\nSimilarity score (weighted F-measure) : " + score(file1, file2, "weightedF-measure", alignmentType, scoreTypeMatching, verbose));
            }

            if (verbose) {
                System.out.println("\n\nFirst file :\n");
                for (int i=0 ; i<file1.length ; i++) {
                    System.out.println(file1[i]);
                }
                System.out.println("\n\nSecond file :\n");
                for (int i=0 ; i<file2.length ; i++) {
                    System.out.println(file2[i]);
                }
                System.out.println("\n\nAlignment table :");
                Annotation[][] corresp = alignAnnotations(file1, file2, alignmentType, verbose);
                for (int i=0 ; i<corresp.length ; i++) {
                    System.out.println("\n[TH] " + file1[i] + " :");
                    for (int j=0 ; j<corresp[i].length ; j++) {
                        if (corresp[i][j] != null) {
                            System.out.println("> [TR] " + corresp[i][j]);
                        }
                    }
                }
            }
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
  	      	System.err.println("Time to calculate the score: " + elapsedTime+" ms");
        }

        // or deal with missing files
        else if (!bratfile1.equals("") || !xmlfile1.equals("") && bratfile2.equals("") && xmlfile2.equals("")) {
            System.out.println("Second file is missing : -bratfile2 or -xmlfile2 to indicate it");
        }
        else if (bratfile1.equals("") && xmlfile1.equals("") && !bratfile2.equals("") || !xmlfile2.equals("")) {
            System.out.println("First file is missing : -bratfile1 or -xmlfile1 to indicate it");
        }

        // load annotations of the file in the first parameter
//        Annotation[] f1 = loadAnnotations(arg[0]);
//        Annotation[] f2 = loadAnnotations(arg[1]);
//        for (int i=0 ; i<f1.length ; i++) {
//            System.out.println(f1[i]);
//        }

        // test all annotations of the file
        // incorrect line : n° line
//        String test = "Incorrect annotations : ";
//        for (int i=0 ; i<f1.length ; i++) {
//            Annotation a = f1[i];
//            if (testAnnotation(arg[0], a) == false) {
//                test += a.getId() + ", ";
//            }
//        }
//        System.out.println(test);

        //test alignAnnotations
//        Annotation[][] corresp = alignAnnotations(f1, f2, arg[2]);
//        for (int i=0 ; i<corresp.length ; i++) {
//            System.out.println("correspTh " + (i+1) + " :");
//            for (int j=0 ; j<corresp[i].length ; j++) {
//                if (corresp[i][j] != null) {
//                    System.out.println(corresp[i][j]);
//                }
//            }
//            System.out.println("\n");
//        }

        // test score
//        System.out.println("Similarity score : " + score(file1, file2, arg[2], "greedyMatching"));

        // test annFromXML
//        SAXBuilder sxb = new SAXBuilder();
//        Document document = sxb.build(new File(arg[0]));
//        Annotation[] ann = annFromXML(document.getRootElement(), document.getRootElement().getValue(), new Annotation[0], 0);
//        for (int i=0 ; i<ann.length ; i++) {
//            System.out.println("- " + ann[i] + ".");
//        }

        //test matchingAnnotaionScore
//        Annotation[] a = loadAnnotations(arg[0]);
//        Annotation[] b = loadAnnotations(arg[1]);
//        System.out.println(matchingAnnotationScore(a, b, a[Integer.parseInt(arg[2])], b[Integer.parseInt(arg[3])], arg[4]));

        // test matchingTypeScore
//        Annotation[] f1 = loadAnnotations(arg[0]);
//        Annotation[] f2 = loadAnnotations(arg[1]);
//        System.out.println(matchingTypeScore(f1, f2, "LOC", "ORG"));
    }



    /**
     * Calculate a similarity score relating to the annotations between two texts.
     *
     * @param th the annotations table of the first text
     * @param tr the annotations table of the second text
     * @param scoreType the score type to choose between these parameters :
     *                  weakprecision       |   weakrecall      |   weakF-measure
     *                  strcitprecision     |   strictrecall    |   strictF-measure
     *                  weightedprecision   |   weightedrecall  |   weightedF-measure
     * @param alignmentType the alignment type to choose between these parameters :
     *                      greedyMatching | maxMatching
     * @return a similarity score between 0 and 1.
     */
    public static float score(Annotation[] th, Annotation[] tr, String scoreType, String alignmentType, String scoreTypeMatching, boolean verbose) {
        Annotation[][] correspTh = alignAnnotations(th, tr, alignmentType, verbose);
        float result = -1;
        float nbMatches = 0;
        
        // adjust the matches number depending on the 3rd parameter
        for (int i=0 ; i<correspTh.length ; i++) {
            //for (int j=0 ; j<correspTh[i].length ; j++) {
            if (correspTh[i][0] != null) {                 
                 nbMatches += matchingAnnotationScore(th, tr, th[i], correspTh[i][0], scoreType, scoreTypeMatching);
            }
            //}
        }
        System.out.println("Nb matches: "+nbMatches);
        System.out.println("TH: "+th.length);
        System.out.println("TR: "+tr.length);

        // choose the correct calculation depending on the 3rd parameter
        if (scoreType.substring(scoreType.length()-9, scoreType.length()).equals("precision")) {
            result = (float) nbMatches / th.length;
        }
        else if (scoreType.substring(scoreType.length()-6, scoreType.length()).equals("recall")) {
            result = (float) nbMatches / tr.length;
        }
        else if (scoreType.substring(scoreType.length()-9, scoreType.length()).equals("F-measure")) {
            result = (float) ( 2 * (nbMatches/th.length) * (nbMatches/tr.length) ) / ( (nbMatches/th.length) + (nbMatches/tr.length) );
        }
        else {
            System.out.println("You didn't specify the score type you want, or you chose an score type which doesn't exist.");
        }

        return result;
    }


    /**
     * Calculate a similarity score between two annotations.
     *
     * @param ann1 the first annotation to compare
     * @param ann2 the second annotation to compare
     * @param scoreType the score type to choose between these paameters :
     *                  weakprecision       |   weakrecall      |   weakF-measure
     *                  strcitprecision     |   strictrecall    |   strictF-measure
     *                  weightedprecision   |   weightedrecall  |   weightedF-measure
     * @return a 0 or 1 if the score type is weak or strict, or a float between 0 and 1 if the score type is weighted
     */
    public static float matchingAnnotationScore(Annotation[] th, Annotation[] tr, Annotation ann1, Annotation ann2, String scoreType, String scoreTypeMatching) {
        float result = 0;
        
        if (ann1.intersect(ann2) && ann1.getType().equals(ann2.getType())) {
            if (scoreType.substring(0, 4).equals("weak")) {
                result = 1;
            }
            else if (scoreType.substring(0, 6).equals("strict")) {
                if (ann1.getStart() == ann2.getStart()
                        && ann1.getEnd() == ann2.getEnd()) {
                    result = 1;
                }
            }
            else if (scoreType.substring(0, 8).equals("weighted")) {
                result = ann1.intersectionPercentage(ann2);
            }
            else {
                System.out.println("You chose a score type which doesn't exist.");
            }
        }
        
        return (result * matchingTypeScore(th, tr, ann1.getType(), ann2.getType(), scoreTypeMatching));
    }


    /**
     * Calculates a ratio of characters covered both by the annotations of a particular type and the annotations of an other type.
     *
     * @param th the first annotations table to compare
     * @param tr the second annotations table to compare
     * @param typeTh the type of the first annotations
     * @param typeTr the type of the second annotations
     * @return a float between 0 and 1
     */
    public static float matchingTypeScore(Annotation[] th, Annotation[] tr, String typeTh, String typeTr, String scoreTypeMatching) {
    	float result = (float) 0.0;
        if (scoreTypeMatching.equals("strictTypeMatching")) {           
           if (typeTh.equals(typeTr)){
              result = (float) 1.0;
           }
        } else {
        		TwoAnnotation ta = new TwoAnnotation(typeTh,typeTr);
        		if(scoreboard.containsKey(ta)) {
        			result = scoreboard.get(ta);
        		}
        		else {
	           Annotation[] newTh = oneTypeAnnotations(th, typeTh);
	           Annotation[] newTr = oneTypeAnnotations(tr, typeTr);
	           Map<Integer,Integer> changeStatus = new TreeMap<>();
	           // status = 1 : this annotation is in th, but not in tr
	           // status = 2 : this annotation is in tr, but not in th
	           // status = 3 : this annotation is in both th and tr 
	           int status = 1;
	           
	           for (Annotation ann:newTh) {
	        	   		if(ann.getStart() == ann.getEnd())	continue;
	               changeStatus.put(ann.getStart(), status);
	               changeStatus.put(ann.getEnd(), status);
	            }
	           for (Annotation ann:newTr) {
	        	   		if(ann.getStart() == ann.getEnd())	continue;
	            	   status = changeStatus.containsKey(ann.getStart()) ? 3:2;
	            	   changeStatus.put(ann.getStart(), status);
	            	   status = changeStatus.containsKey(ann.getEnd()) ? 3:2;
	            	   changeStatus.put(ann.getEnd(), status);
	           }
	           boolean nbThStatus = false;
	           boolean nbTrStatus = false;
	           int lastPosition = 0;
	           int intersection = 0;
	           int union = 0;
	
	           for(Entry<Integer,Integer> e : changeStatus.entrySet()) {
	        	   		if(nbThStatus && nbTrStatus)	intersection += (e.getKey() - lastPosition);
	        	   		if(nbThStatus || nbTrStatus) union += (e.getKey() - lastPosition);
	        	   		switch(e.getValue()) {
		        	   		case 1:	
		        	   			nbThStatus = !nbThStatus;
		        	   			break;
		        	   		case 2:	
		        	   			nbTrStatus = !nbTrStatus;
		        	   			break;
		        	   		case 3: 
		        	   			nbThStatus = !nbThStatus;
		        	   			nbTrStatus = !nbTrStatus;
	        	   		}
	        	   		lastPosition = e.getKey();
	           }
	           
	           if(union == 0)	return 0;
	           result = (float)intersection / union;
	           System.err.println("Intersection: "+intersection);
	           System.err.println("Union: "+union);
	           scoreboard.put(ta, result);
        		}
        	}
        return result;
    }


    /**
     * Keeps, in a table, only the annotations of a particular type,
     * then combines annotations which are consecutives or overlapping to have a maximum size,
     * and finally sorts the annotations depending on their start, in ascending order.
     *
     * @param t the initial annotations table
     * @param typeT the annotation's type we want to keep
     * @return an annotations table sorted in ascending order of the annotation's start
     */
    public static Annotation[] oneTypeAnnotations(Annotation[] t, String typeT) {
        int nullCells = 0;

        // Only keeps annotations with the type in parameter
        Annotation[] tBis = new Annotation[t.length];
        for (int i=0 ; i<t.length ; i++) {
            if (t[i].getType().equals(typeT)) {
                tBis[i] = t[i];
            } else {
                nullCells++;
            }
        }

        // Combines annotations which are consecutive or overlapping to have a maximum size
        // Complexity of this can be improved using the fact that the annotations are intervals sorted according to first character position
        for (int i=0 ; i<tBis.length ; i++) {
            for (int j=i+1 ; j<tBis.length ; j++) {
                if (tBis[i] != null && tBis[j] != null) {
                    if (tBis[i].intersect(t[j])) {
                        if (tBis[i].getStart() > tBis[j].getStart()) {
                            tBis[i].setStart(tBis[j].getStart());
                        }
                        if (tBis[i].getEnd() < tBis[j].getEnd()) {
                            tBis[i].setEnd(tBis[j].getEnd());
                        }
                        tBis[j] = null;
                        nullCells++;
                        i = 0;
                        j = 0;
                    }
                }
            }
        }

        // Deletes the cells of the table which are null
        Annotation[] newT = new Annotation[tBis.length-nullCells];
        int j = 0;
        for (int i=0 ; i<tBis.length ; i++) {
            if (tBis[i] != null) {
                newT[j] = t[i];
                j++;
            }
        }

        // Sorts the annotations depending on their start, in ascending order
        Annotation stock;
        for (int i=0 ; i<newT.length-1 ; i++) {
            if (newT[i].getStart() > newT[i+1].getStart()) {
                stock = newT[i];
                newT[i] = newT[i+1];
                newT[i+1] = stock;
                i = 0;
            }
        }

        return newT;
    }


    /**
     * Align the annotations of two texts, depending on the chosen algorithm.
     *
     * @param th the annotations table of the first text
     * @param tr the annotations table of the second text
     * @param algo the alignment type to choose between the following algorithms :
     *             greedyMatching  |   maxMatching
     * @return an annotations table with the annotations of {@code tr} in the cells corresponding with the annotations of {@code th}
     */
    public static Annotation[][] alignAnnotations(Annotation[] th, Annotation[] tr, String algo, boolean verbose) {
    	Annotation[][] correspTh = new Annotation[th.length][tr.length];

        // align one annotation with several depending on the annotations intersection if they have the same type
        if (algo.equals("maxMatching")) {
            // Create a simple weighted graph: http://jgrapht.org/javadoc/org/jgrapht/graph/SimpleWeightedGraph.html#SimpleWeightedGraph-java.lang.Class-
            WeightedGraph<Integer, DefaultEdge> g = new SimpleWeightedGraph<Integer, DefaultEdge>(DefaultEdge.class);
            Set<Integer> vH = new HashSet<>();
            Set<Integer> vR = new HashSet<>();
            for (int i=0 ; i<th.length+tr.length ; i++) {
               g.addVertex(i);            
               if(i<th.length){vH.add(i);}else{vR.add(i);}
            }
            for (int i=0 ; i<th.length ; i++) {
                for (int j=0 ; j<tr.length ; j++) {
                    if ((th[i].intersect(tr[j]) == true) && (th[i].type.equals(tr[j].type))) {                        
                        //System.out.println(i+" - "+(th.length+j));
                        DefaultWeightedEdge edge = new DefaultWeightedEdge();
                        g.addEdge(i, th.length+j, edge);
                        //DefaultWeightedEdge e = (DefaultWeightedEdge) g.addEdge(i, th.length+j);
                        g.setEdgeWeight(edge, th[i].intersectionPercentage(tr[j]));                        
                    }
                }
            }
            if (algo.equals("maxMatching")) {
                MaximumWeightBipartiteMatching<Integer, DefaultEdge> b = new MaximumWeightBipartiteMatching<Integer, DefaultEdge>(g, vH, vR);
                Set<DefaultEdge> matching = b.computeMatching().getEdges();
                Iterator<DefaultEdge> iter = matching.iterator();
                while (iter.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) iter.next();                    
                    correspTh[g.getEdgeSource(edge)][0] = tr[g.getEdgeTarget(edge)-th.length];
                }
                /*
                for (int v=0 ; v<th.length ; v++) {
                    if (b.mate(v) != -1) {
                        correspTh[v][0] = tr[b.mate(v)-th.length];
                    }
                }
                */
            }
        }

        // align one annotation with one other depending on the annotations intersection (for those who have the same type)
        else if (algo.equals("greedyMatching")) {
            // matrix creation
            float[][] t = new float[th.length][tr.length];
            for (int i=0 ; i<th.length ; i++) {
                for (int j=0 ; j<tr.length ; j++) {
                    if (th[i].type.equals(tr[j].type)){
                       t[i][j] = th[i].intersectionPercentage(tr[j]);
                    } else {
                       t[i][j] = 0;
                    }
                }
            }

            float max = 1;
            int imax = 0, jmax = 0;
            while (max > 0) {
                // search for the maximum
                max = 0;
                for (int i=0 ; i<t.length ; i++) {
                    for (int j=0 ; j<t[i].length ; j++) {
                        if (max < t[i][j] && t[i][j] > 0) {
                            max = t[i][j];
                            imax = i;
                            jmax = j;
                        }
                    }
                }

                // add the match in the matches' table
                correspTh[imax][0] = tr[jmax];
                if (verbose) {
                   System.out.println("Annotation " + jmax + " of TR associated with annotation " + imax + " of TH.");
                }

                // "delete" the column and the line corresponding with the added annotation
                for (int i=0 ; i<t.length ; i++) {
                    t[i][jmax] = 0;
                }
                for (int j=0 ; j<t[0].length ; j++) {
                    t[imax][j] = 0;
                }
            }
        }

        else {
            System.out.println("You didn't specify the alignment type you want, or you chose an alignment type which doesn't exist.");
        }

        return correspTh;
    }


    /**
     * Test that an annotation of the file .ann matches with the correct location in the file .txt.
     *
     * @param file the file containing the annotations (.ann)
     * @param a the annotation to test
     * @return {@code true} if the {@code start} and the {@code end} of the annotation refer to the right word or word group in the text
     *          {@code false} otherwise
     */
    public static boolean testAnnotation(String file, Annotation a) {
        String[] openedFile = openFile(file.substring(0, file.length() - 4) + ".txt"); // load the file ".txt" which matches whith the file ".ann";
        String text = "";
        for (int i=0 ; i<openedFile.length ; i++) {
            text += openedFile[i] + "\n";
        } // put character string table's items into a character string

        //System.out.println("-"+a.getLabel()+"-");
        //System.out.println("-"+text.substring(a.getStart(), a.getEnd())+"-");
        return a.getLabel().equals(text.substring(a.getStart(), a.getEnd()));
    }


    /**
     * Load all the annotations of the file (.ann).
     *
     * @param file the file which contains the annotations
     * @return an annotations table with all the annotations of the file
     */
    public static Annotation[] loadAnnotations(String file) {
        String[] openedFile = openFile(file);
        Annotation[] annotations = new Annotation[openedFile.length];
        String[] attributes = {"","",""};
        String[] attributesBis = {"","",""};

        for (int i=0 ; i<openedFile.length ; i++) {
            attributes = (openedFile[i]+"\t \t ").split("\t");
            attributesBis = (attributes[1]+"  ").split(" ");
            //System.out.println(file+" "+attributes.length+" "+attributesBis.length); 
            annotations[i] = new Annotation(attributes[0], attributesBis[0], Integer.parseInt(attributesBis[1]), Integer.parseInt(attributesBis[2]), attributes[2]);
        }
        System.out.println(annotations.length);
        return annotations;
    }


    /**
     * Save annotations to a file at the BRAT format (.ann).
     *
     * @param file the file which contains the annotations
     * @param ann the annotation table, including the whole XML text in its first cell
     * @return True if the annotations could be saved, false otherwise
     */
    public static boolean saveAnnotations(String file, Annotation[] ann) {
        boolean writeOk = true;
        try{
            PrintWriter annotationWriter = new PrintWriter( file + ".ann", "UTF-8" );
            PrintWriter textWriter = new PrintWriter( file + ".txt", "UTF-8" );
            
            // output all annotations
            for (int i=0 ; i<ann.length ; i++) {
               String annId = ann[i].id;
               if( i>0 ){
                   annotationWriter.println(annId + "\t" + ann[i].type + " " + ann[i].start + " " + ann[i].end + "\t" + ann[i].label);
               } else {
                   textWriter.println( ann[0].label );
               }               
            }
            annotationWriter.close();
            textWriter.close();
        } catch (IOException e) {
            // do something
            writeOk = false;
        }
    
        return writeOk;
    }


    /**
     * Create the Annotation's table corresponding to the XML file.
     * It's a recursive function which will go through all tags of the XML file.
     *
     * @param node the xml tag
     * @param text the entire text of the xml file
     * @param a the annotations table in which the function will add a new annotation in each new tag
     * @return the Annotation's table corresponding to the the XML file
     */
    public static Annotation[] annFromXML(Element node, String text) {
    		node = node.clone();
		List<Element> l = getAllChildren(node);
		Annotation[] ann = new Annotation[l.size()];
		int count = 0, currentPosition=0;
		System.out.println("Text length : " + text.length());
		Collections.reverse(l);
		
		String separator = "$";
		while(text.contains(separator)) separator += "$";
		//System.err.println(separator.length());
		for(Element ele:l) {
			String value = ele.getValue();
			String newValue = separator + value;
			
			ele.setText(newValue);
		}
		Collections.reverse(l);
		text = node.getValue();
		System.out.println(" New Text length : " + text.length());
		
		for(Element ele:l) {
			String value = ele.getValue();
			int index_start = text.indexOf(value);		
			ann[count] = new Annotation("T"+count, ele.getName(), 
					currentPosition+index_start-(count)*separator.length(), currentPosition+index_start-(count)*separator.length()+value.replace(separator, "").length(),
					ele.getValue().replace(separator, ""));
			count++;
			
			//System.err.println(ele.getName()+" : " + (currentPosition+index_start-count+1) + " - "+ (currentPosition+index_start-count+1+value.replace(separator, "").length())
			//		+ "   == ->   " + ele.getValue());
			text = text.substring(index_start+separator.length());		
			currentPosition += (index_start+separator.length());
		}
		return ann;
    }
    
    public static List<Element> getAllChildren(Element node) {
		List<Element> l = new ArrayList<>();
		l.add(node);
		if(node.getChildren().size() != 0) 
			for(Element e:node.getChildren()) 
				l.addAll(getAllChildren(e));		
		return l;
	}


    /**
     * Load a file
     *
     * @param fichier the file to load
     * @return a String containing the file's text
     */
    public static String[] openFile(String fichier){
        int nbLignes;
        String ligne;
        String[] lignes;

        lignes=new String[1];
        lignes[0]="";

        // Code from http://www.commentcamarche.net/forum/affich-590149-lire-un-fichier-texte-en-java
        try{
            nbLignes=0;
            InputStream ips=new FileInputStream(fichier);
            InputStreamReader ipsr=new InputStreamReader(ips);
            BufferedReader br=new BufferedReader(ipsr);
            while ((ligne=br.readLine())!=null){
                nbLignes+=1;
            }
            br.close();
            lignes = new String[nbLignes];
            nbLignes = 0;
            ips=new FileInputStream(fichier);
            ipsr=new InputStreamReader(ips);
            br=new BufferedReader(ipsr);
            while ((ligne=br.readLine())!=null){
                lignes[nbLignes]=ligne;
                nbLignes+=1;
            }
            br.close();
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        return lignes;
    }
    
    private static void createCSV(Annotation[] file1, Annotation[] file2, float threshold, String scoreType, String scoreTypeMatching, boolean newFile) throws IOException {
		if(threshold > 1 || threshold < 0) 
			throw new IllegalArgumentException(" Threshold should entre 0 and 1. Error Value : "+threshold);
		
		StringBuilder sb = new StringBuilder();
		char DEFAULT_SEPARATOR = ',';
		// CSV Format:   https://en.wikipedia.org/wiki/Comma-separated_values
		for(Annotation ann1:file1) {
			for(Annotation ann2:file2) {
				float score = matchingAnnotationScore(file1, file2, ann1, ann2, scoreType, scoreTypeMatching);
				if(score >= threshold) {
					sb.append(ann1.getId());sb.append(DEFAULT_SEPARATOR);
					sb.append(ann1.getStart());sb.append(DEFAULT_SEPARATOR);
					sb.append(ann1.getEnd());sb.append(DEFAULT_SEPARATOR);
					sb.append(applyFormatCSV(ann1.getType()));sb.append(DEFAULT_SEPARATOR);
					sb.append(applyFormatCSV(ann1.getLabel()));sb.append(DEFAULT_SEPARATOR); 
					sb.append(score); sb.append(DEFAULT_SEPARATOR);
					sb.append(applyFormatCSV(ann2.getLabel()));sb.append(DEFAULT_SEPARATOR); 
					sb.append(applyFormatCSV(ann2.getType()));sb.append(DEFAULT_SEPARATOR);
					sb.append(ann2.getId());sb.append(DEFAULT_SEPARATOR);
					sb.append(ann2.getStart());sb.append(DEFAULT_SEPARATOR);
					sb.append(ann2.getEnd());sb.append("\n");
				}
			}
		}
		
		if (newFile) {
			String csvFile = "./" + System.currentTimeMillis() + ".csv";
			File file = new File(csvFile);
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.append(sb.toString());
			writer.flush();
			writer.close();
		} else {
			System.out.println(sb.toString());
		}
	}

	private static String applyFormatCSV(String origin) {
		origin = origin.replace("\"", "\"\"");
		if (origin.contains(",") || origin.contains("\n"))
			origin = "\"" + origin + "\"";
		return origin;
	}
    
	private static class TwoAnnotation {
		final String a1;
		final String a2;

		TwoAnnotation(String a1, String a2) {
			this.a1 = a1;
			this.a2 = a2;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (this.getClass() != o.getClass())
				return false;
			TwoAnnotation ta = (TwoAnnotation) o;
			return ta.a1 == a1 && ta.a2 == a2 || ta.a1 == a2 && ta.a2 == a1;
		}

		@Override
		public int hashCode() {
			return a1.hashCode() * 13 + a2.hashCode();
		}
	}

}