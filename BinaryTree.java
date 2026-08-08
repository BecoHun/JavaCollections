import java.util.Objects;
import java.util.Optional;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BinaryTree {
    private static final String INDENT = "-~-";
    private static final String EOL = System.lineSeparator();
    private Node root;
    private int size;
    private static final class Node {
        Integer e;
        Node left;
        Node right;
        Node(Integer e) {
            this.e = e;
        }
        @Override
        public String toString() {
            return "Node[" + e + "]";
        }
    }
    public BinaryTree() {
        super();
    }
    public BinaryTree(Integer... elements) {
        Objects.requireNonNull(elements);

        for (Integer element : elements) {
            add(Objects.requireNonNull(element));
        }
    }
    public boolean add(Integer element) {
        Objects.requireNonNull(element);
        if (root == null) {
            root = new Node(element);
            size++;
            return true;
        }
        Node current = root;
        while (true) {
            int comparison = element.compareTo(current.e);
            if (comparison == 0) {
                return false;
            }
            if (comparison < 0) {
                if (current.left == null) {
                    current.left = new Node(element);
                    size++;
                    return true;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(element);
                    size++;
                    return true;
                }
                current = current.right;
            }
        }
    }
    public void addAll(Integer... ar) {
        Objects.requireNonNull(ar);

        for (Integer element : ar) {
            add(Objects.requireNonNull(element));
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendInOrder(sb, root);
        return "[" + sb + "]";
    }
    public Optional<Integer> remove(Integer element) {
        Objects.requireNonNull(element);
        Node parent = null;
        Node current = root;
        while (current != null) {
            int comparison = element.compareTo(current.e);
            if (comparison == 0) {
                break;
            }
            parent = current;
            if (comparison < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (current == null) {
            return Optional.empty();
        }
        Integer removed = current.e;
        if (current.left != null && current.right != null) {
            Node successorParent = current;
            Node successor = current.right;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }
            current.e = successor.e;
            if (successorParent.left == successor) {
                successorParent.left = successor.right;
            } else {
                successorParent.right = successor.right;
            }
        }
        else {
            Node replacement;
            if (current.left != null) {
                replacement = current.left;
            } else {
                replacement = current.right;
            }
            if (parent == null) {
                root = replacement;
            } else if (parent.left == current) {
                parent.left = replacement;
            } else {
                parent.right = replacement;
            }
        }
        size--;
        return Optional.of(removed);
    }
    public int size() {
        return size;
    }
    String asTreeString() {
        StringBuilder sb = new StringBuilder();
        asTreeString(sb, root, 0);
        return sb.toString();
    }
    private void asTreeString(StringBuilder sb, Node node, int k) {
        if (node == null) {
            return;
        }
        asTreeString(sb, node.right, k + 1);
        sb.append(INDENT.repeat(k));
        sb.append(String.format("%3s", node.e)).append(EOL);
        asTreeString(sb, node.left, k + 1);
    }
    private void appendInOrder(StringBuilder sb, Node node) {
        if (node == null) {
            return;
        }
        appendInOrder(sb, node.left);
        if (!sb.isEmpty()) {
            sb.append(", ");
        }
        sb.append(node.e);
        appendInOrder(sb, node.right);
    }
    
/*
BinaryTree – How It Works
BinaryTree is a Binary Search Tree (BST) that stores Integer values. Each node can have a left and a right child.
The main BST rule is:
smaller value → left
larger value  → right
equal value   → duplicate, do not add
The tree has two main fields:
root – points to the first node of the tree.
size – stores the number of elements.
Adding elements:
add() starts at the root and compares the new value with the current node.
It moves left if the value is smaller and right if it is larger. When an empty position is found, a new node is created.
If the value already exists, nothing is added.
Removing elements:
remove() first searches for the requested value. There are three cases:
No children → simply remove the node.
One child → replace the node with its child.
Two children → find the smallest value in the right subtree (in-order successor), copy it to the node, and remove the successor.
toString() uses in-order traversal:
LEFT → NODE → RIGHT
Because this is a BST, this produces the elements in ascending order.
addAll() simply calls add() for every element.
size() returns the stored size field without traversing the tree.
Objects.requireNonNull() prevents null values from being stored in the tree, while Optional.empty() is returned when remove() cannot find the requested element.
*/
    
    public static void main(String[] args) {

        System.out.println("==================================================");
        System.out.println("             BINARY TREE TEST / DEMO");
        System.out.println("==================================================");

        testSize();
        testRemoveFromEmpty();
        testRemoveNull();
        testRemoveNonExisting();
        testRemove();
        testAddAll();
        testAdd();
        testConstructorNull();
        testToString();

        System.out.println();
        System.out.println("==================================================");
        System.out.println("               ALL TESTS FINISHED");
        System.out.println("==================================================");
    }

    private static void printTree(String title, BinaryTree tree) {
        System.out.println();
        System.out.println("----- " + title + " -----");
        System.out.println("size       = " + tree.size());
        System.out.println("toString() = " + tree);
        System.out.println("Tree:");

        String treeString = tree.asTreeString();

        if (treeString.isEmpty()) {
            System.out.println("(empty)");
        } else {
            System.out.print(treeString);
        }

        System.out.println("------------------------------------------");
    }

    private static void testSize() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("1. TEST: size(), add(), remove()");
        System.out.println("==================================================");

        Random r = new Random(205);
        BinaryTree tree = new BinaryTree();

        System.out.println();
        System.out.println("Empty BinaryTree created.");

        printTree("Initial state", tree);

        System.out.println("---- Adding 8 elements ----");

        for (int v = 0; v < 8; v++) {

            int nextInt = r.nextInt(-25, 25);

            System.out.println();
            System.out.println("[" + (v + 1) + "/8]");
            System.out.println("add(" + nextInt + ")");

            int oldSize = tree.size();
            boolean result = tree.add(nextInt);

            System.out.println("Return value: " + result);
            System.out.println("size before:  " + oldSize);
            System.out.println("size after:   " + tree.size());

            printTree("After add(" + nextInt + ")", tree);
        }

        System.out.println();
        System.out.println("---- Adding the same 8 elements again ----");

        Random rn = new Random(205);

        for (int v = 0; v < 8; v++) {

            int nextInt = rn.nextInt(-25, 25);

            System.out.println();
            System.out.println("[" + (v + 1) + "/8]");
            System.out.println("add(" + nextInt + ")");

            int oldSize = tree.size();
            boolean result = tree.add(nextInt);

            System.out.println("Return value: " + result);
            System.out.println("size before:  " + oldSize);
            System.out.println("size after:   " + tree.size());

            if (!result) {
                System.out.println(
                        "-> The element already exists, so it was not added again."
                );
            }

            printTree("After duplicate add", tree);
        }

        System.out.println();
        System.out.println("---- Removing 8 elements ----");

        Random rd = new Random(205);

        for (int v = 7; v >= 0; v--) {

            int nextInt = rd.nextInt(-25, 25);

            System.out.println();
            System.out.println("remove(" + nextInt + ")");

            int oldSize = tree.size();
            Optional<Integer> result = tree.remove(nextInt);

            System.out.println("Return value: " + result);
            System.out.println("size before:  " + oldSize);
            System.out.println("size after:   " + tree.size());

            if (result.isPresent()) {
                System.out.println("-> The element was successfully removed.");
            } else {
                System.out.println("-> The element was not found in the tree.");
            }

            printTree("After remove(" + nextInt + ")", tree);
        }
    }

    private static void testRemoveFromEmpty() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("2. TEST: remove() from an empty tree");
        System.out.println("==================================================");

        BinaryTree tree = new BinaryTree();

        System.out.println("Calling remove(0) on an empty tree:");

        Optional<Integer> result = tree.remove(0);

        System.out.println("Return value: " + result);
        System.out.println("Expected:     Optional.empty");
        System.out.println("size:         " + tree.size());
    }

    private static void testRemoveNull() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("3. TEST: remove(null)");
        System.out.println("==================================================");

        BinaryTree tree = new BinaryTree();

        try {
            System.out.println("Calling remove(null)...");

            tree.remove(null);

            System.out.println(
                    "ERROR: No NullPointerException was thrown!"
            );

        } catch (NullPointerException e) {
            System.out.println(
                    "OK: NullPointerException was thrown."
            );
            System.out.println(
                    "-> null is not allowed as an element."
            );
        }
    }

    private static void testRemoveNonExisting() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("4. TEST: removing a non-existing element");
        System.out.println("==================================================");

        BinaryTree tree = new BinaryTree(8, 6, 12, 10);

        printTree("Initial tree", tree);

        System.out.println("remove(13)");

        Optional<Integer> result = tree.remove(13);

        System.out.println("Return value: " + result);
        System.out.println("size:         " + tree.size());

        printTree("After remove(13)", tree);

        System.out.println(
                "-> 13 was not in the tree, so the tree did not change."
        );
    }

    private static void testRemove() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("5. TEST: complex remove() test");
        System.out.println("==================================================");

        Random r = new Random(922);

        Integer[] data = IntStream
                .generate(() -> r.nextInt(0, 40))
                .limit(20)
                .boxed()
                .toArray(Integer[]::new);

        System.out.println("Generated data:");
        System.out.println(Arrays.toString(data));

        BinaryTree tree = new BinaryTree(data);

        printTree("Tree after construction", tree);

        Integer[] toRemove = {
                14, 19, 21, 23, 39,
                33, 13, 0, 2, 7,
                9, 37, 27, 26, 38
        };

        System.out.println();
        System.out.println("Elements to remove:");
        System.out.println(Arrays.toString(toRemove));

        System.out.println();
        System.out.println("remove(35)");

        Optional<Integer> result = tree.remove(35);

        System.out.println("Return value: " + result);
        System.out.println("size:         " + tree.size());

        printTree("After removing 35", tree);

        for (Integer element : toRemove) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("remove(" + element + ")");
            System.out.println("==========================================");

            int oldSize = tree.size();

            Optional<Integer> removed = tree.remove(element);

            System.out.println("Return value: " + removed);
            System.out.println("size before:  " + oldSize);
            System.out.println("size after:   " + tree.size());

            printTree(
                    "After remove(" + element + ")",
                    tree
            );
        }
    }

    private static void testAddAll() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("6. TEST: constructor + addAll()");
        System.out.println("==================================================");

        Random r = new Random(25);

        Integer[] data = IntStream
                .generate(() -> r.nextInt(-5, 5))
                .limit(5)
                .boxed()
                .toArray(Integer[]::new);

        System.out.println("First data set:");
        System.out.println(Arrays.toString(data));

        BinaryTree tree = new BinaryTree(data);

        printTree("After construction", tree);

        System.out.println();
        System.out.println("addAll(data)");

        tree.addAll(data);

        printTree(
                "After adding the same data with addAll()",
                tree
        );

        Integer[] newData = IntStream
                .generate(() -> r.nextInt(-5, 5))
                .limit(5)
                .boxed()
                .toArray(Integer[]::new);

        System.out.println();
        System.out.println("Second data set:");
        System.out.println(Arrays.toString(newData));

        System.out.println();
        System.out.println("addAll(newData)");

        tree.addAll(newData);

        printTree("After the second addAll()", tree);
    }

    private static void testAdd() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("7. TEST: add()");
        System.out.println("==================================================");

        Random r = new Random(25);

        Integer[] data = IntStream
                .generate(() -> r.nextInt(-5, 5))
                .limit(5)
                .boxed()
                .toArray(Integer[]::new);

        System.out.println("First data set:");
        System.out.println(Arrays.toString(data));

        BinaryTree tree = new BinaryTree(data);

        printTree("After construction", tree);

        System.out.println();
        System.out.println("---- Adding elements that already exist ----");

        for (Integer element : data) {

            System.out.println();
            System.out.println("add(" + element + ")");

            int oldSize = tree.size();
            boolean result = tree.add(element);

            System.out.println("Return value: " + result);
            System.out.println("size before:  " + oldSize);
            System.out.println("size after:   " + tree.size());

            if (!result) {
                System.out.println(
                        "-> Duplicate: the element already existed."
                );
            }

            printTree("Current state", tree);
        }

        Integer[] newData = IntStream
                .generate(() -> r.nextInt(5, 15))
                .limit(5)
                .boxed()
                .toArray(Integer[]::new);

        System.out.println();
        System.out.println("New data:");
        System.out.println(Arrays.toString(newData));

        System.out.println();
        System.out.println("---- Adding new elements ----");

        for (Integer element : newData) {

            System.out.println();
            System.out.println("add(" + element + ")");

            int oldSize = tree.size();
            boolean result = tree.add(element);

            System.out.println("Return value: " + result);
            System.out.println("size before:  " + oldSize);
            System.out.println("size after:   " + tree.size());

            printTree("After adding new element", tree);
        }
    }

    private static void testConstructorNull() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("8. TEST: null handling in constructor");
        System.out.println("==================================================");

        try {

            System.out.println(
                    "new BinaryTree((Integer[]) null)"
            );

            new BinaryTree((Integer[]) null);

            System.out.println(
                    "ERROR: No exception was thrown!"
            );

        } catch (NullPointerException e) {

            System.out.println(
                    "OK: NullPointerException was thrown."
            );
        }

        try {

            System.out.println();
            System.out.println(
                    "new BinaryTree(1, null, 1)"
            );

            new BinaryTree(1, null, 1);

            System.out.println(
                    "ERROR: No exception was thrown!"
            );

        } catch (NullPointerException e) {

            System.out.println(
                    "OK: NullPointerException was thrown."
            );
        }
    }

    private static void testToString() {

        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("9. TEST: toString()");
        System.out.println("==================================================");

        Random r = new Random(123);

        Integer[] data = IntStream
                .generate(() -> r.nextInt(-25, 25))
                .limit(15)
                .boxed()
                .toArray(Integer[]::new);

        System.out.println("First data set:");
        System.out.println(Arrays.toString(data));

        String expected = Arrays.stream(data)
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));

        BinaryTree tree = new BinaryTree(data);

        System.out.println();
        System.out.println("Expected toString():");
        System.out.println(expected);

        System.out.println();
        System.out.println("Actual toString():");
        System.out.println(tree);

        printTree("After first data set", tree);

        Integer[] data2 = IntStream
                .generate(() -> r.nextInt(-25, 25))
                .limit(15)
                .boxed()
                .toArray(Integer[]::new);

        System.out.println();
        System.out.println("Second data set:");
        System.out.println(Arrays.toString(data2));

        tree.addAll(data2);

        Integer[] allData = Arrays.copyOf(data, 30);
        System.arraycopy(data2, 0, allData, 15, 15);

        expected = Arrays.stream(allData)
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));

        System.out.println();
        System.out.println("---- After second addAll() ----");

        System.out.println("Expected:");
        System.out.println(expected);

        System.out.println();
        System.out.println("Actual:");
        System.out.println(tree);

        printTree("Final state", tree);
    }
}

