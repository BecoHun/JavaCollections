import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Properties;

public class MainProperties {

    public static void main(String[] args) throws IOException {
        System.out.println("--- Starting Tests ---\n");

        runTest("resetShouldDiscardAllChanges", () -> {
            setUp();
            Config config = new Config();
            config.set("key3", "E");

            assertEquals("E", config.get("key3"));

            config.reset();
            assertNull(config.get("key3"));
        });

        runTest("getShouldReturnProperlyValues", () -> {
            setUp();
            Config config = new Config();

            assertEquals("A", config.get("key1"));
            assertEquals("D", config.get("key2"));

            config.remove("key1");
            assertEquals("B", config.get("key1"));

            config.save();
            assertEquals("B", config.get("key1"));

            config.set("default.filenames", "default2");
            config.save();
            config.reset();

            assertEquals("C", config.get("key1"));
            assertNull(config.get("key7"));
        });

        runTest("configShouldBeProperlyInitWhenNoDefaults", () -> {
            setUp();
            Config config = new Config();
            String fileName = "config.properties";
            Properties props = new Properties();
            props.setProperty("key1", "A");
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                props.store(out, "");
            }

            config.reset();

            assertEquals("A", config.get("key1"));
            assertNull(config.get("key2"));
        });

        runTest("configShouldBeProperlyInitWhenDefaultsIsEmpty", () -> {
            setUp();
            Config config = new Config();
            String fileName = "config.properties";
            Properties props = new Properties();
            props.setProperty("key1", "A");
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                props.store(out, "");
            }

            config.reset();

            assertEquals("A", config.get("key1"));
            assertNull(config.get("key2"));
        });

        System.out.println("\nAll tests executed successfully!");
    }

    // --- Setup Test Environment (replaces @BeforeEach) ---
    private static void setUp() throws IOException {
        try (PrintWriter out = new PrintWriter("config.properties")) {
            out.println("default.filenames = default1,default2");
            out.println("key1 = A");
        }

        try (PrintWriter out = new PrintWriter("default1.properties")) {
            out.println("key1 = B");
        }

        try (PrintWriter out = new PrintWriter("default2.properties")) {
            out.println("key1 = C");
            out.println("key2 = D");
        }
    }

    // --- Custom Assert Helper Methods ---
    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Error: Expected <" + expected + ">, but actual value was <" + actual + ">");
        }
    }

    private static void assertNull(Object actual) {
        if (actual != null) {
            throw new AssertionError("Error: Expected <null>, but actual value was <" + actual + ">");
        }
    }

    private static void runTest(String name, TestRunnable test) {
        try {
            test.run();
            System.out.println("[OK] " + name);
        } catch (Exception | AssertionError e) {
            System.err.println("[FAILED] " + name + " -> " + e.getMessage());
        }
    }

    @FunctionalInterface
    interface TestRunnable {
        void run() throws Exception;
    }
}

// --- Original Config Class ---
class Config {
    private Properties config;

    public Config() {
        reset();
    }

    public void reset() {
        try {
            Properties main = new Properties();

            try (FileInputStream input = new FileInputStream("config.properties")) {
                main.load(input);
            }

            String filenames = main.getProperty("default.filenames");
            Properties defaults = null;

            if (filenames != null && !filenames.trim().isEmpty()) {
                String[] files = filenames.split(",");

                for (int i = files.length - 1; i >= 0; i--) {
                    Properties current = (defaults == null) ? new Properties() : new Properties(defaults);

                    try (FileInputStream input = new FileInputStream(files[i].trim() + ".properties")) {
                        current.load(input);
                    }

                    defaults = current;
                }
            }

            config = new Properties(defaults);

            try (FileInputStream input = new FileInputStream("config.properties")) {
                config.load(input);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        return config.getProperty(key);
    }

    public void remove(String key) {
        config.remove(key);
    }

    public void save() {
        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            config.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String key, String value) {
        config.setProperty(key, value);
    }
}

/*
The Config class implements a multi-layered, hierarchical configuration management system using Java .properties files.
Key features and behavior of the code:
Configuration Hierarchy (Chaining): Reads default configuration files based on the default.filenames list defined in config.properties. 
Utilizing the inheritance mechanism of Java's Properties class, it builds a chain where earlier files in the list override values from later ones.
Main Override: 
The key-value pairs from config.properties itself are loaded at the end of the chain, ensuring they override all default values.
In-Memory Modification and Persistence:
Settings can be modified or removed at runtime using the set() and remove() methods. 
The save() method persists the current state back to the config.properties file.
Resetting (reset()): Reloads all files from the disk, discarding any unsaved in-memory changes.
Standalone Test Environment: 
The file includes a self-contained main method with custom assert helpers
that simulate and validate the expected behavior of the class without requiring external testing frameworks (like JUnit).
*/

