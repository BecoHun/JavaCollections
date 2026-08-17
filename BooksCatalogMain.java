import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class BooksCatalogMain {

    // ==========================================
    // 1. Author class
    // ==========================================
    public static class Author implements Comparable<Author> {
        private final String firstName;
        private final String lastName;

        public Author(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Author author = (Author) o;
            return Objects.equals(firstName, author.firstName) && Objects.equals(lastName, author.lastName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName);
        }

        @Override
        public String toString() {
            return firstName + ' ' + lastName;
        }

        @Override
        public int compareTo(Author o) {
            Objects.requireNonNull(o);
            int result = firstName.compareTo(o.firstName);
            if (result != 0) {
                return result;
            }
            return lastName.compareTo(o.lastName);
        }
    }

    // ==========================================
    // 2. Book class
    // ==========================================
    public static class Book implements Comparable<Book> {
        private final String title;
        private final List<String> genres;
        private final BigDecimal cost;

        public Book(String title, List<String> genres, BigDecimal cost) {
            this.title = title;
            this.genres = genres;
            this.cost = cost;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getGenres() {
            return genres;
        }

        public BigDecimal getCost() {
            return cost;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Book book = (Book) o;
            return Objects.equals(title, book.title) && Objects.equals(genres, book.genres) && Objects.equals(cost, book.cost);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, genres, cost);
        }

        @Override
        public String toString() {
            return "Book{" +
                    "title='" + title + '\'' +
                    ", genres=" + genres +
                    ", cost=" + (cost == null ? "unavailable" : cost) +
                    '}';
        }

        @Override
        public int compareTo(Book o) {
            Objects.requireNonNull(o);
            int result = title.compareTo(o.title);
            if (result != 0) {
                return result;
            }
            if (cost == null && o.cost == null) {
                return 0;
            }
            if (cost == null) {
                return 1;
            }
            if (o.cost == null) {
                return -1;
            }
            return cost.compareTo(o.cost);
        }
    }

    // ==========================================
    // 3. BooksCatalog class
    // ==========================================
    public static class BooksCatalog {
        private static final String EOL = "\n";
        private Map<Author, List<Book>> catalog;

        public BooksCatalog() {
            catalog = new TreeMap<>();
        }

        public BooksCatalog(Map<Author, List<Book>> catalog) {
            Objects.requireNonNull(catalog);
            this.catalog = new TreeMap<>();
            for (Map.Entry<Author, List<Book>> entry : catalog.entrySet()) {
                this.catalog.put(
                        entry.getKey(),
                        new ArrayList<>(entry.getValue())
                );
            }
        }

        public List<Book> findByAuthor(Author author) {
            Objects.requireNonNull(author);
            List<Book> books = catalog.get(author);
            if (books == null) {
                return null;
            }
            return new ArrayList<>(books);
        }

        public String getAllAuthors() {
            StringBuilder result = new StringBuilder();
            for (Author author : catalog.keySet()) {
                if (result.length() > 0) {
                    result.append(EOL);
                }
                result.append(author);
            }
            return result.toString();
        }

        public Map<Book, List<Author>> findAuthorsByBookTitle(String pattern) {
            Objects.requireNonNull(pattern);
            Map<Book, List<Author>> result = new TreeMap<>();
            Pattern searchPattern = Pattern.compile(
                    Pattern.quote(pattern),
                    Pattern.CASE_INSENSITIVE
            );

            for (Map.Entry<Author, List<Book>> entry : catalog.entrySet()) {
                Author author = entry.getKey();
                for (Book book : entry.getValue()) {
                    if (searchPattern.matcher(book.getTitle()).find()) {
                        List<Author> authors = result.get(book);
                        if (authors == null) {
                            authors = new ArrayList<>();
                            result.put(book, authors);
                        }
                        if (!authors.contains(author)) {
                            authors.add(author);
                        }
                    }
                }
            }
            return result;
        }

        public Set<Book> findBooksByGenre(String pattern) {
            Objects.requireNonNull(pattern);
            Set<Book> result = new TreeSet<>();
            Pattern searchPattern = Pattern.compile(
                    Pattern.quote(pattern),
                    Pattern.CASE_INSENSITIVE
            );

            for (List<Book> books : catalog.values()) {
                for (Book book : books) {
                    for (String genre : book.getGenres()) {
                        if (searchPattern.matcher(genre).find()) {
                            result.add(book);
                            break;
                        }
                    }
                }
            }

            if (result.isEmpty()) {
                return null;
            }
            return result;
        }

        public List<Author> findAuthorsByBook(Book book) {
            Objects.requireNonNull(book);
            List<Author> result = new ArrayList<>();
            for (Map.Entry<Author, List<Book>> entry : catalog.entrySet()) {
                for (Book currentBook : entry.getValue()) {
                    if (currentBook.equals(book)) {
                        result.add(entry.getKey());
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return catalog.toString();
        }
    }

    // ==========================================
    // 4. Main & Sample Tests
    // ==========================================
    public static void main(String[] args) {
        System.out.println("=== Books Catalog Execution Example ===");

        // Initialize sample data
        Map<Author, List<Book>> initialData = new HashMap<>();

        Author dumas = new Author("Alexandre", "Dumas");
        Book book1 = new Book("Twenty Years After", Arrays.asList("adventure", "historical"), new BigDecimal("286.02"));
        Book book2 = new Book("The Three Musketeers", Arrays.asList("adventure"), new BigDecimal("150.00"));

        Author verne = new Author("Jules", "Verne");
        Book book3 = new Book("Twenty Thousand Leagues Under the Sea", Arrays.asList("sci-fi", "adventure"), new BigDecimal("200.50"));

        initialData.put(dumas, new ArrayList<>(Arrays.asList(book1, book2)));
        initialData.put(verne, new ArrayList<>(Collections.singletonList(book3)));

        BooksCatalog catalog = new BooksCatalog(initialData);

        // Demo output
        System.out.println("\nAll authors:");
        System.out.println(catalog.getAllAuthors());

        System.out.println("\nSearch by genre ('adventure'):");
        System.out.println(catalog.findBooksByGenre("adventure"));

        System.out.println("\nSearch by title pattern ('tw'):");
        System.out.println(catalog.findAuthorsByBookTitle("tw"));

        // Running test suite
        System.out.println("\n=== Running Tests ===");
        runTests(catalog, dumas, verne, book1, book2, book3);
    }

    private static void runTests(BooksCatalog catalog, Author dumas, Author verne, Book book1, Book book2, Book book3) {
        int passed = 0;
        int failed = 0;

        // Test 1: findByAuthor for an existing author
        try {
            List<Book> dumasBooks = catalog.findByAuthor(dumas);
            if (dumasBooks != null && dumasBooks.size() == 2) {
                System.out.println("[PASS] Test 1: Search by author (Alexandre Dumas)");
                passed++;
            } else {
                throw new AssertionError();
            }
        } catch (Throwable e) {
            System.out.println("[FAIL] Test 1: Search by author");
            failed++;
        }

        // Test 2: findByAuthor for a non-existing author (should return null)
        try {
            List<Book> unknownBooks = catalog.findByAuthor(new Author("John", "Doe"));
            if (unknownBooks == null) {
                System.out.println("[PASS] Test 2: Search for non-existing author (returns null)");
                passed++;
            } else {
                throw new AssertionError();
            }
        } catch (Throwable e) {
            System.out.println("[FAIL] Test 2: Search for non-existing author");
            failed++;
        }

        // Test 3: findBooksByGenre
        try {
            Set<Book> scifiBooks = catalog.findBooksByGenre("sci-fi");
            if (scifiBooks != null && scifiBooks.contains(book3)) {
                System.out.println("[PASS] Test 3: Search by genre ('sci-fi')");
                passed++;
            } else {
                throw new AssertionError();
            }
        } catch (Throwable e) {
            System.out.println("[FAIL] Test 3: Search by genre");
            failed++;
        }

        // Test 4: findAuthorsByBook
        try {
            List<Author> authors = catalog.findAuthorsByBook(book1);
            if (authors.size() == 1 && authors.get(0).equals(dumas)) {
                System.out.println("[PASS] Test 4: Find author by book");
                passed++;
            } else {
                throw new AssertionError();
            }
        } catch (Throwable e) {
            System.out.println("[FAIL] Test 4: Find author by book");
            failed++;
        }

        // Test 5: Author compareTo sorting logic
        try {
            Author a1 = new Author("Alexandre", "Dumas");
            Author a2 = new Author("Jules", "Verne");
            if (a1.compareTo(a2) < 0) { // 'Alexandre' < 'Jules'
                System.out.println("[PASS] Test 5: Author sorting logic (compareTo)");
                passed++;
            } else {
                throw new AssertionError();
            }
        } catch (Throwable e) {
            System.out.println("[FAIL] Test 5: Author sorting logic");
            failed++;
        }

        System.out.println("\nTest results: " + passed + " passed, " + failed + " failed.");
    }
}
/*
The **`BooksCatalogMain`** is a Java application for managing a book catalog, designed to store, search, and sort book and author data.
* **Author and Book Management (`Author`, `Book`):**
* The `Author` class stores the first and last name of an author and enables alphabetical sorting.
* The `Book` class contains the title, genres (as a list), and price (`BigDecimal`) of a book. It is sorted by title and price.
* **Catalog Features (`BooksCatalog`):**
* **Search by Author:** Returns all books written by a specific author.
* **List Authors:** Concatenates all authors present in the catalog into an easy-to-read format.
* **Search by Title:** Performs a case-insensitive search for title snippets, returning the corresponding authors.
* **Search by Genre:** Finds all books matching the specified genre.
* **Find Author by Book:** Identifies which author a specific book belongs to.
* **Demo and Automated Tests (`main`, `runTests`):**
* Includes sample data initialization (featuring works by Alexandre Dumas and Jules Verne).
* Uses a built-in test suite to automatically verify search and sorting functionalities with `PASS`/`FAIL` feedback.
*/

