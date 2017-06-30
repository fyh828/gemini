import java.io.*;

public class Main {

    public static void main(String[] arg) {

        Annotation[] a = loadAnnotations(arg[0]);
        System.out.println(a[0]);
    }


    public static Annotation[] loadAnnotations(String file) {
        String[] openedFile = ouvreFichier(file);
        Annotation[] annotations = new Annotation[openedFile.length];

        for (int i=0 ; i<openedFile.length ; i++) {
            String[] attributes = openedFile[i].split("\t");
            annotations[i] = new Annotation(attributes[0], attributes[1], Integer.parseInt(attributes[2]), Integer.parseInt(attributes[3]), attributes[4]);
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