package gemini;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Visualization {
	private Document doc1;
	private Document doc2;
	public Visualization(Document doc1, Document doc2) {
		this.doc1 = doc1;
		this.doc2 = doc2;
		if(!checkTwoFileSame()) {
			throw new IllegalArgumentException(" Can't Visualize. After removing XML tags, two texts aren't exactly the same. ");
		}
	}
	private boolean checkTwoFileSame() {
		return doc1.getRootElement().getValue().equals(doc2.getRootElement().getValue());
	}
	
	public void display(String annotationType) throws IOException {
		String newfile = "./result_annotations.html";/*
		XMLOutputter outp = new XMLOutputter();
        //outp.setFormat(Format.getCompactFormat());
        outp.getFormat().setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        StringWriter sw = new StringWriter();
        try {
			outp.output(doc1.getRootElement().getContent(), sw);
		} catch (IOException e) {}
        StringBuffer sb = sw.getBuffer();
        String str = sb.toString();*/
        StringBuilder result = new StringBuilder(doc1.getRootElement().getValue());
        
        Annotation[] f1 = Main.annFromXML(doc1.getRootElement(), doc1.getRootElement().getValue());
        Annotation[] f2 = Main.annFromXML(doc2.getRootElement(), doc2.getRootElement().getValue());
        Annotation[] ann1 = Main.oneTypeAnnotations(f1, annotationType);
        Annotation[] ann2 = Main.oneTypeAnnotations(f2, annotationType);
        
        Map<Integer,Integer> changeStatus = new TreeMap<>();
        // status=1 : 1 start;		status=2 : 2 start;		status=3 : 1 end;		status=4 : 2 end;
        // status=5 : 1+2;			status=6 : 1+4;			status=7 : 2+3;			status=8 : 3+4;
        // status=9 : 1 start 2 true;	status=10 : 2 start 1 true;
        // status=11 : 1 end 2 true;		status=12 : 2 end 1 true;
        int status = 1;
        for (Annotation ann:ann1) {
     	   	if(ann.getStart() == ann.getEnd())	continue;
            changeStatus.put(ann.getStart(), 1);
            changeStatus.put(ann.getEnd(), 3);
         }
        for (Annotation ann:ann2) {
        		if(ann.getStart() == ann.getEnd())	continue;
        		status = changeStatus.containsKey(ann.getStart()) ? changeStatus.get(ann.getStart()) + 4 : 2;
        		changeStatus.put(ann.getStart(), status);
        		status = changeStatus.containsKey(ann.getEnd()) ? changeStatus.get(ann.getEnd()) + 5 : 4;
        		changeStatus.put(ann.getEnd(), status);
        }

        int nbLetterBefore = 0;
        int textStatus = 0;
        int i,c;
        
        Entry<Integer,Integer>[] entrys = changeStatus.entrySet().toArray(new Entry[changeStatus.size()]);
        if(entrys.length == 0) {
        		System.err.println(" Can't find any annotations with type "+annotationType);
        		return;
        }
        
        for(int x=1;x<3;x++,textStatus=0) {
        for(i=0;i<changeStatus.size()-1;i++) {
        		if(x==2)
        		for(c=0; c<textStatus; c++) {
        			result.insert(entrys[i].getKey() + nbLetterBefore, "</span>");
        			nbLetterBefore += 7;
        		}
        		if(entrys[i].getValue() == 1 || entrys[i].getValue() == 6) {
        			if(entrys[i].getValue() == 6)
        				textStatus--;
        			if(textStatus == 1) 
        				entrys[i].setValue(9);
        			else {
        				if(x==2) {
		        			if(entrys[i+1].getValue() == 3 || entrys[i+1].getValue() == 7)
		        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_all\">");
		        			if(entrys[i+1].getValue() == 10)
		        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_sta\">");
		        			nbLetterBefore += 22;
        				}
	                	textStatus++;
        			}
        		}
        		
        		if(entrys[i].getValue() == 2 || entrys[i].getValue() == 7) {
        			if(entrys[i].getValue() == 7)
        				textStatus--;
        			if(textStatus == 1) 
        				entrys[i].setValue(10);
        			else {
        				if(x==2) {
		        			if(entrys[i+1].getValue() == 4 || entrys[i+1].getValue() == 6)
		        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT2_all\">");
		        			if(entrys[i+1].getValue() == 9)
		        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT2_sta\">");
		        			nbLetterBefore += 22;
        				}
	                	textStatus++;
        			}
        		}
        		
        		if(entrys[i].getValue() == 3 || entrys[i].getValue() == 4) {
        			if(textStatus == 2) 
        				entrys[i].setValue(entrys[i].getValue() + 8);
        			else
        				textStatus--;
        		}
        		
        		if(entrys[i].getValue() == 5) {
        			if(x==2){
	        			if(entrys[i+1].getValue() == 11)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_all\"><span class=\"TT2_sta\">");
	        			if(entrys[i+1].getValue() == 12)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_sta\"><span class=\"TT2_all\">");
	        			if(entrys[i+1].getValue() == 8)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_all\"><span class=\"TT2_all\">");
	        			nbLetterBefore += 44;
        			}
                	textStatus+=2;
        		}
        		
        		if(entrys[i].getValue() == 8)
        			textStatus-=2;
        		
        		if(entrys[i].getValue() == 9) {
        			if(x==2) {
	        			if(entrys[i+1].getValue() == 11)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_all\"><span class=\"TT2_mid\">");
	        			if(entrys[i+1].getValue() == 12)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_sta\"><span class=\"TT2_end\">");
	        			if(entrys[i+1].getValue() == 8)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_all\"><span class=\"TT2_end\">");
	        			nbLetterBefore += 44;
        			}
                	textStatus++;
        		}
        		
        		if(entrys[i].getValue() == 10) {
        			if(x==2) {
	        			if(entrys[i+1].getValue() == 11)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_end\"><span class=\"TT2_sta\">");
	        			if(entrys[i+1].getValue() == 12)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_mid\"><span class=\"TT2_all\">");
	        			if(entrys[i+1].getValue() == 8)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_end\"><span class=\"TT2_all\">");
	        			nbLetterBefore += 44;
        			}
                	textStatus++;
        		}
        		
        		if(entrys[i].getValue() == 11) {
        			if(x==2) {
	        			if(entrys[i+1].getValue() == 4  || entrys[i+1].getValue() == 6)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT2_end\">");
	        			if(entrys[i+1].getValue() == 9)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT2_mid\">");
	        			nbLetterBefore += 22;
        			}
                	textStatus--;
        		}
        		
        		if(entrys[i].getValue() == 12) {
        			if(x==2) {
	        			if(entrys[i+1].getValue() == 3 || entrys[i+1].getValue() == 7)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_end\">");
	        			if(entrys[i+1].getValue() == 10)
	        				result.insert(entrys[i].getKey() + nbLetterBefore, "<span class=\"TT1_mid\">");
	        			nbLetterBefore += 22;
        			}
                	textStatus--;
        		}

        } 
        if(x==2)
        for(c=0; c<=textStatus; c++) {
			result.insert(entrys[i].getKey() + nbLetterBefore, "</span>");
		}
        
       // System.out.println("x=1 end -> nbLetterBefore: "+nbLetterBefore + "  =  textStatus :   "+ textStatus);
        
        }
        
        
        // SKY BLUE:#66d9ff	RED:#ff4d4d	GREEN:#66ffb3	PURPLE:#ccb3ff	PINK:#ff80bf
        //font-weight:bold;\ncolor:#ff9955;\n
        String formatCSS = "<style>\nbody {background:#A4A4A4;}	\n"+
        //"p.big {line-height:200%}\n"+
        ".TT1_all {border:2px;\nborder-style:solid;\nborder-color:#ffb3d9;\npadding: 4px;}\n "+
        		".TT2_all {border:2px;\nborder-style:solid;\nborder-color:#66d9ff;\n}\n"+
        		".TT1_sta{\nborder-top: 2px solid #ffb3d9;\nborder-bottom: 2px solid #ffb3d9;\nborder-left: 2px solid #ffb3d9;\npadding: 4px;\n}\n"+
        		".TT1_end{\nborder-top: 2px solid #ffb3d9;\nborder-bottom: 2px solid #ffb3d9;\nborder-right: 2px solid #ffb3d9;\npadding: 4px;\n}\n"+
        		".TT1_mid{\nborder-top: 2px solid #ffb3d9;\nborder-bottom: 2px solid #ffb3d9;\npadding: 4px;\n}\n"+
        		".TT2_sta{\nborder-top: 2px solid #66d9ff;\nborder-bottom: 2px solid #66d9ff;\nborder-left: 2px solid #66d9ff;\n}\n"+
        		".TT2_end{\nborder-top: 2px solid #66d9ff;\nborder-bottom: 2px solid #66d9ff;\nborder-right: 2px solid #66d9ff;\n}\n"+
        		".TT2_mid{\nborder-top: 2px solid #66d9ff;\nborder-bottom: 2px solid #66d9ff;\n}\n"+
        		".comment{\ntext-align: center;\nposition:fixed;\nbottom: 0;\nright: 0;\nheight: 130px;\nwidth: 30%;\nz-index:99;\n" + 
        		"border: 5px solid #B0C4DE;\nbackground-color: #E6E6FA;\npadding-top:10px;\n}\n" + 
        		".comment1{\ndisplay: flex;\nalign-items: center;\nwidth:100%;\nheight:40px;\npadding-left:20px;\npadding-top:10px;\n}\n" + 
        		".comment1_example{\nborder:2px solid #ffb3d9;\npadding: 4px;\nfloat:left;\nwidth:40px;\n}\n" + 
        		".comment1_explain{\npadding-left:10px;\nfont-size: 80%;\ntext-align: left;\n}\n" + 
        		".comment2{\ndisplay: flex;\nalign-items: center;\nwidth:100%;\nheight:40px;\npadding-left:20px;\n}\n" + 
        		".comment2_example{\nborder:2px solid #66d9ff;\npadding: 4px;\nfloat:left;\nwidth:40px;\n}\n" + 
        		".comment2_explain{\npadding-left:10px;\nfont-size: 80%;\ntext-align: left;\n}\n"+
        "</style>\n";
        String[] path1 = doc1.getBaseURI().split("/");
        String[] path2 = doc2.getBaseURI().split("/");
        String floatingWindow = "<div class=\"comment\">Annotation &#60"+ annotationType +"&#62;\n" + 
        		"        <div class=\"comment1\">\n" + 
        		"            <div class=\"comment1_example\">Text</div>\n" + 
        		"            <div class=\"comment1_explain\">File1 : "+path1[path1.length-1]+"</div>\n" + 
        		"        </div>\n" + 
        		"        <div class=\"comment2\">\n" + 
        		"            <div class=\"comment2_example\">Text</div>\n" + 
        		"            <div class=\"comment2_explain\">File2 : "+path2[path2.length-1]+"</div>\n" + 
        		"        </div>\n" + 
        		"    </div>";
        
		String htmlPage = "<!DOCTYPE html>\n<html lang=\"fr\">\n<head>\n<meta charset=\"utf-8\" />\n<title>Result</title>\n<meta name=\"description\" content=\"This page is created by Visualization.java\" />\n"
				+ formatCSS +"\n</head> <body>\n "+ floatingWindow
				+ "\n<p style=\"line-height:200%\">"  + applyFormatHTML(result.toString()) + "</p> \n</body> \n</html> ";
		//str
		File file = new File(newfile);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.append(htmlPage);
		writer.flush();
		writer.close();
		
		System.out.println(htmlPage);
	}
	
	private static String applyFormatHTML(String origin) {
		//origin = origin.replace("\r\n", "\n");
		//origin = origin.replace("\n\n", "</p><p>");
		origin = origin.replace("\n", "</br>\n");
		return origin;
	}
	
	public static void main(String args[]) throws JDOMException, IOException {
		SAXBuilder sxb = new SAXBuilder();
		//String xmlfile1 = "/Users/FENGYuheng/Desktop/fichierXML1.xml";
		//String xmlfile2 = "/Users/FENGYuheng/Desktop/fichierXML2.xml";
		String xmlfile1 = "/Users/FENGYuheng/Desktop/CorpusAPILGRAMLAB_2011-06_annoteEleni.xml";
		String xmlfile2 = "/Users/FENGYuheng/Desktop/CorpusAPILGRAMLAB_2011-06_annote_Lidia4.xml";
		
		Visualization vis = new Visualization(sxb.build(new File(xmlfile1)),sxb.build(new File(xmlfile2)));
		vis.display("EvenTour");
	}
}
