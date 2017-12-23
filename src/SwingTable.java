/*
 * Problem Set for Diamond Kinetics:
 *  -Implement data structure for sample swing data in 'latestSwing.csv'
 *  -Implement 4 methods for searching continuous values
 *
 *  Array list backed ADT, with parralel mapping of timestamps to
 *  to corresponding ArrayList index. Makes possible O(1) search for sample
 *  by timeStamp.
 *
 *  Currently indexes start at 1 to mirror logical indexing of csv spreadsheet
 *
 *  Joshua Rodstein
 *  jor94@pitt.edu
 *
 *  */

import java.util.*;
import java.io.*;

public class SwingTable {
    private ArrayList<SwingSample> swingSamples;
    private Map<Integer, Integer> indexMap;
    private SwingSample sample;
    private Scanner scan;


    public SwingTable(File swingData) throws FileNotFoundException{
        scan = new Scanner(swingData);
        swingSamples = new ArrayList<SwingSample>();
        indexMap = new HashMap<Integer, Integer>();

        // add null object to index 0, making arraylist base 1
        // to mirror logical indexing of csv in spreadsheet.
        // Parse file and populate swingSamples in parralel with indexMap
        swingSamples.add(null);
        while(scan.hasNextLine()){
            String[] row = scan.nextLine().split(",");
            sample = new SwingSample(
                    swingSamples.size(),
                    Integer.decode(row[0]),
                    Double.parseDouble(row[1]),
                    Double.parseDouble(row[2]),
                    Double.parseDouble(row[3]),
                    Double.parseDouble(row[4]),
                    Double.parseDouble(row[5]),
                    Double.parseDouble(row[6]));
            swingSamples.add(sample);
            indexMap.put(sample.getTimestamp(), swingSamples.size());
        }
    }


    /**
     * <P>from indexBegin to indexEnd, search data for values that are higher than threshold.
     * Return the first index where data has values that meet this criteria for at least winLength samples.
     *
     * @return int - first(lowest) index of first continuous run of qualifying values of at least winLength.
     * @return -1 if no continuities of size winLength are found
     * @throws IndexOutOfBoundsException if either indexBegin or indexEnd is outside bounds of sample set
     * @throws IllegalArgumentException if indexBegin is greater than indexEnd
     * */
    public int searchContinuityAboveValue(column data, int indexBegin, int indexEnd,
                                          double threshold, int winLength){
        int foundIndex = -1;
        double value = 0;
        ArrayList<IndexPair> results;

        if(indexBegin < 0 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }
        if (indexBegin > indexEnd) {
            throw new IllegalArgumentException("Begin index: " + indexBegin + ", may not be greater than end index: " + indexEnd);
        }

        results = Predicates.filterData(swingSamples.subList(1, indexEnd), Predicates.isAboveValue(data, threshold), winLength);
        if(results == null){
            return -1;
        }
        return results.get(0).getEndIndex();
    }

    /**
     * <P>from indexBegin to indexEnd (where indexBegin is larger than indexEnd), search data for values that
     * are higher than thresholdLo and lower than thresholdHi. Return the first index where data has values
     * that meet this criteria for at least winLength samples.
     *
     * @return int - first(lowest) index of first continuous run of qualifying values of at least winLength
     * @return -1 if no continuities of size winLength are found
     * @throws IndexOutOfBoundsException if either indexBegin or indexEnd is outside bounds of sample set
     * @throws IllegalArgumentException if indexEnd id greater than indexBegin
     * */
    public int backSearchContinuityWithinRange(column data, int indexBegin, int indexEnd,
                                               double thresholdLo, double thresholdHi, int winLength) {
        int foundIndex = -1;
        double value = 0;

        if(indexBegin < 1 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }
        if (indexBegin < indexEnd) {
            throw new IllegalArgumentException("End index: " + indexEnd + ", may not be greater than begin index: " + indexBegin);
        }

        // Iterate in decending order from begin to end index
        for(int i = indexBegin; i >= indexEnd; i--){
            value = swingSamples.get(i).getXYZ(data);
            // if value meets criteria, check if in a run
            // if not, then i is first index of new run
            if(value > thresholdLo && value < thresholdHi) {
                if(foundIndex == -1){
                    foundIndex = i;
                    // if we are in a run, and at the last index of our search
                    // we check if the run of continuity is at least winLength in length
                } else if (foundIndex == indexEnd && (foundIndex - i) >= winLength){
                    foundIndex = i;
                }
                // if value is not valid, and previous run is of valid length
                // then (i + 1) is first index of the continuity. set index and break
                // otherwise reset index and continue search
            } else if (foundIndex != -1){
                if(foundIndex - i >= winLength){
                    foundIndex = i + 1;
                    break;
                } else {
                    foundIndex = -1;
                }
            }
        }

        return foundIndex;
    }

