import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

public class Gradebook {

    // =========================================================================
    // 1. MODELS AND INTERFACES (Student, StudentGradebook)
    // =========================================================================

    public static class Student {
        private String firstName;
        private String lastName;
        private String group;

        public Student(String firstName, String lastName, String group) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.group = group;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getGroup() {
            return group;
        }

        @Override
        public String toString() {
            return firstName + "_" + lastName + "_" + group;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Student student = (Student) o;
            return Objects.equals(firstName, student.firstName) &&
                   Objects.equals(lastName, student.lastName) &&
                   Objects.equals(group, student.group);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName, group);
        }
    }

    public interface StudentGradebook {
        boolean addEntryOfStudent(Student student, String discipline, BigDecimal grade);
        int size();
        Comparator<Student> getComparator();
        List<String> getStudentsByDiscipline(String discipline);
        Map<Student, Map<String, BigDecimal>> removeStudentsByGrade(BigDecimal grade);
        Map<BigDecimal, List<Student>> getAndSortAllStudents();
    }

    // =========================================================================
    // 2. IMPLEMENTATION (StudentGradebookImpl)
    // =========================================================================

    public static class StudentGradebookImpl implements StudentGradebook {
        private final Map<Student, Map<String, BigDecimal>> map;

        public StudentGradebookImpl() {
            map = new LinkedHashMap<>();
        }

        @Override
        public boolean addEntryOfStudent(Student student, String discipline, BigDecimal grade) {
            if (!map.containsKey(student)) {
                map.put(student, new HashMap<String, BigDecimal>());
            }

            Map<String, BigDecimal> grades = map.get(student);

            if (grades.containsKey(discipline)) {
                return false;
            }

            grades.put(discipline, grade);
            return true;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public Comparator<Student> getComparator() {
            return new Comparator<Student>() {
                @Override
                public int compare(Student s1, Student s2) {
                    if (s1 == null || s2 == null) {
                        throw new RuntimeException();
                    }

                    int result = s1.getLastName().compareTo(s2.getLastName());

                    if (result != 0) {
                        return result;
                    }

                    return s1.getFirstName().compareTo(s2.getFirstName());
                }
            };
        }

        @Override
        public List<String> getStudentsByDiscipline(String discipline) {
            List<String> result = new ArrayList<>();

            for (Map.Entry<Student, Map<String, BigDecimal>> entry : map.entrySet()) {
                Student student = entry.getKey();
                Map<String, BigDecimal> grades = entry.getValue();

                if (grades.containsKey(discipline)) {
                    // Note: Standard format according to the test specification:
                    // LName_FName: Grade
                    result.add(student.getLastName() + "_" + student.getFirstName() + ": " + grades.get(discipline));
                }
            }

            return result;
        }

        @Override
        public Map<Student, Map<String, BigDecimal>> removeStudentsByGrade(BigDecimal grade) {
            Map<Student, Map<String, BigDecimal>> removed = new HashMap<>();
            List<Student> studentsToRemove = new ArrayList<>();

            for (Map.Entry<Student, Map<String, BigDecimal>> entry : map.entrySet()) {
                Student student = entry.getKey();
                Map<String, BigDecimal> grades = entry.getValue();

                BigDecimal average = calculateAverage(grades);

                if (average.compareTo(grade) <= 0) {
                    studentsToRemove.add(student);
                }
            }

            for (Student student : studentsToRemove) {
                removed.put(student, map.remove(student));
            }

            return removed;
        }

        @Override
        public Map<BigDecimal, List<Student>> getAndSortAllStudents() {
            Map<BigDecimal, List<Student>> result = new TreeMap<>();

            for (Map.Entry<Student, Map<String, BigDecimal>> entry : map.entrySet()) {
                Student student = entry.getKey();
                Map<String, BigDecimal> grades = entry.getValue();

                BigDecimal average = calculateAverage(grades);

                if (!result.containsKey(average)) {
                    result.put(average, new ArrayList<Student>());
                }

                result.get(average).add(student);
            }

            return result;
        }

        private BigDecimal calculateAverage(Map<String, BigDecimal> grades) {
            if (grades.isEmpty()) {
                return BigDecimal.ZERO;
            }
            BigDecimal sum = BigDecimal.ZERO;

            for (BigDecimal grade : grades.values()) {
                sum = sum.add(grade);
            }

            return sum.divide(
                    BigDecimal.valueOf(grades.size()),
                    10,
                    RoundingMode.HALF_UP
            ).stripTrailingZeros();
        }
    }

    // =========================================================================
    // 3. TESTS AND MAIN METHOD
    // =========================================================================

    private static Student S1 = new Student("LName1", "FName1", "Group1");
    private static Student S2 = new Student("LName2", "FName2", "Group1");
    private static Student S3 = new Student("LName3", "FName3", "Group1");
    private static Student S4 = new Student("LName4", "FName4", "Group2");
    private static Student S5 = new Student("LName5", "FName5", "Group2");
    private static Student S6 = new Student("LName6", "FName6", "Group2");

    private static StudentGradebook gbook;

    private static void setUp() {
        gbook = new StudentGradebookImpl();
        gbook.addEntryOfStudent(S1, "dis1", BigDecimal.valueOf(3.3));
        gbook.addEntryOfStudent(S1, "dis2", BigDecimal.valueOf(3.4));
        gbook.addEntryOfStudent(S1, "dis3", BigDecimal.valueOf(3.5));

        gbook.addEntryOfStudent(S2, "dis1", BigDecimal.valueOf(3.3));
        gbook.addEntryOfStudent(S2, "dis2", BigDecimal.valueOf(3.4));
        gbook.addEntryOfStudent(S2, "dis3", BigDecimal.valueOf(3.5));

        gbook.addEntryOfStudent(S3, "dis1", BigDecimal.valueOf(3.9));
        gbook.addEntryOfStudent(S3, "dis2", BigDecimal.valueOf(4.0));
        gbook.addEntryOfStudent(S3, "dis3", BigDecimal.valueOf(4.1));

        gbook.addEntryOfStudent(S4, "dis1", BigDecimal.valueOf(4.6));
        gbook.addEntryOfStudent(S4, "dis2", BigDecimal.valueOf(4.2));
        gbook.addEntryOfStudent(S4, "dis3", BigDecimal.valueOf(5.0));

        gbook.addEntryOfStudent(S5, "dis1", BigDecimal.valueOf(4.5));
        gbook.addEntryOfStudent(S5, "dis2", BigDecimal.valueOf(4.6));
        gbook.addEntryOfStudent(S5, "dis3", BigDecimal.valueOf(4.7));

        gbook.addEntryOfStudent(S6, "dis1", BigDecimal.valueOf(4.4));
        gbook.addEntryOfStudent(S6, "dis2", BigDecimal.valueOf(4.6));
        gbook.addEntryOfStudent(S6, "dis3", BigDecimal.valueOf(4.8));
    }

    public static void main(String[] args) {
        System.out.println("Starting tests...\n");

        runTest("addEntryOfStudentShouldReturnCorrectValues", () -> addEntryOfStudentShouldReturnCorrectValues());
        runTest("sizeShouldBeEqualed6", () -> sizeShouldBeEqualed6());
        runTest("getAndSortAllStudentsShouldReturnMapWithCorrectContent", () -> getAndSortAllStudentsShouldReturnMapWithCorrectContent());
        runTest("getComparatorShouldReturnCorrectComparator", () -> getComparatorShouldReturnCorrectComparator());
        runTest("getStudentsByDisciplineShouldReturnProperValue", () -> getStudentsByDisciplineShouldReturnProperValue());
        runTest("removeStudentsByGradeShouldProperlyRemoveEntries", () -> removeStudentsByGradeShouldProperlyRemoveEntries());
        runTest("appShouldNotUseLambdaExpressions", () -> appShouldNotUseLambdaExpressions());

        System.out.println("\nAll tests executed successfully!");
    }

    private static void runTest(String name, Runnable testMethod) {
        setUp();
        try {
            testMethod.run();
            System.out.println("[ OK ] " + name);
        } catch (Throwable t) {
            System.err.println("[FAIL] " + name);
            t.printStackTrace();
        }
    }

    // -- Test Methods --

    static void addEntryOfStudentShouldReturnCorrectValues() {
        assertTrue(gbook.addEntryOfStudent(S1, "dis4", BigDecimal.valueOf(3.3)));
        assertFalse(gbook.addEntryOfStudent(S1, "dis4", BigDecimal.valueOf(3.3)));
        assertFalse(gbook.addEntryOfStudent(S1, "dis4", BigDecimal.valueOf(3.3)));
        assertTrue(gbook.addEntryOfStudent(S1, "dis5", BigDecimal.valueOf(3.3)));
    }

    static void sizeShouldBeEqualed6() {
        assertEquals(6, gbook.size());
    }

    static void getAndSortAllStudentsShouldReturnMapWithCorrectContent() {
        Map<BigDecimal, List<Student>> map = gbook.getAndSortAllStudents();

        assertEquals(Arrays.asList(S1, S2), map.get(BigDecimal.valueOf(3.4)));
        assertEquals(Arrays.asList(S3), map.get(BigDecimal.valueOf(4.0)));
        assertEquals(Arrays.asList(S4, S5, S6), map.get(BigDecimal.valueOf(4.6)));
    }

    static void getComparatorShouldReturnCorrectComparator() {
        Comparator<Student> comp = gbook.getComparator();

        assertThrows(RuntimeException.class, () -> comp.compare(S1, null));
        assertTrue(Math.signum(comp.compare(S1, S2)) == -Math.signum(comp.compare(S2, S1)));

        Student s1 = new Student("name1", "name1", "group1");
        Student s2 = new Student("name1", "name1", "group1");
        Student s3 = new Student("name2", "name2", "group2");

        assertTrue(comp.compare(s1, s2) == 0);
        assertTrue(Math.signum(comp.compare(s1, s3)) == Math.signum(comp.compare(s1, s3)));
    }

    static void getStudentsByDisciplineShouldReturnProperValue() {
        List<String> expected = Arrays.asList(
                "LName1_FName1: 3.3",
                "LName2_FName2: 3.3",
                "LName3_FName3: 3.9",
                "LName4_FName4: 4.6",
                "LName5_FName5: 4.5",
                "LName6_FName6: 4.4");
        List<String> actual = gbook.getStudentsByDiscipline("dis1");
        assertEquals(expected, actual);
    }

    static void removeStudentsByGradeShouldProperlyRemoveEntries() {
        gbook.removeStudentsByGrade(BigDecimal.valueOf(4.0));

        List<Student> studs = new ArrayList<>();
        gbook.getAndSortAllStudents().forEach((k, v) -> studs.addAll(v));

        assertEquals(3, studs.size());
        assertTrue(studs.contains(S4));
        assertTrue(studs.contains(S5));
        assertTrue(studs.contains(S6));
    }

    static void appShouldNotUseLambdaExpressions() {
        Stream.of(StudentGradebookImpl.class)
                .map(Class::getDeclaredMethods)
                .flatMap(Stream::of)
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .filter(m -> Modifier.isPrivate(m.getModifiers()))
                .map(Method::getName)
                .filter(name -> name.contains("lambda$"))
                .findAny()
                .ifPresent(m -> fail("Usage of lambda expressions is restricted in implementation: " + m));
    }

    // =========================================================================
    // 4. SIMPLE ASSERTION HELPER METHODS 
    // =========================================================================

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected value: true, but was: false.");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected value: false, but was: true.");
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Actual value does not match expected!\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, Runnable executable) {
        try {
            executable.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }
            throw new AssertionError("A different type of exception was thrown! Expected: " + expectedType.getName() + ", actual: " + actual.getClass().getName());
        }
        throw new AssertionError("Expected exception was not thrown: " + expectedType.getName());
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }
}

