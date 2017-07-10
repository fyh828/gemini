import java.io.*;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.filter.*;
import java.util.List;
import java.util.Iterator;

public class Main {

    public static void main(String[] arg) throws JDOMException, IOException {

        // load annotations of the file in the first parameter
//        Annotation[] allFile1 = loadAnnotations(arg[0]);
//        Annotation[] allFile2 = loadAnnotations(arg[1]);
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
//        Annotation[] corresp = alignAnnotations(allFile1, allFile2);
//        for (int i=0 ; i<corresp.length ; i++) {
//            System.out.println(corresp[i]);
//        }

        // test score
//        System.out.println("Similarity score : "+score(allFile1, allFile2, arg[2]));

        // test annFromXML
        SAXBuilder sxb = new SAXBuilder();
        Document document = sxb.build(new File("test.xml"));
        Element root = document.getRootElement();
        String t = "";
        Annotation[] a = new Annotation[0];
        Annotation[] ann = annFromXML(root, t, 0, a);
        for (int i=0 ; i<ann.length ; i++) {
            System.out.println(ann[i]);
        }
    }



    public static Annotation[] annFromXML(Element node, String t, int id, Annotation[] a) {
        id++;
        String n = "T" + id;
        Annotation[] b = new Annotation[a.length+1];
        b[b.length-1] = new Annotation(n, "unknown", t.length(), t.length() + node.getText().length(), node.getText());
        t += node.getText();

        List tags = node.getChildren();
        Iterator i = tags.iterator();

        while (i.hasNext()) {
            Element current = (Element)i.next();
            a = annFromXML(current, t, id, b);
        }

        return a;
    }


    // returns, depending on the 3rd parameter, the precision, the recall or the F-measure of the similarity score
    // 3rd parameter :  weakprecision || weakrecall || weakF-measure ||
    //                  strictprecision || strictrecall || strictF-measure ||
    //                  weightedprecision || weightedrecall || weightedF-measure
    public static float score(Annotation[] th, Annotation[] tr, String typeScore) {
        Annotation[] correspTh = alignAnnotations(th, tr); // put in an annotations table the intersecting annotations which are of the same type from both Annotation tables
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
    public static Annotation[] alignAnnotations(Annotation[] th, Annotation[] tr) {
        Annotation[] correspTh = new Annotation[th.length];

        for (int i=0 ; i<th.length ; i++) {
            for (int j=0 ; j<tr.length ; j++) {
                if (th[i].intersect(tr[j]) == true
                        && th[i].getType().equals(tr[j].getType())) {
                    correspTh[i] = tr[j];
                }
            }
        }

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