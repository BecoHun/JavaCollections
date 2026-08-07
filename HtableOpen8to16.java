import java.util.Arrays;

// --- 1. INTERFACE ---
interface HashtableOpen8to16 {
    void insert(int key, Object value);
    Object search(int key);
    void remove(int key);
    int size();
    int[] keys();

    static HashtableOpen8to16 getInstance() {
        return new HashtableOpen8to16Impl();
    }
}

// --- 2. IMPLEMENTATION ---
class HashtableOpen8to16Impl implements HashtableOpen8to16 {

    private static final Object DELETED = new Object();

    private int capacity = 8;
    private int size = 0;

    private int[] keys = new int[capacity];
    private Object[] values = new Object[capacity];

    @Override
    public void insert(int key, Object value) {
        int index = findSlotOrKey(key);

        if (index != -1 && values[index] != null && values[index] != DELETED) {
            // Key already exists, update value
            values[index] = value;
            return;
        }

        // Check if capacity doubling is needed before inserting new element
        if (size == capacity) {
            if (capacity >= 16) {
                throw new IllegalStateException("Hashtable full (max capacity 16 reached)");
            }
            resize(capacity * 2);
            index = findSlotOrKey(key);
        }

        keys[index] = key;
        values[index] = value;
        size++;
    }

    @Override
    public Object search(int key) {
        int index = findKey(key);
        if (index == -1) {
            return null;
        }
        return values[index];
    }

    @Override
    public void remove(int key) {
        int index = findKey(key);
        if (index == -1) {
            return; // Key not found
        }

        // Mark as deleted
        values[index] = DELETED;
        size--;

        // Check for capacity halving (when size to capacity ratio reaches 1:4 and size > 0)
        if (size > 0 && size * 4 <= capacity) {
            int newCapacity = capacity / 2;
            if (newCapacity >= 2) {
                resize(newCapacity);
            }
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int[] keys() {
        int[] result = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            if (values[i] != null && values[i] != DELETED) {
                result[i] = keys[i];
            } else {
                result[i] = 0;
            }
        }
        return result;
    }

    // Helper method to find the index of an existing key using linear probing
    private int findKey(int key) {
        int hash = Math.abs(key) % capacity;
        int startIndex = hash;

        for (int i = 0; i < capacity; i++) {
            int curr = (startIndex + i) % capacity;

            if (values[curr] == null) {
                return -1; // Empty slot found, key does not exist
            }

            if (values[curr] != DELETED && keys[curr] == key) {
                return curr;
            }
        }

        return -1;
    }

    // Helper method for insertion: finds existing key or first available (deleted/empty) slot
    private int findSlotOrKey(int key) {
        int hash = Math.abs(key) % capacity;
        int startIndex = hash;
        int firstDeletedIndex = -1;

        for (int i = 0; i < capacity; i++) {
            int curr = (startIndex + i) % capacity;

            if (values[curr] == null) {
                return firstDeletedIndex != -1 ? firstDeletedIndex : curr;
            }

            if (values[curr] == DELETED) {
                if (firstDeletedIndex == -1) {
                    firstDeletedIndex = curr;
                }
            } else if (keys[curr] == key) {
                return curr;
            }
        }

        return firstDeletedIndex;
    }

    // Helper method to rehash all existing elements on table resize
    private void resize(int newCapacity) {
        int[] oldKeys = keys;
        Object[] oldValues = values;

        capacity = newCapacity;
        keys = new int[capacity];
        values = new Object[capacity];
        size = 0;

        for (int i = 0; i < oldKeys.length; i++) {
            if (oldValues[i] != null && oldValues[i] != DELETED) {
                insert(oldKeys[i], oldValues[i]);
            }
        }
    }
}

// --- 3. (PUBLIC CLASS) ---
public class HtableOpen8to16 {

    public static void main(String[] args) {
        System.out.println("--- 1. Running testSearch ---");
        runSearchTest();

        System.out.println("\n--- 2. Running testOverflow ---");
        runOverflowTest();

        System.out.println("\n--- 3. Running simple keys array test ---");
        runSimpleKeysTest();

        System.out.println("\nAll tests executed successfully!");
    }

