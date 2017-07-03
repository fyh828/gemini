public class Annotation{

    public String id;
    public String type;
    public int start;
    public int end;
    public String label;

    public Annotation(String id, String type, int start, int end, String label){
        this.id = id;
        this.type = type;
        this.start = start;
        this.end = end;
        this.label = label;
    }

    public void setId(String id){
        this.id = id;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setStart(int start){
        this.start = start;
    }
    public void setEnd(int end){
        this.end = end;
    }
    public void setLabel(String label){
        this.label = label;
    }

    public String getId(){
        return this.id;
    }
    public String getType(){
        return this.type;
    }
    public int getStart(){
        return this.start;
    }
    public int getEnd(){
        return this.end;
    }
    public String getLabel(){
        return this.label;
    }

    public String toString(){
        return id + " : " + label + " (" + start + "-" + end + ")";
    }

    public boolean intersecte(Annotation a) {
        if (a.getStart() < this.end && a.getStart() > this.start
                || a.getEnd() < this.end && a.getEnd() > this.start) {
            return true;
        } else {
            return false;
        }
    }
}