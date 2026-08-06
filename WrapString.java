import java.util.*;
class WrapString {
    private String string;

    public WrapString(String string) {
        this.string = string;
    }
}
// The class WrapString has not defined the methods equals and hashcode.
// Therefore, both objects are added to the set.
// The class String has defined these methods, so only one object is added to the set.

class Quest3 {
    public static void main(String[] args) {
        Set<Object> set = new HashSet<>();
        WrapString s1 = new WrapString("hello");
        WrapString s2 = new WrapString("hello");
        String str1 = new String("hello");
        String str2 = "hel" + "lo";
        set.add(s1);
        set.add(s2);
        set.add(str1);
        set.add(str2);
        System.out.print(set.size());
    }
}
