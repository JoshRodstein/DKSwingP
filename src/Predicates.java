import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;


public class Predicates
{

    public static Predicate<SwingSample> isAboveValue(column data, double value){
        System.out.println(data +" "+value);
        return p -> p.getXYZ(data) > value;
    }

    public static Predicate<SwingSample> isBetweenValues(column data, double valueLo, double valueHi) {
        return p -> p.getXYZ(data) > valueLo && p.getXYZ(data) < valueHi;
    }

    public static ArrayList<IndexPair> filterData (List<SwingSample> sampleList, Predicate<SwingSample> predicate,
                                                     int runLength) {
        // returns all occurances (not continuous runs)
        List<SwingSample> filterList = new ArrayList<>();
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
                count++;
            } else {
                if(count >= runLength){
                    indexList.add(new  IndexPair(found, filterList.get(i).getIndex()) );
                }
                found = filterList.get(i + 1).getIndex();
            }

        }

        if(indexList.size() == 0){
            return null;
        }

        return indexList;

    }
}
