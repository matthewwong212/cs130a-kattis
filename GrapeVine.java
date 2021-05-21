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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

class GrapeVine {

	public static void main(String[] args) {
        Kattio io = new Kattio(System.in, System.out);

        int numPeople = io.getInt();
        int connections = io.getInt();
        int days = io.getInt();
        
        HashMap<String, Person> map = new HashMap<String, Person>();

        // Add people to the map
        // Added with name as the key, Person object as the value
        for (int i = 0; i < numPeople; i++) {
            String name = io.getWord();
            int skep = io.getInt();
            Person temp = new Person(name, skep);       // All new people start with spreadingRumor = false, and no neighbors
            map.put(name, temp);
        }

        // Create links
        for (int i = 0; i < connections; i++) {
            String name1 = io.getWord();
            String name2 = io.getWord();
            Person p1 = map.get(name1);
            Person p2 = map.get(name2);
            p1.addNeighbor(name2);
            p2.addNeighbor(name1);
        }

        // Queue used to keep track of which nodes to spread from
        Queue<String> spreadQueue;
        Queue<String> nextQueue = new LinkedList<>();

        // Set up initial person with the rumor
        String source = io.getWord();
        Person sourcePerson = map.get(source);
        sourcePerson.skepticism = 0;
        sourcePerson.spreadingRumor = true;
        sourcePerson.heardRumor = true;
        nextQueue.add(sourcePerson.name);


        // Iterate through days here
        int peopleHeard = 0;
        Person spreader;
        //HashMap<String, Boolean> alreadyAdded = new HashMap<String, Boolean>();
        //alreadyAdded.put(source, true);
        for (int i = 0; i < days; i++) {
            // Set spreading Queue to people from last iteration
            spreadQueue = new LinkedList<>(nextQueue);
            nextQueue.clear();

            // If queue is empty, it can't spread anymore
            if (spreadQueue.isEmpty()) {
                break;
            }

            // Go through entire queue
            while (!spreadQueue.isEmpty()) {
                // Remove from queue, spread to neighbors, then remove them from queue
                spreader = map.get(spreadQueue.poll());       // Get next person from queue, anyone here should be able to spread the rumor

                for (String neighbor : spreader.neighbors) {
                    HashMap<String, Boolean> addedToQueue = new HashMap<String, Boolean>();     // Used to check if person already added

                    Person temp = map.get(neighbor);
                    if (temp.spreadingRumor == true) {  // neighbor already spreading rumor, do nothing
                        continue;   // to next neighbor
                    }
                    if (!temp.heardRumor) {     // neighbor hasn't heard rumor
                        peopleHeard++;
                        temp.heardRumor = true;
                    }
                    // to reach this point, person will NOT be spreading rumor
                    temp.skepticism--;  // reduce skepticism
                    if (temp.skepticism < 1) {  // person has reduced skepticism enough to spread during the next day
                        // Check if person is already added to nextQueue (Does it solve test case 8?) NOPE, doesn't break the first 7 test cases tho\
                        if (!addedToQueue.containsKey(temp.name)) {  // person not already in queue
                            temp.spreadingRumor = true;
                            nextQueue.add(temp.name);       // add person to next spreading queue
                        }
                    }
                }
            }
        }

        io.println(peopleHeard);
        io.close();
	}
}

class Person {
    public boolean heardRumor;
    public boolean spreadingRumor;
    public String name;
    public Vector<String> neighbors;
    public int skepticism;

    public Person(String name, int skepticism) {
        this.heardRumor = false;
        this.spreadingRumor = false;
        this.name = name;
        this.neighbors = new Vector<String>();
        this.skepticism = skepticism;
    }

    public void addNeighbor(String newNeighbor) {
        neighbors.add(newNeighbor);
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
