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
        // add null object to index 0, making arraylist base 1
        // to mirror logical indexing of csv in spreadsheet.
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
        }
    }

    public void validateRange(int indexBegin, int indexEnd) {
        if (indexBegin < 1 || indexEnd > swingSamples.size()) {
            throw new IndexOutOfBoundsException("Index values must be within bounds of sample set");
        }

        if (indexBegin > indexEnd) {
            throw new IllegalArgumentException("Illegal index range - Check function params: "
                    + indexBegin + " : " + indexEnd);
        }
    }

    /**
     * <P>from indexBegin to indexEnd, search data for values that are higher than threshold.
     * Return the first index where data has values that meet this criteria for at least winLength samples.
     *
     * @return int|-1 first(lowest) index of first continuous run of qualifying values of at least winLength.
     *  -1 if no continuities of size winLength are found
     * @throws IndexOutOfBoundsException if either indexBegin or indexEnd is outside bounds of sample set
     * @throws IllegalArgumentException if indexBegin is greater than indexEnd
     * */
    public int searchContinuityAboveValue(column data, int indexBegin, int indexEnd,
                                          double threshold, int winLength){
        ArrayList<IndexPair> results;

        validateRange(indexBegin, indexEnd);

        results = SamplePredicates.filterData(swingSamples.subList(indexBegin, indexEnd), SamplePredicates
                .isAboveValue(data, threshold), winLength);
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

        validateRange(indexEnd, indexBegin);

        results = SamplePredicates.filterData(swingSamples.subList(indexEnd, indexBegin), SamplePredicates
                .isBetweenValues(data, thresholdLo, thresholdHi), winLength);
        if(results == null){
            return -1;
        }

        return results.get(results.size()-1).getEndIndex();

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

        validateRange(indexBegin, indexEnd);

        results = SamplePredicates.filterData(swingSamples.subList(indexBegin, indexEnd), SamplePredicates.isAboveValue(data1, threshold1)
                .and(SamplePredicates.isAboveValue(data2, threshold2)), winLength);

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
        validateRange(indexBegin, indexEnd);

        return SamplePredicates.filterData(swingSamples.subList(indexBegin, indexEnd), SamplePredicates
                .isBetweenValues(data, thresholdLo, thresholdHi), winLength);

    }


    /**
     * <P>Method for ease of printing specified range of sample values/rows
     *
     * @param indexBegin - index at which to start printing/iteration
     * @param indexEnd - Index at which to end printing/iteration
     * @throws IndexOutOfBoundsException if either index is outside of sample data bounds
     */
    public void printSwing(ArrayList<SwingSample> s, int indexBegin, int indexEnd){

        validateRange(indexEnd, indexBegin);

        for(int i = indexBegin; i < indexEnd; i++){
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