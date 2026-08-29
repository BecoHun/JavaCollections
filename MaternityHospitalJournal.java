import java.util.*;

public class MaternityHospitalJournal {

    // --- ENUM & MODELS ---

    public enum WeekDay {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    public interface BirthJournalManagement {
        boolean addEntryOfBaby(WeekDay day, Baby baby);
        void commit();
        int amountBabies();
        List<Baby> findBabyWithHighestWeight(String gender);
        List<Baby> findBabyWithSmallestHeight(String gender);
        Set<Baby> findBabiesByBirthTime(String from, String to);
    }

    public static class Baby {
        private final String name;
        private final double weight;
        private final int height;
        private final String gender;
        private final String time;

        public Baby(String name, double weight, int height, String gender, String time) {
            this.name = name;
            this.weight = weight;
            this.height = height;
            this.gender = gender;
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public double getWeight() {
            return weight;
        }

        public int getHeight() {
            return height;
        }

        public String getGender() {
            return gender;
        }

        public String getTime() {
            return time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Baby baby = (Baby) o;
            return Double.compare(baby.weight, weight) == 0 &&
                    height == baby.height &&
                    Objects.equals(name, baby.name) &&
                    Objects.equals(gender, baby.gender) &&
                    Objects.equals(time, baby.time);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, weight, height, gender, time);
        }

        @Override
        public String toString() {
            return "Baby{" +
                    "name='" + name + '\'' +
                    ", weight=" + weight +
                    ", height=" + height +
                    ", gender='" + gender + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }

    // --- IMPLEMENTATION ---

    public static class BirthJournalManagementImpl implements BirthJournalManagement {
        private final Map<WeekDay, List<Baby>> journal;
        private boolean committed;

        public BirthJournalManagementImpl() {
            journal = new HashMap<>();

            for (WeekDay day : WeekDay.values()) {
                journal.put(day, new ArrayList<>());
            }

            committed = false;
        }

        @Override
        public boolean addEntryOfBaby(WeekDay day, Baby baby) {
            if (committed) {
                return false;
            }

            if (day == null || baby == null) {
                return false;
            }

            journal.get(day).add(baby);
            return true;
        }

        @Override
        public void commit() {
            if (committed) {
                return;
            }

            for (WeekDay day : WeekDay.values()) {
                journal.put(day, new ArrayList<>(journal.get(day)));
            }

            committed = true;
        }

        @Override
        public int amountBabies() {
            int amount = 0;

            for (List<Baby> babies : journal.values()) {
                amount += babies.size();
            }

            return amount;
        }

        @Override
        public List<Baby> findBabyWithHighestWeight(String gender) {
            List<Baby> result = new ArrayList<>();
            double highestWeight = Double.NEGATIVE_INFINITY;

            for (List<Baby> babies : journal.values()) {
                for (Baby baby : babies) {
                    if (!baby.getGender().equals(gender)) {
                        continue;
                    }

                    if (baby.getWeight() > highestWeight) {
                        highestWeight = baby.getWeight();
                        result.clear();
                        result.add(baby);
                    } else if (Double.compare(baby.getWeight(), highestWeight) == 0) {
                        result.add(baby);
                    }
                }
            }

            result.sort(new Comparator<Baby>() {
                @Override
                public int compare(Baby baby1, Baby baby2) {
                    return baby1.getName().compareTo(baby2.getName());
                }
            });

            return Collections.unmodifiableList(result);
        }

        @Override
        public List<Baby> findBabyWithSmallestHeight(String gender) {
            List<Baby> result = new ArrayList<>();
            int smallestHeight = Integer.MAX_VALUE;

            for (List<Baby> babies : journal.values()) {
                for (Baby baby : babies) {
                    if (!baby.getGender().equals(gender)) {
                        continue;
                    }

                    if (baby.getHeight() < smallestHeight) {
                        smallestHeight = baby.getHeight();
                        result.clear();
                        result.add(baby);
                    } else if (baby.getHeight() == smallestHeight) {
                        result.add(baby);
                    }
                }
            }

            result.sort(new Comparator<Baby>() {
                @Override
                public int compare(Baby baby1, Baby baby2) {
                    return Double.compare(
                            baby1.getWeight(),
                            baby2.getWeight()
                    );
                }
            });

            return Collections.unmodifiableList(result);
        }

