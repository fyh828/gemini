import java.io.*;
import edu.princeton.cs.algs4.*;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.filter.*;
import java.util.*;

public class Main {

    public static void main(String[] arg) throws JDOMException, IOException {

        String bratfile1 = "";
        String bratfile2 = "";
        String xmlfile1 = "";
        String xmlfile2 = "";
        String typeScore = "";
        String typeAlignment = "multiple";
        boolean verbose = false;

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
                typeScore = arg[i];
            }

            if (arg[i].equals("multiple") || arg[i].equals("greedy") || arg[i].equals("maxmatching")) {
                typeAlignment = arg[i];
            }

            if (arg[i].equals("--help")) {
                System.out.println("To indicate files to compare :" +
                        "\n -bratfile1  : followed by first file's name (.ann)" +
                        "\n -xmlfile1    : followed by first file's name (.xml)" +
                        "\n -bratfile2   : followed by second file's name (.ann)" +
                        "\n -xmlfile2    : followed by second file's name (.xml)" +
                        "\n" +
                        "\nTo indicate score type :" +
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
                        "\nTo indicate alignment type :" +
                        "\n multiple (default value)" +
                        "\n greedy" +
                        "\n maxmatching");
            }

            if (arg[i].equals("-verbose")) {
                verbose = true;
            }
        }

        // display the similarity score
        if ((!bratfile1.equals("") || !xmlfile1.equals("")) && (!bratfile2.equals("") || !xmlfile2.equals(""))) {
            SAXBuilder sxb = new SAXBuilder();
            Annotation[] file1;
            Annotation[] file2;

            if (!bratfile1.equals("")) {
                file1 = loadAnnotations(bratfile1);
            } else {
                Document doc1 = sxb.build(new File(xmlfile1));
                Annotation[] f1 = annFromXML(doc1.getRootElement(), doc1.getRootElement().getValue(), new Annotation[0]);
                file1 = new Annotation[f1.length-1];
                for (int i=1 ; i<f1.length ; i++) {
                    file1[i-1] = f1[i];
                }
            }

            if (!bratfile2.equals("")) {
                file2 = loadAnnotations(bratfile2);
            } else {
                Document doc2 = sxb.build(new File(xmlfile2));
                Annotation[] f2 = annFromXML(doc2.getRootElement(), doc2.getRootElement().getValue(), new Annotation[0]);
                file2 = new Annotation[f2.length-1];
                for (int i=1 ; i<f2.length ; i++) {
                    file2[i-1] = f2[i];
                }
            }

            if (!typeScore.equals("")) {
                System.out.println("\n\nSimilarity score : " + score(file1, file2, typeScore, typeAlignment));
            } else {
                System.out.println("\n\nSimilarity score (weak precision) : " + score(file1, file2, "weakprecision", typeAlignment)
                        + "\nSimilarity score (strict precision) : " + score(file1, file2, "strictprecision", typeAlignment)
                        + "\nSimilarity score (weighted precision) : " + score(file1, file2, "weightedprecision", typeAlignment)
                        + "\nSimilarity score (weak recall) : " + score(file1, file2, "weakrecall", typeAlignment)
                        + "\nSimilarity score (strict recall) : " + score(file1, file2, "strictrecall", typeAlignment)
                        + "\nSimilarity score (weighted recall) : " + score(file1, file2, "weightedrecall", typeAlignment)
                        + "\nSimilarity score (weak F-measure) : " + score(file1, file2, "weakF-measure", typeAlignment)
                        + "\nSimilarity score (strict F-measure) : " + score(file1, file2, "strictF-measure", typeAlignment)
                        + "\nSimilarity score (weighted F-measure) : " + score(file1, file2, "weightedF-measure", typeAlignment));
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
                Annotation[][] corresp = alignAnnotations(file1, file2, typeAlignment);
                for (int i=0 ; i<corresp.length ; i++) {
                    System.out.println("\n[TH] " + file1[i] + " :");
                    for (int j=0 ; j<corresp[i].length ; j++) {
                        if (corresp[i][j] != null) {
                            System.out.println("> [TR] " + corresp[i][j]);
                        }
                    }
                }
            }
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
//        System.out.println("Similarity score : " + score(file1, file2, arg[2], "greedy"));

        // test annFromXML
//        SAXBuilder sxb = new SAXBuilder();
//        Document document = sxb.build(new File(arg[0]));
//        Annotation[] ann = annFromXML(document.getRootElement(), document.getRootElement().getValue(), new Annotation[0]);
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
     * @param typeScore the score type to choose between these paameters :
     *                  weakprecision       |   weakrecall      |   weakF-measure
     *                  strcitprecision     |   strictrecall    |   strictF-measure
     *                  weightedprecision   |   weightedrecall  |   weightedF-measure
     * @param typeAlignment the alignment type to choose between these parameters :
     *                      multiple | greedy | maxmatching
     * @return a similarity score between 0 and 1.
     */
    public static float score(Annotation[] th, Annotation[] tr, String typeScore, String typeAlignment) {
        Annotation[][] correspTh = alignAnnotations(th, tr, typeAlignment);
        float result = -1;
        float nbMatches = 0;

        // adjust the matches number depending on the 3rd parameter
        for (int i=0 ; i<correspTh.length ; i++) {
            for (int j=0 ; j<correspTh[i].length ; j++) {
                if (correspTh[i][j] != null) {
                    nbMatches += matchingAnnotationScore(th, tr, th[i], correspTh[i][j], typeScore);
                }
            }
        }

        // choose the correct calculation depending on the 3rd parameter
        if (typeScore.substring(typeScore.length()-9, typeScore.length()).equals("precision")) {
            result = (float) nbMatches / th.length;
        }
        else if (typeScore.substring(typeScore.length()-6, typeScore.length()).equals("recall")) {
            result = (float) nbMatches / tr.length;
        }
        else if (typeScore.substring(typeScore.length()-9, typeScore.length()).equals("F-measure")) {
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
     * @param typeScore the score type to choose between these paameters :
     *                  weak       |   strict      |   weighted
     * @return a 0 or 1 if the score type is weak or strict, or a float between 0 and 1 if the score type is weighted
     */
    public static float matchingAnnotationScore(Annotation[] th, Annotation[] tr, Annotation ann1, Annotation ann2, String typeScore) {
        float result = 0;

        if (ann1.intersect(ann2) && ann1.getType().equals(ann2.getType())) {
            if (typeScore.substring(0, 4).equals("weak")) {
                result = 1;
            }
            else if (typeScore.substring(0, 6).equals("strict")) {
                if (ann1.getStart() == ann2.getStart()
                        && ann1.getEnd() == ann2.getEnd()) {
                    result = 1;
                }
            }
            else if (typeScore.substring(0, 8).equals("weighted")) {
                result = ann1.intersectionPercentage(ann2);
            }
            else {
                System.out.println("You chose a score type which doesn't exist.");
            }
        }

        return (result + matchingTypeScore(th, tr, ann1.getType(), ann2.getType())) / 2;
    }


    /**
     * Calculates a ratio of characters covered both by the annotations of a particular type and the annotations of an other type.
     *
     * @param th the first annotations table to compare
     * @param tr the second annotations table to compare
     * @param typeTh the type of the first annotations
     * @param typeTr the type of the second aannotations
     * @return a float between 0 and 1
     */
    public static float matchingTypeScore(Annotation[] th, Annotation[] tr, String typeTh, String typeTr) {
        Annotation[] newTh = oneTypeAnnotations(th, typeTh);
        Annotation[] newTr = oneTypeAnnotations(tr, typeTr);
        int newThSize = 0;
        for (int i=0 ; i<newTh.length ; i++) {
            newThSize += newTh[i].getEnd() - newTh[i].getStart();
        }
        float matchingTypeScore = 0;
        int openingTh = -1;
        int openingTr = -1;

        for (int line=0 ; line<=newTh[newTh.length-1].getEnd() ; line++) {
            for (int i=0 ; i<newTh.length ; i++) {
                if (line == newTh[i].getStart()) {
                    openingTh = newTh[i].getStart();
                }
                else if (line == newTh[i].getEnd()) {
                    if (openingTr > -1) {
                        matchingTypeScore += line - Math.max(openingTh, openingTr);
                    }
                    openingTh = -1;
                }
            }
            for (int j=0 ; j<newTr.length ; j++) {
                if (line == newTr[j].getStart()) {
                    openingTr = newTr[j].getStart();
                }
                else if (line == newTr[j].getEnd()) {
                    if (openingTh > -1) {
                        matchingTypeScore += line - Math.max(openingTh, openingTr);
                    }
                    openingTr = -1;
                }
            }
        }

        return matchingTypeScore / newThSize;
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

        // Combines annotations which are consecutives or overlapping to have a maximum size
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
     *             multiple |   greedy  |   maxmatching
     * @return an annotations table with the annotations of {@code tr} in the cells corresponding with the annotations of {@code th}
     */
    public static Annotation[][] alignAnnotations(Annotation[] th, Annotation[] tr, String algo) {
        Annotation[][] correspTh = new Annotation[th.length][tr.length];

        // align one annotation with several depending on the annotations intersection and types
        if (algo.equals("multiple") || algo.equals("maxmatching")) {
            Graph g = new Graph(th.length + tr.length);

            for (int i=0 ; i<th.length ; i++) {
                for (int j=0 ; j<tr.length ; j++) {
                    if (th[i].intersect(tr[j]) == true) {
                        if (algo.equals("multiple")) {
                            for (int c=0 ; c<tr.length ; c++) {
                                if (correspTh[i][c] == null) {
                                    correspTh[i][c] = tr[j];
                                    c = tr.length;
                                }
                            }
                        }
                        g.addEdge(i, th.length+j);
                    }
                }
            }

            if (algo.equals("maxmatching")) {
                BipartiteMatching b = new BipartiteMatching(g);
                for (int v=0 ; v<th.length ; v++) {
                    if (b.mate(v) != -1) {
                        correspTh[v][0] = tr[b.mate(v)-th.length];
                    }
                }
            }
        }

        // align one annotation with one other depending on the annotations intersection without including the types
        else if (algo.equals("greedy")) {
            // matrix creation
            float[][] t = new float[th.length][tr.length];
            for (int i=0 ; i<th.length ; i++) {
                for (int j=0 ; j<tr.length ; j++) {
                    t[i][j] = th[i].intersectionPercentage(tr[j]);
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
        String[] openedFile = ouvreFichier(file.substring(0, file.length() - 4) + ".txt"); // load the file ".txt" which matches whith the file ".ann";
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
        String[] openedFile = ouvreFichier(file);
        Annotation[] annotations = new Annotation[openedFile.length];
        String[] attributes = new String[3];
        String[] attributesBis = new String[3];

        for (int i=0 ; i<openedFile.length ; i++) {
            attributes = openedFile[i].split("\t");
            attributesBis = attributes[1].split(" ");
            annotations[i] = new Annotation(attributes[0], attributesBis[0], Integer.parseInt(attributesBis[1]), Integer.parseInt(attributesBis[2]), attributes[2]);
        }

        return annotations;
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
    public static Annotation[] annFromXML(Element node, String text, Annotation[] a) {
        // update id
        String id = "T" + (a.length);

        // add a cell to the table
        Annotation[] b = new Annotation[a.length+1];
        for (int i=0 ; i<a.length ; i++) {
            b[i] = a[i];
        }

        // add a new Annotation in the last cell of the table
        b[b.length-1] = new Annotation(id, node.getName(), text.indexOf(node.getValue()), text.indexOf(node.getValue()) + node.getValue().length(), node.getValue());

        List tags = node.getChildren();
        Iterator i = tags.iterator();
        while (i.hasNext()) {
            Element current = (Element)i.next();
            b = annFromXML(current, text, b);
        }

        return b;
    }


    /**
     * Load a file
     *
     * @param fichier the file to load
     * @return a String containing the file's text
     */
    public static String[] ouvreFichier(String fichier){
        int nbLignes;
        String ligne;
        String[] lignes;

        lignes=new String[1];
        lignes[0]="";

        // Code provenant de http://www.commentcamarche.net/forum/affich-590149-lire-un-fichier-texte-en-java
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

}