import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NewPostServiceParcelManagement {

    public static void main(String[] args) {
        System.out.println("--- RUNNING TESTS ---\n");

        testConstructor();
        testGetBoxById();
        testGetBoxesByRecipient();
        testGetDescSortedBoxesByWeight();
        testGetAscSortedBoxesByCost();

        System.out.println("\n--- ALL TESTS PASSED SUCCESSFULLY ---");
    }

    // --- TEST METHODS ---

    private static void testConstructor() {
        // Valid data
        List<Box> emptyList = new ArrayList<>();
        new NewPostOfficeManagementImpl(emptyList);

        List<Box> boxes = createSampleBoxes();
        new NewPostOfficeManagementImpl(boxes);

        // Exception handling test (null parameter or null element)
        try {
            new NewPostOfficeManagementImpl(null);
            throw new AssertionError("Should have thrown NullPointerException for null collection!");
        } catch (NullPointerException e) {
            // Expected behavior
        }

        List<Box> failBoxes = Arrays.asList(boxes.get(0), null, boxes.get(1));
        try {
            new NewPostOfficeManagementImpl(failBoxes);
            throw new AssertionError("Should have thrown NullPointerException for null element!");
        } catch (NullPointerException e) {
            // Expected behavior
        }

        System.out.println("[OK] testConstructor");
    }

    private static void testGetBoxById() {
        IntSequence.reset();
        List<Box> boxes = createSampleBoxes();
        NewPostOfficeManagement office = new NewPostOfficeManagementImpl(boxes);

        for (Box b : boxes) {
            Optional<Box> found = office.getBoxById(b.getId());
            if (!found.isPresent() || !found.get().equals(b)) {
                throw new AssertionError("Box not found by ID: " + b.getId());
            }
        }

        if (office.getBoxById(0).isPresent() || office.getBoxById(99).isPresent()) {
            throw new AssertionError("Should return Optional.empty() for non-existent ID!");
        }

        System.out.println("[OK] testGetBoxById");
    }

    private static void testGetBoxesByRecipient() {
        IntSequence.reset();
        List<Box> boxes = createSampleBoxes();
        NewPostOfficeManagement office = new NewPostOfficeManagementImpl(boxes);

        try {
            office.getBoxesByRecipient(null);
            throw new AssertionError("Should have thrown NullPointerException for null recipient!");
        } catch (NullPointerException e) {
            // Expected behavior
        }

        List<Box> recipient1Boxes = office.getBoxesByRecipient("recipient_1");
        if (recipient1Boxes.size() != 1 || !recipient1Boxes.get(0).getRecipient().equals("recipient_1")) {
            throw new AssertionError("Error filtering for 'recipient_1'!");
        }

        List<Box> recipient2Boxes = office.getBoxesByRecipient("recipient_2");
        if (recipient2Boxes.size() != 3) {
            throw new AssertionError("Error filtering for 'recipient_2', expected size: 3, got: " + recipient2Boxes.size());
        }

        System.out.println("[OK] testGetBoxesByRecipient");
    }

    private static void testGetDescSortedBoxesByWeight() {
        IntSequence.reset();
        List<Box> boxes = createSampleBoxes();
        NewPostOfficeManagement office = new NewPostOfficeManagementImpl(boxes);

        String result = office.getDescSortedBoxesByWeight();
        String[] lines = result.split("\n");

        if (lines.length != 5) {
            throw new AssertionError("Incorrect number of returned lines!");
        }

        // Verification: descending order by weight
        if (!lines[0].contains("weight=21.2") || !lines[4].contains("weight=3.5")) {
            throw new AssertionError("Descending sort by weight is incorrect!");
        }

        System.out.println("[OK] testGetDescSortedBoxesByWeight");
    }

    private static void testGetAscSortedBoxesByCost() {
        IntSequence.reset();
        List<Box> boxes = createSampleBoxes();
        NewPostOfficeManagement office = new NewPostOfficeManagementImpl(boxes);

        String result = office.getAscSortedBoxesByCost();
        String[] lines = result.split("\n");

        if (lines.length != 5) {
            throw new AssertionError("Incorrect number of returned lines!");
        }

        // Verification: ascending order by cost
        if (!lines[0].contains("cost=39.72") || !lines[4].contains("cost=246.31")) {
            throw new AssertionError("Ascending sort by cost is incorrect!");
        }

        System.out.println("[OK] testGetAscSortedBoxesByCost");
    }

    private static List<Box> createSampleBoxes() {
        IntSequence.reset();
        return Arrays.asList(
                new Box("sender_1", "recipient_2", 3.5, 13.7, new BigDecimal("39.72"), "city_0", 9),
                new Box("sender_0", "recipient_2", 4.7, 9.56, new BigDecimal("67.38"), "city_1", 5),
                new Box("sender_4", "recipient_4", 15.9, 8.24, new BigDecimal("192.38"), "city_2", 12),
                new Box("sender_0", "recipient_2", 21.2, 11.92, new BigDecimal("234.18"), "city_3", 20),
                new Box("sender_1", "recipient_1", 19.6, 11.19, new BigDecimal("246.31"), "city_4", 13)
        );
    }
}