        @Override
        public Set<Baby> findBabiesByBirthTime(String from, String to) {
            Set<Baby> result = new HashSet<>();

            for (List<Baby> babies : journal.values()) {
                for (Baby baby : babies) {
                    if (isTimeBetween(baby.getTime(), from, to)) {
                        result.add(baby);
                    }
                }
            }

            return result;
        }

        private boolean isTimeBetween(String time, String from, String to) {
            int babyTime = toMinutes(time);
            int fromTime = toMinutes(from);
            int toTime = toMinutes(to);

            if (fromTime <= toTime) {
                return babyTime >= fromTime && babyTime <= toTime;
            }

            return babyTime >= fromTime || babyTime <= toTime;
        }

        private int toMinutes(String time) {
            String[] parts = time.split(":");

            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            return hour * 60 + minute;
        }
    }

    // --- MAIN / TEST RUNNER ---

    public static void main(String[] args) {
        System.out.println("--- RUNNING TESTS ---");

        testAmountBabies();
        testAddEntryAndCommit();
        testFindBabyWithHighestWeight();
        testFindBabyWithSmallestHeight();
        testFindBabiesByBirthTime();

        System.out.println("\n--- ALL TESTS PASSED SUCCESSFULLY! ---");
    }

    private static void testAmountBabies() {
        BirthJournalManagement journal = new BirthJournalManagementImpl();
        assertEquals(0, journal.amountBabies(), "Initial baby count should be 0.");

        journal.addEntryOfBaby(WeekDay.MONDAY, new Baby("Anna", 3.2, 50, "female", "08:30"));
        journal.addEntryOfBaby(WeekDay.TUESDAY, new Baby("Bob", 3.5, 52, "male", "14:15"));

        assertEquals(2, journal.amountBabies(), "Baby count should be 2 after additions.");
        System.out.println("[PASS] testAmountBabies");
    }

    private static void testAddEntryAndCommit() {
        BirthJournalManagement journal = new BirthJournalManagementImpl();
        Baby baby = new Baby("Charlie", 4.0, 55, "male", "10:00");

        assertTrue(journal.addEntryOfBaby(WeekDay.WEDNESDAY, baby), "Adding a baby should succeed.");
        assertFalse(journal.addEntryOfBaby(null, baby), "Should return false when day is null.");

        journal.commit();

        assertFalse(journal.addEntryOfBaby(WeekDay.THURSDAY, baby), "Should not allow adding entries after commit.");
        System.out.println("[PASS] testAddEntryAndCommit");
    }

    private static void testFindBabyWithHighestWeight() {
        BirthJournalManagement journal = new BirthJournalManagementImpl();

        // Two female babies with the same highest weight (3.8 kg), testing alphabetical sorting by name (Alice vs Zoe)
        journal.addEntryOfBaby(WeekDay.MONDAY, new Baby("Zoe", 3.8, 51, "female", "06:00"));
        journal.addEntryOfBaby(WeekDay.TUESDAY, new Baby("Alice", 3.8, 49, "female", "12:00"));
        journal.addEntryOfBaby(WeekDay.WEDNESDAY, new Baby("Diana", 3.1, 48, "female", "15:00"));
        journal.addEntryOfBaby(WeekDay.THURSDAY, new Baby("Ethan", 4.5, 56, "male", "18:00"));

        List<Baby> highestFemale = journal.findBabyWithHighestWeight("female");
        assertEquals(2, highestFemale.size(), "There should be 2 female babies with the highest weight.");
        assertEquals("Alice", highestFemale.get(0).getName(), "Alice should be first alphabetically.");
        assertEquals("Zoe", highestFemale.get(1).getName(), "Zoe should be second alphabetically.");

        // Test unmodifiable list constraint
        try {
            highestFemale.add(new Baby("X", 1.0, 30, "female", "00:00"));
            fail("The returned list must be unmodifiable!");
        } catch (UnsupportedOperationException e) {
            // Expected exception
        }

        System.out.println("[PASS] testFindBabyWithHighestWeight");
    }

