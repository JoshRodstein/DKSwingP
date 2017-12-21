/*
 * Class: IndexPair - holds start and end index of a continuous 
 * run of values in a swing data table. 
 *
 */

public class IndexPair {
    private int start_index;
    private int end_index;


    public IndexPair(){
        this.start_index = -1;
        this.end_index = -1;
    }

    public IndexPair(int start, int end){
        this.start_index = start;
        this.end_index = end;
    }

    public void setStartIndex(int start){
        this.start_index = start;
    }

    public void setEndIndex(int end){
        this.end_index = end;
    }

    public int getStartIndex(){
        return this.start_index;
    }

    public int getEndIndex(){
        return this.end_index;
    }

}