// --- BUSINESS LOGIC AND MODEL CLASSES ---

interface NewPostOfficeManagement {
    Optional<Box> getBoxById(int id);
    String getDescSortedBoxesByWeight();
    String getAscSortedBoxesByCost();
    List<Box> getBoxesByRecipient(String recipient);
}

class NewPostOfficeManagementImpl implements NewPostOfficeManagement {
    private final List<Box> parcels;
    private final Comparator<Box> idComparator;
    private final Comparator<Box> weightComparator;
    private final Comparator<Box> costComparator;
    private final Comparator<Box> recipientComparator;

    public NewPostOfficeManagementImpl(Collection<Box> boxes) {
        Objects.requireNonNull(boxes);

        List<Box> temporary = new ArrayList<>();
        for (Box box : boxes) {
            temporary.add(Objects.requireNonNull(box));
        }

        parcels = temporary;

        idComparator = new Comparator<Box>() {
            @Override
            public int compare(Box box1, Box box2) {
                return Integer.compare(box1.getId(), box2.getId());
            }
        };

        weightComparator = new Comparator<Box>() {
            @Override
            public int compare(Box box1, Box box2) {
                return Double.compare(box2.getWeight(), box1.getWeight());
            }
        };

        costComparator = new Comparator<Box>() {
            @Override
            public int compare(Box box1, Box box2) {
                return box1.getCost().compareTo(box2.getCost());
            }
        };

        recipientComparator = new Comparator<Box>() {
            @Override
            public int compare(Box box1, Box box2) {
                return box1.getRecipient().compareTo(box2.getRecipient());
            }
        };
    }

    @Override
    public Optional<Box> getBoxById(int id) {
        if (parcels.isEmpty()) {
            return Optional.empty();
        }

        parcels.sort(idComparator);

        Box searchBox;
        try {
            searchBox = parcels.get(0).clone();
        } catch (CloneNotSupportedException e) {
            return Optional.empty();
        }

        searchBox.setId(id);

        int index = Collections.binarySearch(parcels, searchBox, idComparator);

        if (index >= 0) {
            return Optional.of(parcels.get(index));
        }

        return Optional.empty();
    }

