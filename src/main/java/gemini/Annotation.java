/**
 * The {@code Annotation} class an annotation of a word or a word group in a text.
 */

/*
Copyright 2017-2018 Coline Mignot, Philippe Gambette

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

    /**
     * Check if the annotation intersects with the one in parameter.
     *
     * @param a the annotation to compare with
     * @return {@code true} if there is an intersection in common between the two annotations
     *         {@code false} otherwise
     */
    public boolean intersect(Annotation a) {
        return a.getStart() >= this.start && a.getStart() <= this.end
                || this.start >= a.getStart() && this.start <= a.getEnd();
    }

    /**
     * Returns the size of the segment common to both annotations.
     *
     * @param a the annotation to compare
     * @return the size of the segment common to both annotations
     */
    public int intersectionSize(Annotation a) {
        if (a.getStart() <= this.start && a.getEnd() >= this.start && a.getEnd() <= this.end) {
            return a.getEnd() - this.start;
        }
        else if (a.getStart() >= this.start && a.getEnd() <= this.end) {
            return a.getEnd() - a.getStart();
        }
        else if (a.getStart() >= this.start && a.getStart() <= this.end && a.getEnd() >= this.end) {
            return this.end - a.getStart();
        }
        else if (a.getStart() <= this.start && a.getEnd() >= this.end) {
            return this.end - this.start;
        }
        else {
            return 0;
        }
    }

    /**
     * Returns the percentage of the intersection size.
     *
     * @param a the annotation to compare with
     * @return the percentage of the intersection size
     */
    public float intersectionPercentage(Annotation a) {
        return (float) intersectionSize(a) / ( (a.getEnd()-a.getStart()) + (this.end-this.start) - intersectionSize(a) );
    }
    
    @Override
    public boolean equals(Object o) {
    		if(this == o) return true;
    		if(!(o instanceof Annotation)) return false;
    		Annotation ann = (Annotation) o;
    		return this.type == ann.type && this.id == ann.id && this.start == ann.start && this.end == ann.end && this.label == ann.label;
    }
    
    @Override
    public int hashCode() {
    		int result = 17;
    		result = 31 * result + id.hashCode();
    		result = 31 * result + type.hashCode();
    		result = 31 * result + start;
    		result = 31 * result + end;
    		result = 31 * result + label.hashCode();
    		return result;
    }
}