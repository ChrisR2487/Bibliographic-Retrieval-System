import java.util.Random;
import java.util.TreeSet;

public class LinkedList {
    /**
     * If docID is not already stored in this list, add it to a node, while
     * keeping the list sorted in ascending order.
     *
     * @param docId the id to be added to the list
     * @return true if the list was changed, and false otherwise
     */
    public boolean addDocument(int docId) {
        Node node = new Node(docId);

        // Add node to front of list if appropriate
        if (first == null || first.id() > docId) {
            node.next = first;
            first = node;
            return true;
        }

        if (first.id() == docId) return false; // do not add duplicates

        Node prev = first; // prev points to a node with an id less than docId
        while (prev.next != null && prev.next.id() < docId) {
            prev = prev.next;
        }

        // prev marks the insertion point
        if (prev.next == null || prev.next.id() > docId) {
            node.next = prev.next;
            prev.next = node;
            return true;
        }
        else {
            // Do not add duplicates
            return false;
        }
    }

    /**
     * Remove docID if it is stored in this list.
     *
     * @param docId the id to be removed from the list
     * @return true if the list was changed, and false otherwise
     */
    public boolean removeDocument(int docId) {
        // Note that this assumes that there are no duplicates in the list
        if (first == null) return false;
        if (first.id() == docId) {
            first = first.next;
            return true;
        }

        // Advance prev to node before one to be removed
        Node prev = first;
        while (prev.next != null && prev.next.id() < docId) {
            prev = prev.next;
        }

        // Remove next node, if appropriate
        if (prev.next != null && prev.next.id() == docId) {
            prev.setNext(prev.next().next());
            return true;
        }
        else {
            return false;
        }

    }


    /**
     * Add all of the nodes from the list other to this list. The list other should
     * be empty when this method is done running.
     * NOTE: You should not create new nodes, just add the existing nodes from the other
     *       list into this one, making sure to maintain the sorted order of this list.
     *
     * @param other the other list with nodes to be added
     */
    public void mergeList(LinkedList other) {
        Node otherP = other.first; // pointer to other list
        Node q = null; // pointer behind p in current list
        Node p = first; // pointer to node in current list

        // While we have unmerged nodes
        while (otherP != null && p != null) {
            if (otherP.id() < p.id()) {
                // merge otherP into this list
                Node tmp = otherP.next;
                otherP.next = p;
                if (q == null) {
                    first = otherP;
                }
                else {
                    q.next = otherP;
                }
                q = otherP;
                otherP = tmp;
            }
            else if (otherP.id() == p.id()) {
                otherP = otherP.next;
            }
            else {
                q = p;
                p = p.next;
            }
        }

        if (otherP != null) {
            // merge remaining nodes into list
            if (q == null) {
                first = otherP;
            }
            else {
                q.next = otherP;
            }
        }
        other.first = null;
    }

    /**
     * Return a list that contains the ids that are present in this list and
     * other list.
     * Note:  Neither this list nor other list should change.
     *
     * @param other the other list
     *
     * @return the list with the id's present in both lists
     */
    public LinkedList andMerge(LinkedList other) {
        LinkedList retVal = new LinkedList();
        retVal.first = andMerge(this.first, other.first);

        return retVal;
    }

    private static Node andMerge(Node n1, Node n2) {
        if (n1 == null || n2 == null) return null;

        if (n1.id() == n2.id()) {
            Node node = new Node(n1.id());
            node.next = andMerge(n1.next, n2.next);
            return node;
        }
        else if (n1.id() < n2.id()) {
            return andMerge(n1.next, n2);
        }
        else {
            return andMerge(n1, n2.next);
        }
    }

    /**
     * Return a list that contains the ids that are present in this list or t
     * other list.
     * Note:  Neither this list nor other list should change.
     *
     * @param other the other list
     *
     * @return the list with the id's present in at least one list
     */
    public LinkedList orMerge(LinkedList other) {
        LinkedList retVal = new LinkedList();
        Node lastAdded = null; // The last node we have added to the new list

        Node p = this.first;
        Node q = other.first;

        while (p != null || q != null) {
            int id = -1; // Id for new node
            if (p == null || (q != null && p.id() > q.id())) {
                id = q.id();
                q = q.next();
            }
            else if (q == null || p.id() < q.id()) {
                id = p.id();
                p = p.next;
            }
            else { // p.id() == q.id()
                // Note that I am not adding duplicate values
                id = p.id();
                p = p.next;
                q = q.next;
            }
            Node node = new Node(id);
            if (lastAdded == null) {
                retVal.first = node;
            }
            else {
                lastAdded.next = node;
            }
            lastAdded = node;
        }

        return retVal;
    }

