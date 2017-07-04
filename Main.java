import java.io.*;

public class Main {

    public static void main(String[] arg) {

        Annotation[] all = loadAnnotations(arg[0]);
//        for (int i=0 ; i<all.length ; i++) {
//            System.out.println(all[i]);
//        }
        Annotation a = all[Integer.parseInt(arg[1])];
        System.out.println(testAnnotation(arg[0], a));
    }



    public static Annotation[] aligneAnnotations(Annotation[] th, Annotation[] tr) {
        Annotation[] correspTh = new Annotation[th.length];

        for (int i=0 ; i<th.length ; i++) {
            for (int j=0 ; j<tr.length ; j++) {
                if (th[i].intersecte(tr[j]) == true
                        && th[i].getType().compareTo(tr[j].getType()) == 0) {
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

        if (a.getLabel().compareTo(text.substring(a.getStart(), a.getEnd())) == 0) {
            System.out.println(a.getLabel());
            System.out.println(text.substring(a.getStart(), a.getEnd()));
            return true;
        } else {
            System.out.println(a.getLabel());
            System.out.println(text.substring(a.getStart(), a.getEnd()));
            return false;
        }
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