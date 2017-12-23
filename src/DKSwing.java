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
        data = column.WX;
        int i = aSwing.searchContinuityAboveValue(data, 1,1200, 1, 20);
        if(i == -1){
            System.out.println("No continuity above value found for sensor " + data);
        } else {
            System.out.println("From index [1 - 1200] for sensor "+data+",  \n\tStart Index[" + i + "]\n");
        }

        data = column.AX;
        int x = aSwing.backSearchContinuityWithinRange(data, 860,1, 1,
                16, 20 );
        if(i == -1){
            System.out.println("No continuity within specified range found for sensor " + data);
        } else {
            System.out.println("From index [860 - 1] for sensor " + data + ", \n\tStart Index[" + x + "]\n");
        }

        data = column.AX;
        data2 = column.WX;
        int y = aSwing.searchContinuityAboveValueTwoSignals(data, data2, 1, 1200, 0,
                0, 5);
        if(i == -1){
            System.out.println("No continuity above values found for sensors " + data + " & " + data2);
        } else {
            System.out.println("From index [1 - 1200] for sensor " + data + " \n\tStart Index[" + y + "]\n");
        }

        data = column.AY;
        ArrayList<IndexPair> testList = aSwing.searchMultiContinuityWithinRange(data, 1, 1200,
                2, 3, 3);
        if(testList == null){
            System.out.println("No continuity above value found for sensor " + data);
        } else {
            System.out.println("All runs of continuous values for sensor "+ data + " from index[1 - 1200]");
            for(int z = 0; z < testList.size(); z++){
                System.out.println("\tStart["+testList.get(z).getStartIndex() + "] : End[" + testList.get(z).getEndIndex()+"]");
            }
        }


        System.exit(0);

    }
}