/*
* Basic driver for testing methods of SwingTable
*  
* Joshua Rodstein
* jor94@pitt.edu
* */

import java.io.*;
import java.util.ArrayList;

public class DKSwing {
    private static SwingTable aSwing;


    public static void main(String[] args){
        column data, data2;
        aSwing = null;
        try {
            File swingFile = new File(args[0]);
            aSwing = new SwingTable(swingFile);
        } catch(FileNotFoundException fn) {
            System.err.println("File not found");
        }

        data = column.WZ;
        int i = aSwing.searchContinuityAboveValue(data, 1,1200, 1, 20);
        printResults(data, null, 1, 1200, i, null);

        data = column.AX;
        int x = aSwing.backSearchContinuityWithinRange(data, 860,1, 1,
                16, 20 );
        printResults(data, null, 860, 1, x, null);

        data = column.AX;
        data2 = column.WX;
        int y = aSwing.searchContinuityAboveValueTwoSignals(data, data2, 1, 1200, 0,
                0, 5);
        printResults(data, data2, 1, 1200, y, null);

        data = column.AY;
        ArrayList<IndexPair> testList = aSwing.searchMultiContinuityWithinRange(data, 10, 900,
                2, 3, 3);
        printResults(data, null, 1, 1200, -1, testList);


        System.exit(0);

    }

    public static void printResults(column data1, column data2, int begin, int end, int result, ArrayList<IndexPair> i){
        if(i == null && result == -1) {
            System.out.println("No continuity above value found for sensor " + data1 + "\n");
            return;
        } else if (i != null) {
            System.out.println("All runs of continuous values for sensor "+ data1 + " from index[1 - 1200]");
            for(int z = 0; z < i.size(); z++){
                System.out.println("\tStart["+i.get(z).getStartIndex() + "] : End[" + i.get(z).getEndIndex()+"]");
            }
            return;
        }

        if (result != -1) {
            if (data2 == null) {
                System.out.println("From index [" + begin + " - " + end + "] for sensor " + data1 + ",  \n\tStart Index[" + result + "]\n");
            } else {
                System.out.println("From index [" + begin + " - " + end + "] for sensor " + data1 + ", " + data2 + ",  \n\tStart Index[" + result + "]\n");
            }
        } else {
            if (data2 == null) {
                System.out.println("No continuity above value found for sensor " + data1 +"\n");
            } else {
                System.out.println("No continuity above value found for sensor " + data1 + ", "+data2+"\n");
            }
        }
    }
}