    private Node first;

    private static class Node {
        private int documentID;
        private Node next;

        public Node(int documentID) { this.documentID = documentID; }

        public int id() { return documentID; }
        public Node next() { return next; }

        public void setNext(Node next) { this.next = next; }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Node p = first;

        while (p != null) {
            sb.append(p.id());
            sb.append(" ");
            p = p.next;
        }
        return sb.toString();
    }

    private static String setToString(TreeSet<Integer> set) {
        StringBuffer sb = new StringBuffer();

        for (Integer n: set) {
            sb.append(n);
            sb.append(" ");
        }
        return sb.toString();
    }

    private static boolean testAddDocument() {
        for (int i = 0; i < 100; i++) {
            TreeSet<Integer> set = new TreeSet<>();
            LinkedList list = new LinkedList();
            Random prng = new Random();

            for (int j = 0; j < 1000; j++) {
                int id = prng.nextInt();
                if (!set.contains(id)) {
                    set.add(id);
                    list.addDocument(id);
                }
            }
            if (!list.toString().equals(setToString(set))) return false;
        }
        return true;
    }

    private static boolean testRemoveDocument() {
        LinkedList list = new LinkedList();
        boolean success = true;

        // Test remove on empty list
        if (list.removeDocument(0) != false) {
            System.err.println("Bad return value on remove from empty list.");
            success =  false;
        }

        list = new LinkedList();
        TreeSet<Integer> set = new TreeSet<>();
        list.addDocument(1);
        boolean retVal = list.removeDocument(1);
        if (!list.toString().equals(setToString(set))) {
            System.err.println("Failed to remove from a list with one element.");
            success = false;
        }
        if (!retVal) {
            System.err.println("Bad return value on remove from a list with one element.");
            success = false;
        }

        list = new LinkedList();
        for (int i = 0; i < 100; i++) {
            list.addDocument(i);
            set.add(i);
        }
        set.remove(99);
        retVal = list.removeDocument(99);
        if (!list.toString().equals(setToString(set))) {
            System.err.println("Failed to remove last element in list.");
            success = false;
        }
        if (!retVal) {
            System.err.println("Bad return value on remove last element in list.");
            success = false;
        }

        set = new TreeSet<>();
        list = new LinkedList();
        for (int i = 0; i < 100; i++) {
            list.addDocument(i);
            set.add(i);
        }
        set.remove(0);
        retVal = list.removeDocument(0);
        if (!list.toString().equals(setToString(set))) {
            System.err.println("Failed to remove first element in list.");
            success = false;
        }
        if (!retVal) {
            System.err.println("Bad return value on remove first element in list.");
            success = false;
        }

        set = new TreeSet<>();
        list = new LinkedList();
        for (int i = 0; i < 100; i++) {
            list.addDocument(i);
            set.add(i);
        }
        for (int t = 0; t < 10; t++) {
            for (int i = 0; i < 100; i++) {
                Random prng = new Random();
                int n = prng.nextInt(100);
                if (set.contains(n)) {
                    set.remove(n);
                    retVal = list.removeDocument(n);
                    if (!list.toString().equals(setToString(set))) {
                        System.err.println("Failed to remove random element in list.");
                        success = false;
                    }
                    if (!retVal) {
                        System.err.println("Bad return value on remove random element in list.");
                        success = false;
                    }
                }
                else {
                    retVal = list.removeDocument(n);
                    if (!list.toString().equals(setToString(set))) {
                        System.err.println("Failed on remove nonexistent element in list.");
                        success = false;
                    }
                    if (retVal) {
                        System.err.println("Bad return value on remove nonexistent element in list.");
                        success = false;
                    }
                }
            }
        }
        return success;
    }

    private static boolean testMergeLists() {
        boolean success = true;
        Random prng = new Random();
        for (int t = 0; t < 25; t++) {
            TreeSet<Integer>set1 = new TreeSet<>();
            LinkedList list1 = new LinkedList();
            if (t != 1) {
                for (int i = 0; i < 100; i++) {
                    int n = prng.nextInt(500);
                    if (!set1.contains(n)) {
                        list1.addDocument(n);
                        set1.add(n);
                    }
                }
            }
            TreeSet<Integer>set2 = new TreeSet<>();
            LinkedList list2 = new LinkedList();
            if (t > 0) {
                for (int i = 0; i < 100; i++) {
                    int n = prng.nextInt(500);
                    if (!set1.contains(n) && !set2.contains(n)) {
                        list2.addDocument(n);
                        set2.add(n);
                    }
                }
            }
            set1.addAll(set2);
            list1.mergeList(list2);
            if (list2.first != null) {
                System.err.println("in mergeLists, other list is not correctly emptied.");
                success = false;
            }
            if (!list1.toString().equals(setToString(set1))) {
                System.err.println(list1.toString());
                System.err.println(setToString(set1));
                if (t == 0) {
                    System.err.println("mergeLists incorrectly merges if current list is empty.");
                }
                else if (t == 1) {
                    System.err.println("mergeLists incorrectly merges if other list is empty.");
                }
                else {
                    System.err.println("mergeLists incorrectly merges in general case.");
                }
                success = false;
            }
        }
        return success;
    }

