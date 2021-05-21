// ------- Used for Kattio -------//
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStream;
// ------- Used for Kattio -------//
import java.util.Vector;

class KattisQuest {

	public static void main(String[] args) {
        Kattio io = new Kattio(System.in, System.out);

        int numCommands = io.getInt();
        long totalGold = 0;
        int goldVal, energyVal;
        String command = "";
        AVLTree quests = new AVLTree();
        MaxHeap heapCur;

        for (int i = 0; i < numCommands; i++) {
            command = io.getWord();
            if (command.equals("add")) {    // add quest
                energyVal = io.getInt();
                goldVal = io.getInt();
                if (quests.hasKey(energyVal) == false) {
                    MaxHeap temp = new MaxHeap();
                    temp.insert(goldVal);
                    quests.insert(energyVal, temp);
                } else {
                    heapCur = quests.getHeap(energyVal);
                    heapCur.insert(goldVal);
                }
            } else if (command.equals("query")) {
                int availableEnergy = io.getInt();
                while (true) {
                    int nextEnergy = quests.greatestEnergyAvailable(availableEnergy);
                    if (nextEnergy == -1) { // no more keys
                        break;
                    } else {
                        heapCur = quests.getHeap(nextEnergy);
                        totalGold = totalGold + heapCur.pop();
                        availableEnergy = availableEnergy - nextEnergy;
                        if (heapCur.isEmpty()) {
                            quests.remove(nextEnergy);
                        }
                    }
                }
                io.println(totalGold);
                totalGold = 0;
            }
        }
        io.close();
	}
}

class MaxHeap {
    private Vector<Integer> maxHeap;

    public MaxHeap() {
        this.maxHeap = new Vector<Integer>();
    }

    public void insert(int val) {
        maxHeap.add(val);
        int cur = maxHeap.size() - 1;
        heapifyUp(cur);
    }

    public int getHighest() {
        return maxHeap.get(0);
    }

    public int getSize() {
        return maxHeap.size();
    }

    public boolean isEmpty() {
        return maxHeap.isEmpty();
    }

    // should only be called if heap isn't empty
    public int pop() {
        if (maxHeap.isEmpty()) {
            System.out.println("Hey dum dum you tried to pop from a empty heap");
            return -9999;
        }

        int largest = maxHeap.get(0);

        maxHeap.set(0, maxHeap.lastElement());  // replace root with lowest in tree
        maxHeap.remove(maxHeap.size() - 1);     // remove lowest in tree (why doesn't lastElement() work?!?!?!?)

        heapifyDown(0);

        return largest;
    }

    private int getParent(int cur) {
        if (cur == 0) {
            return 0;
        } else {
            return (cur - 1) / 2;
        }
    }

    private int getLeftChild(int cur) {
        return (2 * cur + 1);
    }

    private int getRightChild(int cur) {
        return (2 * cur + 2);
    }

    private void swap(int ndxA, int ndxB) {
        int temp = maxHeap.get(ndxA);
        maxHeap.set(ndxA, maxHeap.get(ndxB));
        maxHeap.set(ndxB, temp);
    }

    private void heapifyDown(int cur) {
        int currentLargest = cur;

        // value on left, get larger of two
        if (getLeftChild(cur) < maxHeap.size()) {
            if (maxHeap.get(currentLargest) < maxHeap.get(getLeftChild(cur)) ) {
                currentLargest = getLeftChild(cur);
            }
        }

        // value on right, get larger of three
        if (getRightChild(cur) < maxHeap.size()) {
            if (maxHeap.get(currentLargest) < maxHeap.get(getRightChild(cur))) {
                currentLargest = getRightChild(cur);
            }
        }

        // violates heap property
        if (currentLargest != cur) {
            swap(cur, currentLargest);
            heapifyDown(currentLargest);
        }
    }

