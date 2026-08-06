import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
//The HashSet class implements the Set interface.
//The HashSet class does not implement the SortedSet interface.
public class TaskTwo {
    public static void main(String[] args) {
        Object object = new HashSet<String>();

        System.out.print(object instanceof Set);
        System.out.print(" ");
        System.out.print(object instanceof SortedSet);
    }
}
