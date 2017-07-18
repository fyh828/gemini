import java.io.*;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.filter.*;
import java.util.List;
import java.util.Iterator;

public class Main {

    public static void main(String[] arg) throws JDOMException, IOException {

        // load annotations of the file in the first parameter
        Annotation[] allFile1 = loadAnnotations(arg[0]);
        Annotation[] allFile2 = loadAnnotations(arg[1]);
//        for (int i=0 ; i<allFile1.length ; i++) {
//            System.out.println(allFile1[i]);
//        }

        // test all annotations of the file
        // incorrect line : n° line
//        String test = "Incorrect annotations : ";
//        for (int i=0 ; i<allFile1.length ; i++) {
//            Annotation a = allFile1[i];
//            if (testAnnotation(arg[0], a) == false) {
//                test += a.getId() + ", ";
//            }
//        }
//        System.out.println(test);

        //test alignAnnotations
        Annotation[] corresp = alignAnnotations(allFile1, allFile2, arg[2]);
        for (int i=0 ; i<corresp.length ; i++) {
            System.out.println(corresp[i]);
        }

        // test score
//        System.out.println("Similarity score : "+score(allFile1, allFile2, arg[2]));

        // test annFromXML
//        SAXBuilder sxb = new SAXBuilder();
//        Document document = sxb.build(new File(arg[0]));
//        Annotation[] ann = annFromXML(document.getRootElement(), document.getRootElement().getValue(), new Annotation[0]);
//        for (int i=0 ; i<ann.length ; i++) {
//            System.out.println("- " + ann[i] + ".");
//        }
    }



    // create the Annotation's table corresponding to the the XML file
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


    // returns, depending on the 3rd parameter, the precision, the recall or the F-measure of the similarity score
    // 3rd parameter :  weakprecision || weakrecall || weakF-measure ||
    //                  strictprecision || strictrecall || strictF-measure ||
    //                  weightedprecision || weightedrecall || weightedF-measure
    public static float score(Annotation[] th, Annotation[] tr, String typeScore, String typeAlignment) {
        Annotation[] correspTh = alignAnnotations(th, tr, typeAlignment); // put in an annotations table the intersecting annotations which are of the same type from both Annotation tables
        float result = -1;
        int nbMatches = 0;

        // adjust the matches number depending on the 3rd parameter
        for (int i=0 ; i<correspTh.length ; i++) {
            if (correspTh[i] != null) {
                if (typeScore.substring(0, 4).equals("weak")) {
                    nbMatches++;
                }
                if (typeScore.substring(0, 6).equals("strict")) {
                    if (correspTh[i].getStart() == th[i].getStart()
                            && correspTh[i].getEnd() == th[i].getEnd()) {
                        nbMatches++;
                    }
                }
                if (typeScore.substring(0, 8).equals("weighted")) {
                    nbMatches += correspTh[i].intersectionPercentage(th[i]);
                }
            }
        }

        // choose the correct calculation depending on the 3rd parameter
        if (typeScore.substring(typeScore.length()-9, typeScore.length()).equals("precision")) {
            result = (float) nbMatches / th.length;
        }
        if (typeScore.substring(typeScore.length()-6, typeScore.length()).equals("recall")) {
            result = (float) nbMatches / tr.length;
        }
        if (typeScore.substring(typeScore.length()-9, typeScore.length()).equals("F-measure")) {
            result = (float) ( 2 * (nbMatches/th.length) * (nbMatches/tr.length) ) / ( (nbMatches/th.length) + (nbMatches/tr.length) );
        }

        return result;
    }


    // returns an Annotation table containing the intersecting annotations which are of the same type from both Annotation tables
    public static Annotation[] alignAnnotations(Annotation[] th, Annotation[] tr, String algo) {
        Annotation[] correspTh = new Annotation[th.length];

        if (algo.equals("multiple")) {
            for (int i=0 ; i<th.length ; i++) {
                for (int j=0 ; j<tr.length ; j++) {
                    if (th[i].intersect(tr[j]) == true
                            && th[i].getType().equals(tr[j].getType())) {
                        correspTh[i] = tr[j];
                    }
                }
            }
        }

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
                correspTh[imax] = tr[jmax];

                // delete the column and the line corresponding with the added annotation
                for (int i=0 ; i<t.length ; i++) {
                    t[i][jmax] = 0;
                }
                for (int j=0 ; j<t[0].length ; j++) {
                    t[imax][j] = 0;
                }
            }
        }

//        else if (algo.equals("maximum matching")) {
//
//        }

        return correspTh;
    }


    // test that an annotation of the file .ann matches with the correct location in the file .txt
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


    // load all the annotations of the file in an Annotation table
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