    private void heapifyUp(int cur) {
        if (cur == 0) {     // reached root, new largest value
            return;
        }

        if (maxHeap.get(cur) > maxHeap.get(getParent(cur))) {   // new value larger than parent, move up
            swap (cur, getParent(cur));
            heapifyUp(getParent(cur));
        }

    }
}

// Can't use standard library, so create BST here
// Node for each maxheap
class Node {
    Node left, right;
    MaxHeap heap;
    int height;
    int energy;

    public Node(int energy, MaxHeap h) {
        this.left = null;
        this.right = null;
        this.energy = energy;
        this.heap = h;
        height = 1;
    }
}

class AVLTree {
    private Node root;

    public AVLTree() {
        root = null;
    }

    public void clear() {
        root = null;
    }

    public boolean empty() {
        if (root == null) {
            return true;
        } else {
            return false;
        }
    }

    public void insert(int energy, MaxHeap m) {
        this.root = insert(energy, m, root);
    }

    // Should not have any duplicates, duplicates are handled in the main functions
    private Node insert(int energy, MaxHeap m, Node n) {
        if (n == null) {    // reached leaf, create new node
            Node temp = new Node(energy, m);
            return temp;
        } else if (energy > n.energy) { //target value is larger, move right
            n.right = insert(energy, m, n.right);
        } else if (energy < n.energy) { // target value is smaller, move left
            n.left = insert(energy, m, n.left);
        } else {
            System.out.println("Shoudln't reach here dumb dum");
        }

        n.height = updateHeight(n);

        // Balance
        if ( (height(n.left) - height(n.right)) > 1 && energy < n.left.energy ) {   // LL Rotation
            return rotateRight(n);
        }
        if ( (height(n.right) - height(n.left)) > 1 && energy > n.right.energy ) {  // RR Rotation
            return rotateLeft(n);
        }
        if ( (height(n.left) - height(n.right)) > 1 && energy > n.left.energy ) {   // LR Rotation
            n.left = rotateLeft(n.left);
            return rotateRight(n);
        }
        if ( (height(n.right) - height(n.left)) > 1 && energy < n.right.energy ) {  // RL rotation
            n.right = rotateRight(n.right);
            return rotateLeft(n);
        }
    
        return n;   // already balanced

    }

    public int greatestEnergyAvailable(int energy) {
        return greatestEnergyAvailable(energy, this.root);
    }

    // returns largest energy level under target
    // returns -1 if none existing
    public int greatestEnergyAvailable(int energy, Node n) {
        if (n == null) {
            return -1;
        } else if (n.energy == energy) {
            return energy;
        } else if (n.energy < energy) { // move right
            int temp = greatestEnergyAvailable(energy, n.right);    // get highest in larger subtree
            if (temp == -1) {
                // nothing found, return last known highest
                return n.energy;
            } else {
                // found something higher
                return temp;
            }
        } else if (n.energy > energy) {     // move left, will be lower than current, or nothing
            return greatestEnergyAvailable(energy, n.left);
        }

        System.out.println("WHY ARE YOU HERE");
        return -1;  // shouldn't reach here, just makes java happy
    }

    public boolean hasKey(int energy) {
        Node temp = find(energy, this.root);
        return temp != null;
    }

    // returns null if not found, matching maxHeap if found.
    // should not ever return null, checking if it exists should be done in main
    public MaxHeap getHeap(int energy) {
        Node temp = find(energy, this.root);
        if (temp == null) {
            return null;
        } else {
            return temp.heap;
        }
    }

    // returns null if not found, returns node of target if found
    private Node find(int targetEnergy, Node n) {
        if (n == null) {    // not found
            return null;
        } else if (n.energy == targetEnergy) {  // found target, return node
            return n;
        }

        if (n.energy < targetEnergy) {
            //move right
            return find(targetEnergy, n.right);
        } else {
            // move left
            return find(targetEnergy, n.left);
        }

    }

    // public helper to remove
    public void remove(int energy) {
        this.root = remove(energy, this.root);
    }

