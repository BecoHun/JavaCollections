import java.util.*;
//A set can be initialized by referring to a collection and contains elements in sorted order.
public class Quest4 {
    public static Collection<?> get() {
        Collection<String> sorted = new TreeSet<>();
        sorted.add("B");
        sorted.add("C");
        sorted.add("A");
        return sorted;
    }
    public static void main(String[] args) {
        for (Object obj : get()) {
            System.out.print(obj + " ");
        }
    }
}
