import java.util.Set;
import java.util.TreeSet;
//The TreeSet collection is a class that supports sorting objects in natural order.
public class TaskZero {
    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<>();
        set.add(2);
        set.add(1);
        System.out.println(set);
    }
}
