import java.util.Collection;
import java.util.TreeSet;

//The collection is initialized with the TreeSet‹E› class, which supports sorting in natural order.
//This class contains only unique elements.
//Note that in the code table, uppercase letters come before lowercase.
public class TaskOne {
    public static void main(String[] args) {
        Collection<String> collection = new TreeSet<>();

        collection.add("java");
        collection.add("scala");
        collection.add("kotlin");
        collection.add("sca" + "la");
        collection.add("Java");

        for (Object o : collection) {
            System.out.print(o + " ");
        }
    }
}
