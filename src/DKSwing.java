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
        aSwing = null;
        try {
            File swingFile = new File(args[0]);
            aSwing = new SwingTable(swingFile);
        } catch(FileNotFoundException fn) {
            System.err.println("File not found");
        }

        int i = aSwing.searchContinuityAboveValue(column.WZ, 1,1200, 1, 20);
        System.out.println("From index [1 - 1200] for sensor 'wz', Above Value 1 of length >= " + 20 + "\n\t: Index["+i+"]");

        int x = aSwing.backSearchContinuityWithinRange(column.AX, 860,1, 1,
                16, 20 );
        System.out.println("From index [860 - 1] for sensor 'ax', Above Value 1 and below value 16 of length >= 20 \n\t: Index["+x+"]");

        int y = aSwing.searchContinuityAboveValueTwoSignals(column.AX, column.WX, 1, 1200, 0,
                0, 5);
        System.out.println("From index [1 - 1200] for sensor 'ax' above value 0 and for sensor wx above value 0, of length >= 5 \n\t: Index["+y+"]");

        ArrayList<IndexPair> testList = aSwing.searchMultiContinuityWithinRange(column.AY, 1, 1200,
                2, 3, 3);

        System.out.println("All runs of continuous values between 2-3 of length >= 3 from index[1 - 1200]");
        for(int z = 0; z < testList.size(); z++){
            System.out.println("\tStart["+testList.get(z).getStartIndex() + "] : End[" + testList.get(z).getEndIndex()+"]");
        }

        System.exit(0);

    }
}