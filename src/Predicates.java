import java.util.function.Predicate;


public class Predicates
{

    public static Predicate<SwingSample> isAboveValue(column data, double value){
        return p -> p.getXYZ(data) > value;
    }

    public static Predicate<SwingSample> isBetweenValues(column data, double valueLo, double valueHi) {
        return p -> p.getXYZ(data) > valueLo && p.getXYZ(data) < valueHi;
    }

}
