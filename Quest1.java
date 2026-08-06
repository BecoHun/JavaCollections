import java.util.EnumSet; 
enum Counter {UNO, DOS, TRES, CUATRO, CINCO, SEIS, SIETE} 
public class Quest1 { 
    public static void main(String[] args) { 
        EnumSet<Counter> set1 = EnumSet.range(Counter.TRES, Counter.CINCO); 
        EnumSet<Counter>  set2 = EnumSet.complementOf(set1); 
        System.out.println(set2); 
    } 
}
//The range() method creates an enum set containing all of the elements in the specified range, and а complementOf() method creates an enum set containing all the elements not in the specified set.