    private static boolean testAndMerge() {
        boolean success = true;
        Random prng = new Random();
        for (int t = 0; t < 25; t++) {
            TreeSet<Integer>set1 = new TreeSet<>();
            LinkedList list1 = new LinkedList();
            if (t != 1) {
                for (int i = 0; i < 100; i++) {
                    int n = prng.nextInt(500);
                    if (!set1.contains(n)) {
                        list1.addDocument(n);
                        set1.add(n);
                    }
                }
            }
            TreeSet<Integer>set2 = new TreeSet<>();
            LinkedList list2 = new LinkedList();
            if (t > 0) {
                for (int i = 0; i < 100; i++) {
                    int n = prng.nextInt(500);
                    if (!set2.contains(n)) {
                        list2.addDocument(n);
                        set2.add(n);
                    }
                }
            }
            TreeSet<Integer> set = new TreeSet<>();
            for (Integer n: set1) {
                if (set2.contains(n)) set.add(n);
            }
            LinkedList list = list1.andMerge(list2);
            if (!list1.toString().equals(setToString(set1))) {
                System.err.println("andMerge incorrectly changes list1.");
                success = false;
            }
            if (!list2.toString().equals(setToString(set2))) {
                System.err.println("andMerge incorrectly changes list2.");
                success = false;
            }

            if (!list.toString().equals(setToString(set))) {
                System.err.println(list.toString());
                System.err.println(setToString(set));
                if (t == 0) {
                    System.err.println("andMerge incorrectly merges if current list is empty.");
                }
                else if (t == 1) {
                    System.err.println("andMerge incorrectly merges if other list is empty.");
                }
                else {
                    System.err.println("andMerge incorrectly merges in general case.");
                }
                success = false;
            }
        }
        return success;
    }

    private static boolean testOrMerge() {
        boolean success = true;
        Random prng = new Random();
        for (int t = 0; t < 25; t++) {
            TreeSet<Integer>set1 = new TreeSet<>();
            LinkedList list1 = new LinkedList();
            if (t != 1) {
                for (int i = 0; i < 100; i++) {
                    int n = prng.nextInt(500);
                    if (!set1.contains(n)) {
                        list1.addDocument(n);
                        set1.add(n);
                    }
                }
            }
            TreeSet<Integer>set2 = new TreeSet<>();
            LinkedList list2 = new LinkedList();
            if (t > 0) {
                for (int i = 0; i < 100; i++) {
                    int n = prng.nextInt(500);
                    if (!set2.contains(n)) {
                        list2.addDocument(n);
                        set2.add(n);
                    }
                }
            }
            LinkedList list = list1.orMerge(list2);
            if (!list1.toString().equals(setToString(set1))) {
                System.err.println("orMerge incorrectly changes list1.");
                success = false;
            }
            if (!list2.toString().equals(setToString(set2))) {
                System.err.println("orMerge incorrectly changes list2.");
                success = false;
            }
            set1.addAll(set2);
            if (!list.toString().equals(setToString(set1))) {
                System.err.println(list.toString());
                System.err.println(setToString(set1));
                if (t == 0) {
                    System.err.println("orMerge incorrectly merges if current list is empty.");
                }
                else if (t == 1) {
                    System.err.println("orMerge incorrectly merges if other list is empty.");
                }
                else {
                    System.err.println("orMerge incorrectly merges in general case.");
                }
                success = false;
            }
        }
        return success;
    }

    public static void main(String[] args) {
        if (!testAddDocument()) {
            System.err.println("Test of addDocument() failed");
        }
        if (!testRemoveDocument()) {
            System.err.println("Test of removeDocument() failed");
        }
        if (!testMergeLists()) {
            System.err.println("Test of mergeLists() failed");
        }
        if (!testAndMerge()) {
            System.err.println("Test of orMerge() failed");
        }
        if (!testOrMerge()) {
            System.err.println("Test of orMerge() failed");
        }
    }
}