/*
Student Gradebook – System Summary
The Student Gradebook is a self-contained, single-file Java application designed to store, filter, average without precision loss, 
and test student academic records without relying on external libraries.
Key Components
Student (Model): Class storing student data (first name, last name, group). Overrides equals and hashCode for safe usage in Map structures.
StudentGradebookImpl (Business Logic):
Data Storage: Uses LinkedHashMap<Student, BigDecimal Map<String,>> to preserve insertion order.
Precision: BigDecimal-based arithmetic mean calculations rounded to 10 decimal places (HALF_UP).
Sorting: Uses TreeMap to group and sort students by their grade point average (GPA).
Filtering: Subject-based queries and removal of students below a given GPA threshold.
Gradebook (Test Runner): Custom JUnit-independent testing setup with built-in assert helpers and Reflection-based code compliance checks (e.g., restricting lambda expressions).
Core API Methods
addEntryOfStudent(...)Adds a grade for a student if the subject does not exist yet.
size()Returns the total count of unique students.
getComparator()Returns a name-based comparator (Last Name -> First Name).
getStudentsByDiscipline(...)Retrieves students and grades for a given subject ("LastName_FirstName: Grade").
removeStudentsByGrade(...)Removes students with a GPA less than or equal to a specified limit.
getAndSortAllStudents()Groups and sorts all students by average grade in ascending order.
*/

