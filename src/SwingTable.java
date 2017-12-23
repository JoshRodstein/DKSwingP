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
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    public ArrayList<IndexPair> filterData (List<SwingSample> sampleList, Predicate<SwingSample> predicate,
                                                   int runLength) {
        // returns all occurances (not continuous runs)
        ArrayList<SwingSample> filterList = new ArrayList<>();
        filterList = sampleList.stream().filter(predicate).collect(Collectors.toCollection(ArrayList::new));

        int count = 1, found = 0;
        int size = filterList.size() - 1;
        ArrayList<IndexPair> indexList = new ArrayList<IndexPair>();

        if (size <= 0){
            return null;
        }

        found = filterList.get(0).getIndex();
        for(int i = 0; i < size; i++) {
            if(filterList.get(i+1).getIndex() - filterList.get(i).getIndex() == 1) {
                if(i+1 == size){
                    indexList.add(new  IndexPair(found, filterList.get(i).getIndex()) );
                }
                count++;
            } else {
                if(count >= runLength){
                    indexList.add(new  IndexPair(found, filterList.get(i).getIndex()) );
                }
                count = 1;
                found = filterList.get(i + 1).getIndex();
            }
        }

        if(indexList.size() == 0){
            return null;
        }
        System.out.println(indexList.size());
        return indexList;

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

        ArrayList<IndexPair> results;

        if(indexBegin < 0 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }
        if (indexBegin > indexEnd) {
            throw new IllegalArgumentException("Begin index: " + indexBegin + ", may not be greater than end index: " + indexEnd);
        }

        results = filterData(swingSamples.subList(indexBegin, indexEnd), Predicates.isAboveValue(data, threshold), winLength);
        if(results == null){
            return -1;
        }
        return results.get(0).getStartIndex();
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
        ArrayList<IndexPair> results;

        if(indexBegin < 1 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }
        if (indexBegin < indexEnd) {
            throw new IllegalArgumentException("End index: " + indexEnd + ", may not be greater than begin index: " + indexBegin);
        }

        results = filterData(swingSamples.subList(indexEnd, indexBegin), Predicates.isBetweenValues(data, thresholdLo, thresholdHi), winLength);

        if(results == null){
            return -1;
        }

        return results.get(results.size()-1).getStartIndex();

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
        ArrayList<IndexPair> results;

        if(indexBegin < 1 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }

        if (indexBegin > indexEnd) {
            throw new IllegalArgumentException("Begin index: " + indexBegin + ", may not be greater than end index: " + indexEnd);
        }

        results = filterData(swingSamples.subList(indexBegin, indexEnd), Predicates.isAboveValue(data1, threshold1)
                .and(Predicates.isAboveValue(data2, threshold2)), winLength);

        if(results == null){
            return -1;
        }

        return results.get(0).getStartIndex();
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
        ArrayList<IndexPair> results;

        if(indexBegin < 1 || indexEnd > swingSamples.size()){
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }
        if (indexBegin > indexEnd) {
            throw new IllegalArgumentException("Begin index: " + indexBegin + ", may not be greater than end index: " + indexEnd);
        }

        return filterData(swingSamples.subList(indexBegin, indexEnd), Predicates.isBetweenValues(data, thresholdLo, thresholdHi), winLength);
        
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


    public void printSwing(ArrayList<SwingSample> s){


        for(int i = 0; i < s.size(); i++){
            System.out.println();
            System.out.print(s.get(i).getXYZ(column.TIMESTAMP));
            System.out.print(", " + s.get(i).getXYZ(column.AX));
            System.out.print(", " + s.get(i).getXYZ(column.AY));
            System.out.print(", " + s.get(i).getXYZ(column.AZ));
            System.out.print(", " + s.get(i).getXYZ(column.WX));
            System.out.print(", " + s.get(i).getXYZ(column.WY));
            System.out.print(", " + s.get(i).getXYZ(column.WZ));
        }
    }

}