    /**
     * <P>from indexBegin to indexEnd, search data1 for values that are higher than threshold1 and also search
     * data2 for values that are higher than threshold2. Return the first index where both data1 and data2
     * have values that meet these criteria for at least winLength samples.
     *
     * @return int - first(lowest) index of first continuous run of qualifying values of at least winLength
     * @return -1 if no continuities of size winLength are found
     * @throws IndexOutOfBoundsException if either indexBegin or indexEnd is outside bounds of sample set
     * @throws IllegalArgumentException if indexBegin is greater than indexEnd
     * */
    public int searchContinuityAboveValueTwoSignals(column data1, column data2, int indexBegin,
                                                    int indexEnd, double threshold1, double threshold2, int winLength){
        int foundIndex = -1;
        double value1 = 0;
        double value2 = 0;

        if(indexBegin < 1 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }

        if (indexBegin > indexEnd) {
            throw new IllegalArgumentException("Begin index: " + indexBegin + ", may not be greater than end index: " + indexEnd);
        }

        for(int i = indexBegin; i <= indexEnd; i++){
            value1 = swingSamples.get(i).getXYZ(data1);
            value2 = swingSamples.get(i).getXYZ(data2);
            // if value1 and value2 meets search criteria 1 and 2
            // check if we are in the middle of a run of continuity.
            // if not, then i is the first index of new continuity
            if(value1 > threshold1 && value2 > threshold2) {
                if(foundIndex == -1){
                    foundIndex = i;
                }
                // if value1 and value 2 are not valid, check in in run of continuity
                // if so and run of cont. was at least winLenght in length, break.
                // if not, reset index and keep searching
            } else if (foundIndex != -1){
                if(i - foundIndex >= winLength){
                    break;
                } else {
                    foundIndex = -1;
                }
            }
        }

        return foundIndex;

    }

    /**
     * <P>from indexBegin to indexEnd, search data for values that are higher than thresholdLo and lower than
     * thresholdHi. Return the the starting index and ending index of all continuous samples that meet this
     * criteria for at least winLength data points.
     *
     * @return ArrayList<IndexPair> - List of objects containing first and last (lowest to highest) int index
     * pairs of all found continuous runs of qualifying values of at least winlength
     * @return empty ArrayList if no continuities of at least winLength length are found
     * @throws IndexOutOfBoundsException if either indexBegin or indexEnd is outside bounds of sample set
     * @throws IllegalArgumentException if indexBegin is greater than indexEnd
     * */
    public ArrayList<IndexPair> searchMultiContinuityWithinRange(column data, int indexBegin, int indexEnd,
                                                                 double thresholdLo, double thresholdHi, int winLength){
        ArrayList<IndexPair> indexList = new ArrayList<IndexPair>();
        int foundStart = -1;
        int foundEnd;
        double value = 0;

        if(indexBegin < 1 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }
        if (indexBegin > indexEnd) {
            throw new IllegalArgumentException("Begin index: " + indexBegin + ", may not be greater than end index: " + indexEnd);
        }

        for(int i = indexBegin; i <= indexEnd; i++){
            value = swingSamples.get(i).getXYZ(data);
            // if value meets criteria, check if in a run
            // if not, then i is first index of new run
            if(value > thresholdLo && value < thresholdHi) {
                if(foundStart == -1){
                    foundStart = i;
                    // if in middle of continuity, and at last index of search
                    // if if length of continuity is at least winLength in length
                    // if so, end index is i.
                } else if (foundStart == indexEnd && (foundStart - i) >= winLength){
                    foundEnd = i;
                    // add new indexPair to indexList
                    indexList.add(new IndexPair(foundStart, foundEnd));
                }
                // if value not valid, check if index i-1 was end of a valid run
                // if valid, i-1 is last index.
            } else if (foundStart != -1) {
                if(i - foundStart >= winLength){
                    foundEnd = i-1;
                    // add new indexPair to indexList
                    indexList.add(new IndexPair(foundStart, foundEnd));
                }
                foundStart = -1;
            }
        }

        return new ArrayList<>(indexList);
    }


    public boolean isValid(double ...v){



        return true;
    }


    /**
     * <P>Method for ease of printing specified range of sample values/rows
     *
     * @param indexBegin - index at which to start printing/iteration
     * @param indexEnd - Index at which to end printing/iteration
     * @throws IndexOutOfBoundsException if either index is outside of sample data bounds
     */
    public void printSwing(int indexBegin, int indexEnd){

        if(indexBegin < 1 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Invalid index values " + indexBegin + ", " + indexEnd);
        }

        for(int i = indexBegin; i < indexEnd; i++){
            System.out.println();
            System.out.print(swingSamples.get(i).getXYZ(column.TIMESTAMP));
            System.out.print(", " + swingSamples.get(i).getXYZ(column.AX));
            System.out.print(", " + swingSamples.get(i).getXYZ(column.AY));
            System.out.print(", " + swingSamples.get(i).getXYZ(column.AZ));
            System.out.print(", " + swingSamples.get(i).getXYZ(column.WX));
            System.out.print(", " + swingSamples.get(i).getXYZ(column.WY));
            System.out.print(", " + swingSamples.get(i).getXYZ(column.WZ));
        }
    }




}