    private static void testFindBabyWithSmallestHeight() {
        BirthJournalManagement journal = new BirthJournalManagementImpl();

        // Two male babies with the same smallest height (48 cm), testing ascending weight sorting order
        journal.addEntryOfBaby(WeekDay.FRIDAY, new Baby("George", 3.9, 48, "male", "01:00"));
        journal.addEntryOfBaby(WeekDay.SATURDAY, new Baby("Henry", 3.2, 48, "male", "02:00"));
        journal.addEntryOfBaby(WeekDay.SUNDAY, new Baby("Ian", 3.5, 53, "male", "03:00"));

        List<Baby> smallestMale = journal.findBabyWithSmallestHeight("male");
        assertEquals(2, smallestMale.size(), "There should be 2 male babies with the smallest height.");
        assertEquals("Henry", smallestMale.get(0).getName(), "Lighter baby (Henry - 3.2kg) should come first.");
        assertEquals("George", smallestMale.get(1).getName(), "Heavier baby (George - 3.9kg) should come second.");

        System.out.println("[PASS] testFindBabyWithSmallestHeight");
    }

    private static void testFindBabiesByBirthTime() {
        BirthJournalManagement journal = new BirthJournalManagementImpl();

        Baby b1 = new Baby("B1", 3.0, 50, "male", "08:00");
        Baby b2 = new Baby("B2", 3.0, 50, "female", "12:30");
        Baby b3 = new Baby("B3", 3.0, 50, "male", "23:15");
        Baby b4 = new Baby("B4", 3.0, 50, "female", "02:45");

        journal.addEntryOfBaby(WeekDay.MONDAY, b1);
        journal.addEntryOfBaby(WeekDay.TUESDAY, b2);
        journal.addEntryOfBaby(WeekDay.WEDNESDAY, b3);
        journal.addEntryOfBaby(WeekDay.THURSDAY, b4);

        // Regular time interval (within same day: 07:00 - 13:00)
        Set<Baby> normalRange = journal.findBabiesByBirthTime("07:00", "13:00");
        assertEquals(2, normalRange.size(), "Should contain 2 babies within the interval.");
        assertTrue(normalRange.contains(b1), "Should contain b1.");
        assertTrue(normalRange.contains(b2), "Should contain b2.");

        // Overnight time interval (spanning across midnight: 22:00 - 05:00)
        Set<Baby> overnightRange = journal.findBabiesByBirthTime("22:00", "05:00");
        assertEquals(2, overnightRange.size(), "Should contain 2 babies within the overnight interval.");
        assertTrue(overnightRange.contains(b3), "Should contain b3.");
        assertTrue(overnightRange.contains(b4), "Should contain b4.");

        System.out.println("[PASS] testFindBabiesByBirthTime");
    }

    // --- TEST HELPER METHODS ---

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", but was: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }
}
/*
Overview of the Code
The provided Java code implements a Maternity Hospital Journal System (MaternityHospitalJournal). 
The purpose of the system is to manage newborn records grouped by the day of the week they were born, while providing various search, filtering, and reporting features.
Key Components & Structure
WeekDay (Enum):
Represents the days of the week (MONDAY through SUNDAY).
Baby (Model Class):
Stores information about a newborn in an immutable structure:
name (String)
weight (double, in kg)
height (int, in cm)
gender (String: "male" / "female")
time (String: birth time in 24-hour "HH:mm" format)
Properly overrides equals, hashCode, and toString.
BirthJournalManagement (Interface):
Defines the required operations for managing the journal.
BirthJournalManagementImpl (Implementation):
Data Storage: Stores entries in a Map<WeekDay, List<Baby>> indexed by weekday.
addEntryOfBaby: Adds a baby entry for a specific day. Handles null checks and rejects additions if the journal has already been committed (false).
commit: Finalizes and locks the journal. Once committed, no further entries can be added.
amountBabies: Returns the total count of babies born throughout the week.
findBabyWithHighestWeight: Finds the heaviest baby/babies of a specified gender. In case of a tie, results are sorted alphabetically by name. Returns an unmodifiable list.
findBabyWithSmallestHeight: Finds the shortest baby/babies of a specified gender. In case of a tie, results are sorted by weight in ascending order. Returns an unmodifiable list.
findBabiesByBirthTime: Filters babies by birth time interval. 
Correctly handles both standard intra-day intervals (e.g., 07:00 to 13:00) and overnight intervals spanning midnight (e.g., 22:00 to 05:00).
main Method (Test Runner):
A standalone test suite that runs without external dependencies (e.g., JUnit/ArchUnit). 
It uses custom assertion helper methods (assertEquals, assertTrue, assertFalse, fail) to verify all business logic and edge cases.
*/

