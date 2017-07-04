import java.io.*;

public class Main {

    public static void main(String[] arg) {

        // load annotations of the file in the first parameter
        Annotation[] allFile1 = loadAnnotations(arg[0]);
        Annotation[] allFile2 = loadAnnotations(arg[1]);
//        for (int i=0 ; i<allFile1.length ; i++) {
//            System.out.println(allFile1[i]);
//        }
//        Annotation a = allFile1[Integer.parseInt(arg[2])];
//        System.out.println(testAnnotation(arg[0], a));

        // test all annotations of the file
        // incorrect line : n° line
        String test = "Incorrect annotations : ";
        for (int i=0 ; i<allFile1.length ; i++) {
            Annotation a = allFile1[i];
            if (testAnnotation(arg[0], a) == false) {
                test += a.getId() + ", ";
            }
        }
        System.out.println(test);

//        Annotation a = allFile1[Integer.parseInt(arg[2])];
//        Annotation b = allFile2[Integer.parseInt(arg[3])];
//        System.out.println("\n" + a.intersectionPercentage(b));

        System.out.println("score : "+score(allFile1, allFile2, arg[2]));
    }



    public static float score(Annotation[] th, Annotation[] tr, String typeScore) {
        Annotation[] correspTh = alignAnnotations(th, tr);

        if (typeScore.substring(0, 4).equals("weak")) {

        }
        if (typeScore.substring(0, 4).equals("stri")) {

        }
        if (typeScore.substring(0, 4).equals("weig")) {

        }

        if (typeScore.substring(typeScore.length()-6, typeScore.length()).equals("cision")) {
            int nbMatches = 0;
            for (int i=0 ; i<correspTh.length ; i++) {
                if (correspTh[i] != null) {
                    nbMatches++;
                }
            }
            float precision = nbMatches / th.length;
            return precision;
        }
        if (typeScore.substring(typeScore.length()-6, typeScore.length()).equals("recall")) {

        }
        if (typeScore.substring(typeScore.length()-6, typeScore.length()).equals("easure")) {

        }

        return 0;
    }


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