    // removes node, should not be called on a non existing energy value, should only be called by public helper
    // Public helper called by main, should only be called if ensured corresponding maxheap is empty
    private Node remove(int energy, Node n) {
        if (n == null) {
            return n;
        }
        if (energy < n.energy) {    // target less than cursor, move left
            n.left = remove(energy, n.left);
        } else if (energy > n.energy) { // target more than cursor, move right
            n.right = remove(energy, n.right);
        } else {    // node found
            if (n.left==null || n.right==null) {    // at least one missing child
                Node child = null;
                if (n.left == null) {
                    child = n.right;
                } else {
                    child = n.left;
                }

                if (child != null) {    // has one child, replace
                    n = child;
                } else {    // no children, 
                    child = n;
                    n = null;
                }
            } else {    // both children exist, find smaller and swap
                Node smallest = getSmallest(n.right);
                n.energy = smallest.energy;
                n.heap = smallest.heap;
                n.right = remove(smallest.energy, n.right);
            }  
        }

        if (n == null) {    // one node
            return n;
        }

        n.height = updateHeight(n);

        // Balance
        int nBal = balanceFactor(n);

        if (nBal > 1 && balanceFactor(n.left) >= 0) {   // LL rotation
            return rotateRight(n);
        }
        if (nBal < -1 && balanceFactor(n.right) <= 0) { // RR rotation
            return rotateLeft(n);
        }
        if (nBal > 1 && balanceFactor(n.left) < 0) {    // LR Rotation
            n.left = rotateLeft(n.left);
            return rotateRight(n);
        }
        if (nBal < -1 && balanceFactor(n.right) > 0) {  // RL rotation
            n.right = rotateRight(n.right);
            return rotateLeft(n);
        }
        return n;
    }

    int balanceFactor(Node n) {
        if (n == null) {
            return 0;
        } else {
            return height(n.left) - height(n.right);
        }
    }

    private int updateHeight(Node n) {
        return Math.max(height(n.left), height(n.right)) + 1;
    }

    // Smallest node in tree for removing with 2 children
    private Node getSmallest(Node n) {
        Node cur = n;

        while (cur.left != null) {
            cur = cur.left;
        }
        return cur;
    }

    private int height(Node n) {
        if (n == null) {
            return 0;
        } else {
            return n.height;
        }
    }

    private Node rotateRight(Node n) {
        Node temp1 = n.left;
        Node temp2 = temp1.right;       // double check correctness
        temp1.right = n;
        n.left = temp2;

        // change height
        n.height = updateHeight(n);
        temp1.height = updateHeight(temp1);

        return temp1;
    }

    private Node rotateLeft(Node n) {
        Node temp1 = n.right;  
        Node temp2 = temp1.left;        // double check correctness
        temp1.left = n;
        n.right = temp2;

        // change height
        n.height = updateHeight(n);
        temp1.height = updateHeight(temp1);

        return temp1;
    }
}

// Below used for Kattio
class Kattio extends PrintWriter {
    public Kattio(InputStream i) {
        super(new BufferedOutputStream(System.out));
        r = new BufferedReader(new InputStreamReader(i));
    }
    public Kattio(InputStream i, OutputStream o) {
        super(new BufferedOutputStream(o));
        r = new BufferedReader(new InputStreamReader(i));
    }

    public boolean hasMoreTokens() {
        return peekToken() != null;
    }

    public int getInt() {
        return Integer.parseInt(nextToken());
    }

    public double getDouble() {
        return Double.parseDouble(nextToken());
    }

    public long getLong() {
        return Long.parseLong(nextToken());
    }

    public String getWord() {
        return nextToken();
    }



    private BufferedReader r;
    private String line;
    private StringTokenizer st;
    private String token;

    private String peekToken() {
        if (token == null)
            try {
                while (st == null || !st.hasMoreTokens()) {
                    line = r.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line);
                }
                token = st.nextToken();
            } catch (IOException e) { }
        return token;
    }

    private String nextToken() {
        String ans = peekToken();
        token = null;
        return ans;
    }
}
