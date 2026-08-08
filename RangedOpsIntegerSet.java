import java.util.AbstractSet;
import java.util.Iterator;
import java.util.TreeSet;

class RangedOpsIntegerSet extends AbstractSet<Integer> {
    private final TreeSet<Integer> elements = new TreeSet<>();
    public boolean add(int fromInclusive, int toExclusive) {
        boolean changed = false;
        for (int i = fromInclusive; i < toExclusive; i++) {
            if (elements.add(i)) {
                changed = true;
            }
        }
        return changed;
    }
    public boolean remove(int fromInclusive, int toExclusive) {
        boolean changed = false;
        for (int i = fromInclusive; i < toExclusive; i++) {
            if (elements.remove(i)) {
                changed = true;
            }
        }
        return changed;
    }
    @Override
    public boolean add(final Integer integer) {
        return elements.add(integer);
    }
    @Override
    public boolean remove(final Object o) {
        return elements.remove(o);
    }
    @Override
    public Iterator<Integer> iterator() {
        return elements.iterator();
    }
    @Override
    public int size() {
        return elements.size();
    }
/*
RangedOpsIntegerSet extends AbstractSet<Integer> and uses a TreeSet<Integer> internally to store the elements.
TreeSet automatically keeps the values sorted and prevents duplicates.
The add(int fromInclusive, int toExclusive) and remove(int fromInclusive,
int toExclusive) methods process the range [fromInclusive, toExclusive),
meaning the lower bound is included while the upper bound is excluded.
They return true only when the set is actually modified.
The single-element add() and remove() methods delegate the operation to the internal TreeSet.
The iterator() returns the TreeSet iterator, so elements are always returned in ascending order, while size() returns the current number of elements.
*/    
	public static void main(String[] args) {

        System.out.println("=== RangedOpsIntegerSet test ===");

        RangedOpsIntegerSet set = new RangedOpsIntegerSet();

        // Test initial state
        System.out.println("\n1. Initial state");
        System.out.println("Size: " + set.size());

        Iterator<Integer> iterator = set.iterator();
        System.out.println("Has next: " + iterator.hasNext());

        // Test ordered adding
        System.out.println("\n2. Add ordered");
        set.add(0);
        set.add(1);
        set.add(2);

        System.out.println("Size: " + set.size());
        printSet(set);

        // Test unordered adding
        System.out.println("\n3. Add unordered");

        set = new RangedOpsIntegerSet();

        set.add(12);
        set.add(5);
        set.add(0);
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(8);

        System.out.println("Size: " + set.size());
        printSet(set);

        // Test duplicates
        System.out.println("\n4. Add duplicates");

        set = new RangedOpsIntegerSet();

        set.add(12);
        set.add(5);
        set.add(0);
        set.add(12);
        set.add(2);
        set.add(8);
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(8);

        System.out.println("Size: " + set.size());
        printSet(set);

        // Test range operations
        System.out.println("\n5. Range operations");

        set = new RangedOpsIntegerSet();

        System.out.println("add(-5, 5): " + set.add(-5, 5));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nadd(9, 10): " + set.add(9, 10));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nadd(-100, -97): " + set.add(-100, -97));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nadd(0, 3): " + set.add(0, 3));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nadd(2, 5): " + set.add(2, 5));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nadd(2, 7): " + set.add(2, 7));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nremove(20, 25): " + set.remove(20, 25));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nremove(0, 3): " + set.remove(0, 3));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nremove(0, 3): " + set.remove(0, 3));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nremove(5, 10): " + set.remove(5, 10));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nremove(-99, 99): " + set.remove(-99, 99));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\nremove(-100, 100): " + set.remove(-100, 100));
        System.out.println("Size: " + set.size());
        printSet(set);

        System.out.println("\n=== Test finished ===");
	}

    private static void printSet(RangedOpsIntegerSet set) {
        Iterator<Integer> iterator = set.iterator();

        System.out.print("Elements: ");

        while (iterator.hasNext()) {
            System.out.print(iterator.next());

            if (iterator.hasNext()) {
                System.out.print(" ");
            }
        }

        System.out.println();
    }
}

