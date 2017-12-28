import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class SamplePredicates
{

    public static Predicate<SwingSample> isAboveValue(column data, double value){
        return p -> p.getXYZ(data) > value;
    }

    public static Predicate<SwingSample> isBetweenValues(column data, double valueLo, double valueHi) {
        return p -> p.getXYZ(data) > valueLo && p.getXYZ(data) < valueHi;
    }

    public static ArrayList<IndexPair> filterData(List<SwingSample> sampleList, Predicate<SwingSample> predicate,
                                           int runLength) {

        int count, found, size;
        ArrayList<IndexPair> indexList = new ArrayList<IndexPair>();
        ArrayList<SwingSample> filterList = sampleList
                .stream()
                .filter(predicate)
                .collect(Collectors.toCollection(ArrayList::new));
        size = filterList.size() - 1;
        if (size <= 0){
            return null;
        }

        count = 1;
        found = filterList.get(0).getIndex();
        for(int i = 0; i < size; i++) {
            if(filterList.get(i+1).getIndex() - filterList.get(i).getIndex() == 1) {
                count++;
                if(i+1 == size && count >= runLength){
                    indexList.add(new IndexPair(found, filterList.get(i+1).getIndex()));
                };
            } else {
                if(count >= runLength){
                    indexList.add(new IndexPair(found, filterList.get(i).getIndex()));
                }
                count = 1;
                found = filterList.get(i + 1).getIndex();
            }
        }

        if(indexList.size() == 0){
            return null;
        }

        return indexList;
    }


}
