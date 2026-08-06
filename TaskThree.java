import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class TaskThree {
    public static void main(String[] args) {
        Set<String> strings1 = new HashSet<>();
        strings1.add("C33");
        strings1.add("A11");
        strings1.add("B22");

        System.out.print("HashSet: ");
        System.out.println(strings1);

        Set<String> strings2 = new LinkedHashSet<>();
        strings2.add("C33");
        strings2.add("A11");
        strings2.add("B22");

        System.out.print("LinkedHashSet: ");
        System.out.println(strings2);
    }
}
//The placement of elements in the HashSet collection depends on the hashing method.
//Therefore, the sequence of displaying the elements of the collection is unpredictable.
//For the LinkedHashSet collection, the output order corresponds to the order in which it was added.
//Thus, only the fourth statement is true.