    private static void runSearchTest() {
        HashtableOpen8to16 hashtable = HashtableOpen8to16.getInstance();

        hashtable.insert(10, 10);
        hashtable.insert(18, 18);
        hashtable.insert(34, 34);

        assertEquals(10, hashtable.search(10), "Search 10");
        assertEquals(18, hashtable.search(18), "Search 18");
        assertEquals(34, hashtable.search(34), "Search 34");

        hashtable.remove(18);

        assertEquals(10, hashtable.search(10), "Search 10 post remove 18");
        assertEquals(null, hashtable.search(18), "Search 18 post remove 18");
        assertEquals(34, hashtable.search(34), "Search 34 post remove 18");

        hashtable.remove(10);

        assertEquals(null, hashtable.search(10), "Search 10 post remove 10");
        assertEquals(null, hashtable.search(18), "Search 18 post remove 10");
        assertEquals(34, hashtable.search(34), "Search 34 post remove 10");

        hashtable.insert(10, 10);
        hashtable.insert(18, 18);
        hashtable.insert(34, 34);
        hashtable.insert(42, 42);
        hashtable.insert(50, 50);
        hashtable.insert(58, 58);
        hashtable.insert(66, 66);
        hashtable.insert(74, 74);

        hashtable.remove(2);
        assertEquals(10, hashtable.search(10), "Search 10 post remove non-existent 2");

        hashtable.remove(10);

        assertEquals(null, hashtable.search(10), "Search 10 post remove 10");
        assertEquals(18, hashtable.search(18), "Search 18");
        assertEquals(34, hashtable.search(34), "Search 34");
        assertEquals(42, hashtable.search(42), "Search 42");
        assertEquals(50, hashtable.search(50), "Search 50");
        assertEquals(58, hashtable.search(58), "Search 58");
        assertEquals(66, hashtable.search(66), "Search 66");
        assertEquals(74, hashtable.search(74), "Search 74");

        hashtable.remove(50);

        assertEquals(null, hashtable.search(10), "Search 10");
        assertEquals(18, hashtable.search(18), "Search 18");
        assertEquals(34, hashtable.search(34), "Search 34");
        assertEquals(42, hashtable.search(42), "Search 42");
        assertEquals(null, hashtable.search(50), "Search 50 post remove 50");
        assertEquals(58, hashtable.search(58), "Search 58");
        assertEquals(66, hashtable.search(66), "Search 66");
        assertEquals(74, hashtable.search(74), "Search 74");

        System.out.println("testSearch: PASSED");
    }

    private static void runOverflowTest() {
        HashtableOpen8to16 hashtable = HashtableOpen8to16.getInstance();

        // Insert 16 elements (capacity expands from 8 to 16)
        for (int i = 0; i < 32; i += 2) {
            hashtable.insert(i, i);
        }

        // Inserting the 17th element must throw IllegalStateException
        boolean exceptionThrown = false;
        try {
            hashtable.insert(42, 42);
        } catch (IllegalStateException e) {
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            throw new AssertionError("Error: Allowed insertion beyond maximum capacity of 16!");
        }

        // Updating an existing key must work even when the table is full
        hashtable.insert(16, 32);
        assertEquals(32, hashtable.search(16), "Key 16 value updated to 32");

        System.out.println("testOverflow: PASSED");
    }

    private static void runSimpleKeysTest() {
        HashtableOpen8to16 hashtable = HashtableOpen8to16.getInstance();
        hashtable.insert(1, 100);
        hashtable.insert(2, 200);

        System.out.println("Keys array state (size: " + hashtable.size() + "): " + Arrays.toString(hashtable.keys()));
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
            throw new AssertionError("Assertion failed [" + message + "]: Expected = " + expected + ", Actual = " + actual);
        }
    }
}
/*
Overview
This Java application implements and tests a custom Open Addressing Hash Table with Linear Probing collision resolution (HashtableOpen8to16Impl).
The dynamic data structure grows and shrinks under strict capacity constraints:
Initial Capacity: 8 buckets
Maximum Capacity: 16 buckets
Minimum Capacity: 2 buckets

Component Breakdown
HashtableOpen8to16 (Interface)
Defines core contracts: insert, search, remove, size, and keys.
Contains a static factory method getInstance() returning a new implementation instance.

HashtableOpen8to16Impl (Class)
Linear Probing: Calculates index using Math.abs(key) % capacity. On collision, it steps linearly (index + i) % capacity to find an available slot.
Tombstones (DELETED): When removing an element, its slot is marked with a sentinel object (DELETED) instead of null so linear search chains remain unbroken.

Resizing Rules:
Expansion: When size == capacity during an insertion, capacity doubles (up to 16). Inserting a 17th element triggers an IllegalStateException.
Contraction: When removing an element causes the load factor ratio to reach 1:4 (size * 4 <= capacity) and size > 0, capacity halves (down to 2).

HtableOpen8to16 (Test Runner)
Executes three distinct test suites to verify functionality without external frameworks like JUnit:
Execution Flow in HtableOpen8to16
1. runSearchTest()
Insertions & Probing: Inserts keys 10, 18, and 34. Because they all hash to index 2 (mod 8), linear probing places them in consecutive slots (2, 3, and 4).
Deletion & Tombstones: Removes 18 and then 10. Verifies that searching for 34 still works even though items earlier in its probe chain were deleted.
Multiple Elements: Re-populates the table with keys (10, 18, 34, 42, 50, 58, 66, 74). Performs searches, removals, and confirms value lookups return correct elements or null.
2. runOverflowTest()
Capacity Expansion: Inserts 16 elements (0, 2, 4, ... 30), triggering dynamic expansion from capacity 8 to 16.
Boundary Guard: Attempts to insert a 17th element (42) and asserts that an IllegalStateException is thrown.
In-Place Updates: Verifies that updating an existing key (16) is permitted even when the table is at full capacity (16 elements).
3. runSimpleKeysTest()
Inserts two elements (1, 2) and prints the internal state of the keys() array (showing active keys padded with 0 for empty/deleted slots).
*/

