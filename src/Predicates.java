import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;


public class Predicates
{

    public static Predicate<SwingSample> isAboveValue(column data, double value){
        return p -> p.getXYZ(data) > value;
    }

    public static Predicate<SwingSample> isBetweenValues(column data, double valueLo, double valueHi) {
        return p -> p.getXYZ(data) > valueLo && p.getXYZ(data) < valueHi;
    }

    public static ArrayList<IndexPair> filterData (List<SwingSample> sampleList, Predicate<SwingSample> predicate,
                                                     int runLength) {
        // returns all occurances (not continuous runs)
        ArrayList<SwingSample> filterList = new ArrayList<SwingSample>(sampleList.stream().filter(predicate)
                                                                    .collect(Collectors.<SwingSample>toList()));

        int count = 1, found = filterList.get(0).getIndex();
        int size = filterList.size() - 1;
        ArrayList<IndexPair> indexList = new ArrayList<IndexPair>();

        for(int i = 0; i < size; i++) {

            if(filterList.get(i+1).getIndex() - filterList.get(i).getIndex() == 1) {
                count++;
            } else {
                if(count >= runLength){
                    indexList.add(new  IndexPair(found, filterList.get(i).getIndex()) );
                }
                found = filterList.get(i + 1).getIndex();
            }

        }

        return indexList;

    }
}
