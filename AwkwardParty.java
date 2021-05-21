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

import java.util.ArrayList;
import java.util.List;

class AwkwardParty {

	public static void main(String[] args) {
        Kattio io = new Kattio(System.in, System.out);

        int awkwardLevel = io.getInt();     // intial value if no matching languages at all, gonna make martin's momma real happy
        int numGuests = awkwardLevel;
        PartyMap party = new PartyMap();

        for (int i = 0; i < numGuests; i++) {   // iterate through guests
            int newLevel = party.put(i, io.getInt());   // add to map, as well as get distance between matching language if any
            if (newLevel == -1) {   // new language
                continue;
            } else if (newLevel < awkwardLevel) {   // if less awkward, replace
                awkwardLevel = newLevel;
            }
        }

        io.println(awkwardLevel);
        io.close();
	}
}

// Can't use standard library, so create hash table here
class PartyMap {

    // pair of integers just to make code easier to keep track of than an array
    class Pair {
        int guest;
        int language;

        public Pair(int guest, int language) {
            this.guest = guest;
            this.language = language;
        }

        public int getGuest() {
            return guest;
        }

        public int getLanguage() {
            return language;
        }

        public void setGuest(int guest) {
            this.guest = guest;
        }
    }

    int mapSize = 10000;
    private Pair pair;
    private List<Pair>[] table;
    
    @SuppressWarnings("unchecked")
    public PartyMap() {
        this.table = new List[mapSize];
    }

    // Returns an int with the distance between the input "guest" and a guest (with lower index) with the same language
    // Returns -1 if no matching languages are found
    public int put(int guest, int language) {
        int hashValue = language % mapSize; // test different functions, but this should be fine for this problem

        if (table[hashValue] != null) {     // existing list
            for (Pair cur : table[hashValue]) {
                if (cur.getLanguage() == language) {    // same language
                    int existingGuest = cur.getGuest();
                    cur.setGuest(guest);                // update highest position of guest with same language
                    return guest - existingGuest;       // returns distance between new guest w/ same language, and last seen guest with same language
                } else {                // new language, but same hash value
                    pair = new Pair(guest, language);
                    table[hashValue].add(pair);
                    return -1;
                }
            }
        } else {        // no arraylist at hash value yet
            pair = new Pair(guest, language);
            table[hashValue] = new ArrayList<Pair>();
            table[hashValue].add(pair);
            return -1;
        }
        return -1;
    }
}


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