    @Override
    public String getDescSortedBoxesByWeight() {
        List<Box> sorted = new ArrayList<>(parcels);
        sorted.sort(weightComparator);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sorted.size(); i++) {
            result.append(sorted.get(i));
            if (i < sorted.size() - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    @Override
    public String getAscSortedBoxesByCost() {
        List<Box> sorted = new ArrayList<>(parcels);
        sorted.sort(costComparator);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sorted.size(); i++) {
            result.append(sorted.get(i));
            if (i < sorted.size() - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    @Override
    public List<Box> getBoxesByRecipient(String recipient) {
        Objects.requireNonNull(recipient);

        parcels.sort(recipientComparator);

        Box searchBox;
        try {
            searchBox = parcels.get(0).clone();
        } catch (CloneNotSupportedException e) {
            return new ArrayList<>();
        }

        searchBox.setRecipient(recipient);

        int index = Collections.binarySearch(
                parcels,
                searchBox,
                recipientComparator
        );

        if (index < 0) {
            return new ArrayList<>();
        }

        int left = index;
        while (left > 0 && parcels.get(left - 1).getRecipient().equals(recipient)) {
            left--;
        }

        int right = index;
        while (right < parcels.size() - 1 && parcels.get(right + 1).getRecipient().equals(recipient)) {
            right++;
        }

        List<Box> result = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            result.add(parcels.get(i));
        }

        return result;
    }
}

class IntSequence {
    static AtomicInteger base = new AtomicInteger();

    static synchronized void reset() {
        base = new AtomicInteger();
    }

    static int next() {
        return base.incrementAndGet();
    }
}

class Box implements Cloneable {
    private int id;
    private String sender;
    private String recipient;
    private double weight;
    private double volume;
    private BigDecimal cost;
    private String city;
    private int office;

    public Box(String sender, String recipient, double weight, double volume, BigDecimal cost, String city, int office) {
        this.id = IntSequence.next();
        this.sender = sender;
        this.recipient = recipient;
        this.weight = weight;
        this.volume = volume;
        this.cost = cost;
        this.city = city;
        this.office = office;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getOffice() {
        return office;
    }

    public void setOffice(int office) {
        this.office = office;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return id == box.id && Double.compare(box.weight, weight) == 0 && Double.compare(box.volume, volume) == 0
        			 && office == box.office && sender.equals(box.sender) && recipient.equals(box.recipient) && cost.equals(box.cost) && city.equals(box.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, recipient, weight, volume, cost, city, office);
    }

    @Override
    public String toString() {
        return "{" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", weight=" + weight +
                ", volume=" + volume +
                ", cost=" + cost +
                ", city='" + city + '\'' +
                ", office=" + office +
                '}';
    }

    @Override
    public Box clone() throws CloneNotSupportedException {
        return (Box) super.clone();
    }
}
/*
This Java program implements a parcel management system for a post office, complete with embedded unit tests.
**Main Components and Structure:**
* **`Box` Model Class:** Represents parcels with properties such as unique ID, sender, recipient, weight, volume, cost, city, and office branch. 
     Unique IDs are generated automatically upon object creation.
     It implements the `Cloneable` interface and overrides the `toString()`, `equals()`, and `hashCode()` methods.
* **`IntSequence` Class:** A thread-safe utility class built on `AtomicInteger` that guarantees unique ID generation for each parcel.
* **`NewPostOfficeManagement` Interface and Implementation (`Impl`):** Contains the core business logic:
* **Search by ID (`getBoxById`):** Uses binary search (`Collections.binarySearch`) to locate elements within an ID-sorted list.
* **Search by Recipient (`getBoxesByRecipient`):** Sorts the list by recipient name, finds an initial match via binary search, 
    and expands outward (left and right) to collect all matching parcels.
* **Formatted String Exports:**
* `getDescSortedBoxesByWeight()`: Returns the list of parcels formatted as a string, sorted by weight in descending order and separated by newlines.
* `getAscSortedBoxesByCost()`: Returns the list of parcels formatted as a string, sorted by cost in ascending order.
* **`NewPostServiceParcelManagement` (Main Class):** Acts as a built-in test suite verifying constructor validations (e.g., handling `null` references), 
     search operations, sorting correctness, and exception handling.
**Algorithmic Details:**
  Rather than relying on basic linear iteration or the Java Stream API, the implementation uses **binary search** for queries. 
  To achieve this, it sorts the internal list based on the relevant search property (ID or recipient) prior 
  to search execution and utilizes the object's `clone()` method to instantiate temporary search templates.
*/

