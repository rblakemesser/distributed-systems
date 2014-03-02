import java.util.ArrayList;

public class LibraryServer {
    private DirectClock dc = null;
    private int myId = -1;
    private int timeToWait = 0;
    private int killCounter = 0;
    private BookDatabase bookDatabase;
    // Book list
    // Hashmap of <booknumber, status>?

    // Client list
    // Do we need to maintain this here?  For broadcasts of new info?
    private OtherServerList otherServers;

    public LibraryServer(ArrayList<String[]> input, int pid) {
        int numProcs = Integer.parseInt(input.get(0)[0]);
        int numBooks = Integer.parseInt(input.get(0)[0]);
        myId = pid;
        dc = new DirectClock(numProcs, pid);

        for (String[] line: input){
            // Add each line after the first to serverList

            // Start listeners for localhost lines


            // Set timeToWait and killCounter based on last line
            if (line[0].contains("s")){
                killCounter = Integer.parseInt(line[1]);
                timeToWait = Integer.parseInt(line[2]);
            }
        }
    }
}
