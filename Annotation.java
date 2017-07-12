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
        return id + " : " + label + " (" + type + " ; " + start + "-" + end + ")";
    }

    // check if the annotation intersects with the one in parameter
    public boolean intersect(Annotation a) {
        return a.getStart() <= this.end && a.getStart() >= this.start
                || a.getEnd() <= this.end && a.getEnd() >= this.start;
    }

    // returns the size of the segment common to both annotations
    public int intersectionSize(Annotation a) {
        if (a.getStart() <= this.end && a.getStart() >= this.start) {
            return this.end - a.getStart();
        }
        else if (a.getEnd() <= this.end && a.getEnd() >= this.start) {
            return a.getEnd() - this.start;
        }
        else {
            return 0;
        }
    }

    // returns the persentage of the intersection size
    public float intersectionPercentage(Annotation a) {
        return (float) intersectionSize(a) / ( a.getLabel().length() + this.label.length() - intersectionSize(a) );
    }
}