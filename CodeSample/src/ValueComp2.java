import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class ValueComp2 implements Comparator<String> {

    Map<String, Double> base;
    public ValueComp2(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
